import { test, expect } from '@playwright/test';

test.describe('cálculo de frete funcional', () => {
    test('calcula frete de R$ 15,00 para CEP iniciado por 8', async ({ page }) => {
        await page.goto('/frete');

        await page.getByLabel('CEP').fill('87000000');
        await page.getByLabel('Valor do pedido').fill('100');
        await page.getByRole('button', { name: 'Calcular frete' }).click();

        await expect(page.getByRole('status')).toHaveText('Frete: R$ 15,00');
    });

    test('calcula frete de R$ 25,00 para CEP que não inicia por 8', async ({ page }) => {
        await page.goto('/frete');

        await page.getByLabel('CEP').fill('10000000');
        await page.getByLabel('Valor do pedido').fill('100');
        await page.getByRole('button', { name: 'Calcular frete' }).click();

        await expect(page.getByRole('status')).toHaveText('Frete: R$ 25,00');
    });

    test('calcula frete grátis para pedidos a partir de R$ 200,00', async ({ page }) => {
        await page.goto('/frete');

        await page.getByLabel('CEP').fill('87000000');
        await page.getByLabel('Valor do pedido').fill('200');
        await page.getByRole('button', { name: 'Calcular frete' }).click();

        await expect(page.getByRole('status')).toHaveText('Frete grátis');
    });

    test('calcula frete grátis para pedido acima de R$ 200,00', async ({ page }) => {
        await page.goto('/frete');

        await page.getByLabel('CEP').fill('10000000');
        await page.getByLabel('Valor do pedido').fill('250');
        await page.getByRole('button', { name: 'Calcular frete' }).click();

        await expect(page.getByRole('status')).toHaveText('Frete grátis');
    });

    test('aceita valor com vírgula nos centavos', async ({ page }) => {
        await page.goto('/frete');

        await page.getByLabel('CEP').fill('87000000');
        await page.getByLabel('Valor do pedido').fill('100,50');
        await page.getByRole('button', { name: 'Calcular frete' }).click();

        await expect(page.getByRole('status')).toHaveText('Frete: R$ 15,00');
    });

    test('nega CEP com quantidade diferente de 8 dígitos', async ({ page }) => {
        await page.goto('/frete');

        await page.getByLabel('CEP').fill('8700000');
        await page.getByLabel('Valor do pedido').fill('100');
        await page.getByRole('button', { name: 'Calcular frete' }).click();

        await expect(page.getByRole('alert')).toHaveText('Dados inválidos');
    });

    test('nega CEP contendo caracteres que não são números', async ({ page }) => {
        await page.goto('/frete');

        await page.getByLabel('CEP').fill('8700000A');
        await page.getByLabel('Valor do pedido').fill('100');
        await page.getByRole('button', { name: 'Calcular frete' }).click();

        await expect(page.getByRole('alert')).toHaveText('Dados inválidos');
    });

    test('nega valor igual a zero', async ({ page }) => {
        await page.goto('/frete');

        await page.getByLabel('CEP').fill('87000000');
        await page.getByLabel('Valor do pedido').fill('0');
        await page.getByRole('button', { name: 'Calcular frete' }).click();

        await expect(page.getByRole('alert')).toHaveText('Dados inválidos');
    });

    test('nega valor negativo', async ({ page }) => {
        await page.goto('/frete');

        await page.getByLabel('CEP').fill('87000000');
        await page.getByLabel('Valor do pedido').fill('-100');
        await page.getByRole('button', { name: 'Calcular frete' }).click();

        await expect(page.getByRole('alert')).toHaveText('Dados inválidos');
    });

    test('nega valor com mais de duas casas decimais', async ({ page }) => {
        await page.goto('/frete');

        await page.getByLabel('CEP').fill('87000000');
        await page.getByLabel('Valor do pedido').fill('100,999');
        await page.getByRole('button', { name: 'Calcular frete' }).click();

        await expect(page.getByRole('alert')).toHaveText('Dados inválidos');
    });
});