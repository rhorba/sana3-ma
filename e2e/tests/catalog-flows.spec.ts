import { test, expect } from '@playwright/test';

// Covers Sprint 2's critical flows (Stories 3.1-3.4, 4.1-4.5) as one continuous session so
// Playwright's video recording produces a single file per rule 9
// (.recordings/v0.2-2026-07-11.webm).

const runId = Date.now();
const artisanEmail = `e2e-catalog-artisan-${runId}@example.com`;
const password = 'TestPass123!';
const productName = `E2E Zellige Tile Set ${runId}`;

test('catalog critical user flows', async ({ page }) => {
  await test.step('Register an artisan and complete their profile', async () => {
    await page.goto('/register');
    await page.getByLabel('Email').fill(artisanEmail);
    await page.getByLabel('Password').fill(password);
    await page.getByRole('radio', { name: 'Artisan' }).check();
    await page.getByRole('button', { name: 'Register' }).click();

    await expect(page).toHaveURL(/\/profile$/);
    await page.getByLabel('Display name').fill('E2E Catalog Artisan');
    await page.getByLabel('Craft type').fill('Pottery');
    await page.getByLabel('Region').fill('Fes');
    await page.getByRole('button', { name: 'Save' }).click();
    await expect(page.getByText('Profile updated')).toBeVisible();
  });

  await test.step('Add a product', async () => {
    await page.goto('/profile/products');
    await expect(page.getByText("You haven't added any products yet.")).toBeVisible();

    await page.getByLabel('Name').fill(productName);
    await page.getByLabel('Description').fill('Handmade blue zellige tiles, added by the E2E suite.');
    await page.getByLabel('Price').fill('450');
    await page.getByLabel('Craft type').fill('Pottery');
    await page.getByRole('button', { name: 'Add product' }).click();

    await expect(page.getByText('Product added')).toBeVisible();
    await expect(page.locator('.product-card', { hasText: productName })).toBeVisible();
  });

  await test.step('Edit the product', async () => {
    const card = page.locator('.product-card', { hasText: productName });
    await card.getByRole('button', { name: 'Edit' }).click();

    await expect(page.getByRole('heading', { name: 'Edit product' })).toBeVisible();
    await page.getByLabel('Price').fill('500');
    await page.getByRole('button', { name: 'Save changes' }).click();

    await expect(page.getByText('Product updated')).toBeVisible();
    await expect(page.locator('.product-card', { hasText: productName })).toContainText('500 MAD');
  });

  await test.step('The product is publicly browsable and filterable', async () => {
    await page.goto('/browse');
    await expect(page.locator('.product-card', { hasText: productName })).toBeVisible();

    await page.getByLabel('Craft type').fill('nonexistent-craft-type');
    await page.getByRole('button', { name: 'Search' }).click();
    await expect(page.getByText('No products match your search.')).toBeVisible();

    await page.getByLabel('Craft type').fill('Pottery');
    await page.getByRole('button', { name: 'Search' }).click();
    await expect(page.locator('.product-card', { hasText: productName })).toBeVisible();
  });

  await test.step('Product detail page shows full detail and the artisan summary', async () => {
    await page.locator('.product-card', { hasText: productName }).getByRole('link').first().click();

    await expect(page).toHaveURL(/\/products\/[0-9a-f-]+$/);
    await expect(page.getByRole('heading', { name: productName })).toBeVisible();
    await expect(page.getByText('500 MAD — Pottery')).toBeVisible();
    await expect(page.getByText('Sold by E2E Catalog Artisan')).toBeVisible();
  });

  await test.step('An unknown product id shows the not-found state, not an error', async () => {
    await page.goto('/products/00000000-0000-0000-0000-000000000000');
    await expect(page.getByText("This product couldn't be found. It may have been removed.")).toBeVisible();
  });

  await test.step('Delete the product removes it from both the owner list and public browsing', async () => {
    page.once('dialog', (dialog) => dialog.accept());

    await page.goto('/profile/products');
    await page.locator('.product-card', { hasText: productName }).getByRole('button', { name: 'Delete' }).click();

    await expect(page.getByText('Product deleted')).toBeVisible();
    await expect(page.locator('.product-card', { hasText: productName })).toHaveCount(0);

    await page.goto('/browse');
    await expect(page.locator('.product-card', { hasText: productName })).toHaveCount(0);
  });
});
