import { test, expect } from '@playwright/test';

test.describe('cadastro de senha funcional', () => {
    test('permite cadastrar senha válida', async ({ page }) => {
        await page.goto('/senha');

        await page.getByLabel('Nova senha').fill('SenhaSegura123');
        await page.getByLabel('Confirmar senha').fill('SenhaSegura123');
        await page.getByRole('button', { name: 'Cadastrar senha' }).click();

        await expect(page.getByRole('status')).toHaveText('Senha cadastrada');
        await expect(page.getByLabel('Nova senha')).toHaveValue('');
        await expect(page.getByLabel('Confirmar senha')).toHaveValue('');
    });

    test('nega senha com menos de 8 caracteres', async ({ page }) => {
        await page.goto('/senha');

        await page.getByLabel('Nova senha').fill('Senha1');
        await page.getByLabel('Confirmar senha').fill('Senha1');
        await page.getByRole('button', { name: 'Cadastrar senha' }).click();

        await expect(page.getByRole('alert')).toHaveText('Senha fora do padrão');
    });

    test('nega senha com mais de 20 caracteres', async ({ page }) => {
        await page.goto('/senha');

        await page.getByLabel('Nova senha').fill('SenhaSegura1234567890');
        await page.getByLabel('Confirmar senha').fill('SenhaSegura1234567890');
        await page.getByRole('button', { name: 'Cadastrar senha' }).click();

        await expect(page.getByRole('alert')).toHaveText('Senha fora do padrão');
    });

    test('nega senha sem letra maiúscula', async ({ page }) => {
        await page.goto('/senha');

        await page.getByLabel('Nova senha').fill('senhasegura123');
        await page.getByLabel('Confirmar senha').fill('senhasegura123');
        await page.getByRole('button', { name: 'Cadastrar senha' }).click();

        await expect(page.getByRole('alert')).toHaveText('Senha fora do padrão');
    });

    test('nega senha sem letra minúscula', async ({ page }) => {
        await page.goto('/senha');

        await page.getByLabel('Nova senha').fill('SENHASEGURA123');
        await page.getByLabel('Confirmar senha').fill('SENHASEGURA123');
        await page.getByRole('button', { name: 'Cadastrar senha' }).click();

        await expect(page.getByRole('alert')).toHaveText('Senha fora do padrão');
    });

    test('nega senha sem número', async ({ page }) => {
        await page.goto('/senha');

        await page.getByLabel('Nova senha').fill('SenhaSegura');
        await page.getByLabel('Confirmar senha').fill('SenhaSegura');
        await page.getByRole('button', { name: 'Cadastrar senha' }).click();

        await expect(page.getByRole('alert')).toHaveText('Senha fora do padrão');
    });

    test('nega senha com espaço', async ({ page }) => {
        await page.goto('/senha');

        await page.getByLabel('Nova senha').fill('Senha Segura123');
        await page.getByLabel('Confirmar senha').fill('Senha Segura123');
        await page.getByRole('button', { name: 'Cadastrar senha' }).click();

        await expect(page.getByRole('alert')).toHaveText('Senha fora do padrão');
    });

    test('nega cadastro quando as senhas não coincidem', async ({ page }) => {
        await page.goto('/senha');

        await page.getByLabel('Nova senha').fill('SenhaSegura123');
        await page.getByLabel('Confirmar senha').fill('SenhaDiferente123');
        await page.getByRole('button', { name: 'Cadastrar senha' }).click();

        await expect(page.getByRole('alert')).toHaveText('As senhas não coincidem');
    });
});