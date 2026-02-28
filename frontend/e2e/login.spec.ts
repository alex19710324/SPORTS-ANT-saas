// e2e/login.spec.ts
import { test, expect } from '@playwright/test';

test('login flow', async ({ page }) => {
  await page.goto('http://localhost:5174/login');

  // Fill login form
  await page.fill('input[type="text"]', 'testuser');
  await page.fill('input[type="password"]', 'password123');

  // Click login
  await page.click('button[type="submit"]');

  // Expect to be redirected to home
  await expect(page).toHaveURL('http://localhost:5174/');
  
  // Expect to see welcome message
  await expect(page.locator('h1')).toContainText('Welcome');
});
