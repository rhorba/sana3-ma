import { test, expect, request as playwrightRequest } from '@playwright/test';

// Covers the 4 ATDD scenarios from docs/test-strategy-sana3-ma.md #3, as one continuous
// session so Playwright's video recording produces a single file per rule 9
// (.recordings/v0.1-2026-07-10.webm).

const API_BASE_URL = process.env.E2E_API_BASE_URL ?? 'http://localhost:8081/api/v1';
const runId = Date.now();
const artisanEmail = `e2e-artisan-${runId}@example.com`;
const buyerEmail = `e2e-buyer-${runId}@example.com`;
const password = 'TestPass123!';

test('critical user flows', async ({ page }) => {
  await test.step('Successful artisan registration -> auto-login -> profile empty state', async () => {
    await page.goto('/register');
    await page.getByLabel('Email').fill(artisanEmail);
    await page.getByLabel('Password').fill(password);
    await page.getByRole('radio', { name: 'Artisan' }).check();
    await page.getByRole('button', { name: 'Register' }).click();

    await expect(page).toHaveURL(/\/profile$/);
    await expect(page.getByText('Complete your profile so buyers can find you.')).toBeVisible();
  });

  await test.step('Artisan updates their profile', async () => {
    await page.getByLabel('Display name').fill('Atelier E2E');
    await page.getByLabel('Craft type').fill('Weaving');
    await page.getByLabel('Region').fill('Marrakech');
    await page.getByLabel('Bio').fill('Created by the Sprint 1 E2E suite.');
    await page.getByLabel('Phone').fill('+212600000001');
    await page.getByRole('button', { name: 'Save' }).click();

    await expect(page.getByText('Profile updated')).toBeVisible();

    await page.reload();
    await expect(page.getByLabel('Display name')).toHaveValue('Atelier E2E');
  });

  await test.step('Logout', async () => {
    await page.getByRole('button', { name: 'Log Out' }).click();
    await expect(page).toHaveURL(/\/$/);
  });

  await test.step('Login with wrong password shows a generic error and does not log in', async () => {
    await page.goto('/login');
    await page.getByLabel('Email').fill(artisanEmail);
    await page.getByLabel('Password').fill('WrongPassword123!');
    await page.getByRole('button', { name: 'Log In' }).click();

    await expect(page.getByText('Invalid email or password')).toBeVisible();
    await expect(page).toHaveURL(/\/login$/);
  });

  await test.step('Login with correct password succeeds', async () => {
    await page.getByLabel('Password').fill(password);
    await page.getByRole('button', { name: 'Log In' }).click();

    await expect(page).toHaveURL(/\/profile$/);
    await expect(page.getByLabel('Display name')).toHaveValue('Atelier E2E');
  });

  await test.step('Buyer cannot access artisan profile edit (403 at the API, guard in the UI)', async () => {
    await page.getByRole('button', { name: 'Log Out' }).click();

    await page.goto('/register');
    await page.getByLabel('Email').fill(buyerEmail);
    await page.getByLabel('Password').fill(password);
    await page.getByRole('radio', { name: 'Buyer' }).check();
    await page.getByRole('button', { name: 'Register' }).click();

    await expect(page).toHaveURL(/\/$/);

    // UI-level defense: the artisan guard bounces a direct navigation attempt.
    await page.goto('/profile');
    await expect(page).toHaveURL(/\/$/);

    // API-level enforcement: the literal ATDD scenario ("request the artisan profile edit
    // endpoint" -> "403 Forbidden"), independent of the frontend guard.
    const api = await playwrightRequest.newContext();
    const loginResponse = await api.post(`${API_BASE_URL}/auth/login`, {
      data: { email: buyerEmail, password },
    });
    expect(loginResponse.status()).toBe(200);
    const { accessToken } = await loginResponse.json();

    const upsertResponse = await api.put(`${API_BASE_URL}/artisan-profiles/me`, {
      headers: { Authorization: `Bearer ${accessToken}` },
      data: {
        displayName: 'Should Not Be Created',
        craftType: 'N/A',
        region: 'N/A',
        bio: '',
        contactPhone: '',
      },
    });
    expect(upsertResponse.status()).toBe(403);
    await api.dispose();
  });
});
