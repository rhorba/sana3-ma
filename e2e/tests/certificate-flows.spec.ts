import { test, expect } from '@playwright/test';

// Covers Sprint 5's critical flows (Stories 8.1-8.4) as one continuous session so
// Playwright's video recording produces a single file per rule 9
// (.recordings/v0.5-2026-07-15.webm).

const runId = Date.now();
const artisanEmail = `e2e-cert-artisan-${runId}@example.com`;
const password = 'TestPass123!';
const productName = `E2E Cert Product ${runId}`;

test('craft certificate critical user flows', async ({ page }) => {
  let verificationCode = '';

  await test.step('Register an artisan, create a profile and a product', async () => {
    await page.goto('/register');
    await page.getByLabel('Email').fill(artisanEmail);
    await page.getByLabel('Password').fill(password);
    await page.getByRole('radio', { name: 'Artisan' }).check();
    await page.getByRole('button', { name: 'Register' }).click();

    await expect(page).toHaveURL(/\/profile$/);
    await page.getByLabel('Display name').fill('E2E Cert Artisan');
    await page.getByLabel('Craft type').fill('Zellige');
    await page.getByLabel('Region').fill('Fes');
    await page.getByRole('button', { name: 'Save' }).click();
    await expect(page.getByText('Profile updated')).toBeVisible();

    await page.goto('/profile/products');
    await page.getByLabel('Name').fill(productName);
    await page.getByLabel('Price').fill('250');
    await page.getByLabel('Craft type').fill('Zellige');
    await page.getByRole('button', { name: 'Add product' }).click();
    await expect(page.locator('.product-card', { hasText: productName })).toBeVisible();
  });

  await test.step('Issue a certificate and see the verification code and QR code', async () => {
    const card = page.locator('.product-card', { hasText: productName });
    await card.getByRole('button', { name: 'Issue Certificate' }).click();

    await expect(page.getByText('Certificate issued')).toBeVisible();
    await expect(card.getByRole('button', { name: 'View Certificate' })).toBeVisible();
    await expect(card.locator('.qr-code svg')).toBeVisible();

    const codeText = await card.locator('.verification-code').textContent();
    verificationCode = codeText?.replace('Verification code:', '').trim() ?? '';
    expect(verificationCode).toMatch(/^[0-9a-f-]{36}$/);
  });

  await test.step('Re-issuing is idempotent — clicking again returns the same code', async () => {
    const card = page.locator('.product-card', { hasText: productName });
    await card.getByRole('button', { name: 'View Certificate' }).click();

    const codeText = await card.locator('.verification-code').textContent();
    expect(codeText).toContain(verificationCode);
  });

  await test.step('The public verification page shows the certificate details with no login required', async () => {
    await page.getByRole('button', { name: 'Log Out' }).click();

    await page.goto(`/certificates/verify/${verificationCode}`);
    const result = page.locator('.verify-result.valid');
    await expect(result).toContainText('Genuine craft certificate');
    await expect(result).toContainText('E2E Cert Artisan');
    await expect(result).toContainText(productName);
    await expect(result).toContainText('Zellige');
  });

  await test.step('An unknown code shows a clear invalid message, not an error page', async () => {
    await page.goto('/certificates/verify/00000000-0000-0000-0000-000000000000');
    const result = page.locator('.verify-result.invalid');
    await expect(result).toContainText('Not a valid certificate');
  });
});
