import { test, expect } from '@playwright/test';

// Covers Sprint 3's critical flows (Stories 5.1-5.3, 6.1-6.5) as one continuous session so
// Playwright's video recording produces a single file per rule 9
// (.recordings/v0.3-2026-07-12.webm).

const runId = Date.now();
const artisanEmail = `e2e-order-artisan-${runId}@example.com`;
const buyerEmail = `e2e-order-buyer-${runId}@example.com`;
const password = 'TestPass123!';
const productName = `E2E Order Flow Product ${runId}`;
const doomedProductName = `E2E Doomed Product ${runId}`;

// The local dev database accumulates products across many sessions, so /browse can span
// multiple pages — search by the run-unique name instead of assuming page 1.
async function searchBrowseFor(page: import('@playwright/test').Page, name: string) {
  await page.goto('/browse');
  await page.getByLabel('Search').fill(name);
  await page.getByRole('button', { name: 'Search' }).click();
  await expect(page.locator('.product-card', { hasText: name })).toBeVisible();
}

test('order critical user flows', async ({ page }) => {
  await test.step('Register an artisan, complete their profile, and add two products', async () => {
    await page.goto('/register');
    await page.getByLabel('Email').fill(artisanEmail);
    await page.getByLabel('Password').fill(password);
    await page.getByRole('radio', { name: 'Artisan' }).check();
    await page.getByRole('button', { name: 'Register' }).click();

    await expect(page).toHaveURL(/\/profile$/);
    await page.getByLabel('Display name').fill('E2E Order Artisan');
    await page.getByLabel('Craft type').fill('Pottery');
    await page.getByLabel('Region').fill('Fes');
    await page.getByRole('button', { name: 'Save' }).click();
    await expect(page.getByText('Profile updated')).toBeVisible();

    await page.goto('/profile/products');
    await page.getByLabel('Name').fill(productName);
    await page.getByLabel('Price').fill('100');
    await page.getByLabel('Craft type').fill('Pottery');
    await page.getByRole('button', { name: 'Add product' }).click();
    await expect(page.locator('.product-card', { hasText: productName })).toBeVisible();

    await page.getByLabel('Name').fill(doomedProductName);
    await page.getByLabel('Price').fill('50');
    await page.getByLabel('Craft type').fill('Pottery');
    await page.getByRole('button', { name: 'Add product' }).click();
    await expect(page.locator('.product-card', { hasText: doomedProductName })).toBeVisible();

    await page.getByRole('button', { name: 'Log Out' }).click();
  });

  await test.step('Register a buyer', async () => {
    await page.goto('/register');
    await page.getByLabel('Email').fill(buyerEmail);
    await page.getByLabel('Password').fill(password);
    await page.getByRole('radio', { name: 'Buyer' }).check();
    await page.getByRole('button', { name: 'Register' }).click();
    await expect(page).toHaveURL(/\/$/);
  });

  await test.step('Add the product to the cart from its detail page with a chosen quantity', async () => {
    await searchBrowseFor(page, productName);
    await page.locator('.product-card', { hasText: productName }).getByRole('link').first().click();
    await expect(page).toHaveURL(/\/products\/[0-9a-f-]+$/);

    await page.getByLabel('Qty').fill('2');
    await page.getByRole('button', { name: 'Add to Cart' }).click();

    await expect(page.getByRole('link', { name: /Cart \(2\)/ })).toBeVisible();
  });

  await test.step('Cart page shows the item, quantity is editable, total updates', async () => {
    await page.goto('/cart');
    const item = page.locator('.cart-item', { hasText: productName });
    await expect(item).toBeVisible();
    await expect(item).toContainText('200 MAD');

    await item.getByLabel('Qty').fill('3');
    await item.getByLabel('Qty').blur();
    await expect(item).toContainText('300 MAD');
    await expect(page.locator('.cart-totals')).toContainText('300 MAD');
  });

  await test.step('Checkout places an order and clears the cart', async () => {
    await page.getByRole('link', { name: 'Proceed to Checkout' }).click();
    await expect(page).toHaveURL(/\/checkout$/);
    await expect(page.locator('.review-list')).toContainText(`${productName} × 3`);

    await page.getByLabel('Shipping address').fill('123 Rue Example, Fes');
    await page.getByRole('button', { name: 'Place order' }).click();

    await expect(page.getByText('Order placed!')).toBeVisible();
    await expect(page.getByRole('link', { name: /Cart \(/ })).toHaveCount(0);
  });

  await test.step('Buyer order history shows the order and can cancel it', async () => {
    await page.goto('/orders');
    const order = page.locator('.order-card', { hasText: productName }).first();
    await expect(order).toContainText('PLACED');
    await expect(order).toContainText('300 MAD');

    page.once('dialog', (dialog) => dialog.accept());
    await order.getByRole('button', { name: 'Cancel order' }).click();

    await expect(order).toContainText('CANCELLED');
    await expect(order.getByRole('button', { name: 'Cancel order' })).toHaveCount(0);
  });

  await test.step('Place a second order (for the artisan fulfillment step)', async () => {
    await searchBrowseFor(page, productName);
    await page.locator('.product-card', { hasText: productName }).getByRole('button', { name: 'Add to Cart' }).click();

    await page.goto('/checkout');
    await page.getByLabel('Shipping address').fill('456 Ave Example, Rabat');
    await page.getByRole('button', { name: 'Place order' }).click();
    await expect(page.getByText('Order placed!')).toBeVisible();
  });

  await test.step('Cart survives logging out (client-side only, not tied to the session)', async () => {
    await searchBrowseFor(page, doomedProductName);
    await page.locator('.product-card', { hasText: doomedProductName }).getByRole('button', { name: 'Add to Cart' }).click();
    await expect(page.getByRole('link', { name: /Cart \(1\)/ })).toBeVisible();

    await page.getByRole('button', { name: 'Log Out' }).click();
    await page.goto('/login');
    await page.getByLabel('Email').fill(artisanEmail);
    await page.getByLabel('Password').fill(password);
    await page.getByRole('button', { name: 'Log In' }).click();
    await expect(page).toHaveURL(/\/profile$/);

    // Simulate "the product changed since it was added to the cart" (Story 5.2 AC).
    await page.goto('/profile/products');
    page.once('dialog', (dialog) => dialog.accept());
    await page
      .locator('.product-card', { hasText: doomedProductName })
      .getByRole('button', { name: 'Delete' })
      .click();
    await expect(page.getByText('Product deleted')).toBeVisible();

    await page.getByRole('button', { name: 'Log Out' }).click();
    await page.goto('/login');
    await page.getByLabel('Email').fill(buyerEmail);
    await page.getByLabel('Password').fill(password);
    await page.getByRole('button', { name: 'Log In' }).click();
    // Wait for the login to actually complete before the next step does a hard navigation —
    // otherwise it can abort the in-flight login request (access token is memory-only, so a
    // full page navigation relies on the just-set refresh cookie already being there).
    await expect(page).toHaveURL(/\/$/);

    await expect(page.getByRole('link', { name: /Cart \(1\)/ })).toBeVisible();
  });

  await test.step('A per-line rejection at checkout is shown clearly, not a generic failure', async () => {
    await page.goto('/checkout');
    await page.getByLabel('Shipping address').fill('Address');
    await page.getByRole('button', { name: 'Place order' }).click();

    await expect(page.getByText(/No product found for id/)).toBeVisible();
    // The cart is untouched by a failed checkout.
    await expect(page.locator('.review-list')).toContainText(doomedProductName);
  });

  await test.step('Artisan sees incoming order items with buyer/shipping info and can mark one completed', async () => {
    await page.getByRole('button', { name: 'Log Out' }).click();
    await page.goto('/login');
    await page.getByLabel('Email').fill(artisanEmail);
    await page.getByLabel('Password').fill(password);
    await page.getByRole('button', { name: 'Log In' }).click();
    await expect(page).toHaveURL(/\/profile$/);

    await page.goto('/profile/orders');
    const placedItem = page.locator('.item-card', { hasText: '456 Ave Example, Rabat' });
    await expect(placedItem).toContainText(buyerEmail);
    await expect(placedItem).toContainText(productName);

    await placedItem.getByRole('button', { name: 'Mark completed' }).click();
    await expect(placedItem.getByText('Completed')).toBeVisible();
    await expect(placedItem.getByRole('button', { name: 'Mark completed' })).toHaveCount(0);
  });
});
