import { test, expect } from '@playwright/test';

// Covers Sprint 4's critical flows (Stories 7.1-7.4) as one continuous session so
// Playwright's video recording produces a single file per rule 9
// (.recordings/v0.4-2026-07-13.webm).

const runId = Date.now();
const ownerEmail = `e2e-coop-owner-${runId}@example.com`;
const memberEmail = `e2e-coop-member-${runId}@example.com`;
const password = 'TestPass123!';
const coopName = `E2E Coop ${runId}`;
const ownerProductName = `E2E Owner Product ${runId}`;
const memberProductName = `E2E Member Product ${runId}`;

test('cooperative membership critical user flows', async ({ page }) => {
  await test.step('Register the owner, create the cooperative profile and a product', async () => {
    await page.goto('/register');
    await page.getByLabel('Email').fill(ownerEmail);
    await page.getByLabel('Password').fill(password);
    await page.getByRole('radio', { name: 'Artisan' }).check();
    await page.getByRole('button', { name: 'Register' }).click();

    await expect(page).toHaveURL(/\/profile$/);
    await page.getByLabel('Display name').fill(coopName);
    await page.getByLabel('Craft type').fill('Weaving');
    await page.getByLabel('Region').fill('Fes');
    await page.getByRole('button', { name: 'Save' }).click();
    await expect(page.getByText('Profile updated')).toBeVisible();

    await page.goto('/profile/products');
    await page.getByLabel('Name').fill(ownerProductName);
    await page.getByLabel('Price').fill('100');
    await page.getByLabel('Craft type').fill('Weaving');
    await page.getByRole('button', { name: 'Add product' }).click();
    await expect(page.locator('.product-card', { hasText: ownerProductName })).toBeVisible();

    await page.getByRole('button', { name: 'Log Out' }).click();
  });

  await test.step('Register the future member first — an invite can only target an existing ARTISAN account', async () => {
    await page.goto('/register');
    await page.getByLabel('Email').fill(memberEmail);
    await page.getByLabel('Password').fill(password);
    await page.getByRole('radio', { name: 'Artisan' }).check();
    await page.getByRole('button', { name: 'Register' }).click();
    await expect(page).toHaveURL(/\/profile$/);

    await page.getByRole('button', { name: 'Log Out' }).click();
  });

  await test.step('Owner logs back in and invites the member by email', async () => {
    await page.goto('/login');
    await page.getByLabel('Email').fill(ownerEmail);
    await page.getByLabel('Password').fill(password);
    await page.getByRole('button', { name: 'Log In' }).click();
    await expect(page).toHaveURL(/\/profile$/);

    await page.goto('/profile/members');
    await expect(page.locator('.member-card', { hasText: ownerEmail })).toContainText('OWNER');

    await page.getByLabel('Invite an artisan by email').fill(memberEmail);
    await page.getByRole('button', { name: 'Send Invite' }).click();
    await expect(page.getByText('Invite sent')).toBeVisible();

    await page.getByRole('button', { name: 'Log Out' }).click();
  });

  await test.step('The member sees the pending-invite banner right after logging in and accepts it', async () => {
    await page.goto('/login');
    await page.getByLabel('Email').fill(memberEmail);
    await page.getByLabel('Password').fill(password);
    await page.getByRole('button', { name: 'Log In' }).click();
    await expect(page).toHaveURL(/\/profile$/);

    const banner = page.locator('.invite-banner');
    await expect(banner).toContainText(`${coopName} invited you to join.`);

    await banner.getByRole('button', { name: 'Accept' }).click();
    await expect(banner).toHaveCount(0);
  });

  await test.step('Members list now shows both users, and the member can manage the shared profile', async () => {
    await page.goto('/profile/members');
    await expect(page.locator('.member-card', { hasText: ownerEmail })).toContainText('OWNER');
    const memberRow = page.locator('.member-card', { hasText: memberEmail });
    await expect(memberRow).toContainText('MEMBER');
    // A MEMBER isn't the owner, so no invite form should be visible to them.
    await expect(page.getByLabel('Invite an artisan by email')).toHaveCount(0);

    await page.goto('/profile/products');
    await expect(page.locator('.product-card', { hasText: ownerProductName })).toBeVisible();
    await page.getByLabel('Name').fill(memberProductName);
    await page.getByLabel('Price').fill('50');
    await page.getByLabel('Craft type').fill('Weaving');
    await page.getByRole('button', { name: 'Add product' }).click();
    await expect(page.locator('.product-card', { hasText: memberProductName })).toBeVisible();

    await page.getByRole('button', { name: 'Log Out' }).click();
  });

  await test.step('Owner sees the product the member added to their shared profile', async () => {
    await page.goto('/login');
    await page.getByLabel('Email').fill(ownerEmail);
    await page.getByLabel('Password').fill(password);
    await page.getByRole('button', { name: 'Log In' }).click();
    await expect(page).toHaveURL(/\/profile$/);

    await page.goto('/profile/products');
    await expect(page.locator('.product-card', { hasText: memberProductName })).toBeVisible();
  });

  await test.step('Owner removes the member from the cooperative', async () => {
    await page.goto('/profile/members');
    const memberRow = page.locator('.member-card', { hasText: memberEmail });
    page.once('dialog', (dialog) => dialog.accept());
    await memberRow.getByRole('button', { name: 'Remove' }).click();

    await expect(page.locator('.member-card', { hasText: memberEmail })).toHaveCount(0);
    await expect(page.locator('.member-card', { hasText: ownerEmail })).toBeVisible();

    await page.getByRole('button', { name: 'Log Out' }).click();
  });

  await test.step('The removed member has lost access to the cooperative profile', async () => {
    await page.goto('/login');
    await page.getByLabel('Email').fill(memberEmail);
    await page.getByLabel('Password').fill(password);
    await page.getByRole('button', { name: 'Log In' }).click();
    await expect(page).toHaveURL(/\/profile$/);

    // No membership left -> GetArtisanProfileHandler 404s -> the frontend treats this as the
    // normal "no profile yet" empty state, not an error (same as a brand-new artisan).
    await expect(page.getByText('Complete your profile so buyers can find you.')).toBeVisible();
  });
});
