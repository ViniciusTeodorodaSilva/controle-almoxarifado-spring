import { test, expect, fixtures } from './security-support.js'

const evidence = process.env.BES_VISUAL_EVIDENCE_DIR || 'test-results/premium-p0-round2'
async function focusCycle(page, dialog) {
  for (const key of ['Tab', 'Shift+Tab']) for (let index = 0; index < 30; index++) {
    await page.keyboard.press(key)
    expect(await dialog.evaluate(element => element.contains(document.activeElement))).toBeTruthy()
  }
}

for (const width of [390, 768, 1440]) test(`Premium drawer: keyboard, dismissal and resize ${width}`, async ({ page }) => {
  await page.setViewportSize({ width, height: 900 })
  await page.goto('/dashboard')
  const sidebar = page.locator('.sidebar'), toggle = page.getByRole('button', { name: 'Abrir menu', exact: true })
  if (width > 850) {
    await expect(sidebar).toBeVisible()
    expect(await sidebar.evaluate(element => element.inert)).toBeFalsy()
    await expect(toggle).toBeHidden()
    const content = page.locator('#content')
    await content.focus()
    await page.setViewportSize({ width: 390, height: 900 })
    await expect(toggle).toBeVisible()
    await expect(content).toBeFocused()
    await page.setViewportSize({ width, height: 900 })
    await expect(toggle).toBeHidden()
    await expect(content).toBeFocused()
  } else {
    await expect(sidebar).toBeHidden()
    await toggle.focus()
    for (let index = 0; index < 25; index++) {
      await page.keyboard.press('Tab')
      expect(await sidebar.evaluate(element => element.contains(document.activeElement))).toBeFalsy()
    }
    await toggle.click()
    await expect(sidebar).toHaveAttribute('aria-modal', 'true')
    await expect(sidebar.getByRole('button', { name: 'Fechar menu', exact: true })).toBeFocused()
    await focusCycle(page, sidebar)
    await page.screenshot({ path: `${evidence}/drawer-open-${width}.png`, fullPage: true })
    await page.keyboard.press('Escape')
    await expect(sidebar).toBeHidden()
    await expect(toggle).toBeFocused()
    await toggle.click()
    await sidebar.getByRole('link', { name: 'Fornecedores', exact: true }).click()
    await expect(page).toHaveURL(/fornecedores$/)
    await expect(toggle).toBeFocused()
    await toggle.click()
    await page.locator('.sidebar-backdrop').click({ position: { x: width - 10, y: 500 } })
    await expect(toggle).toBeFocused()
    await toggle.click()
    await page.setViewportSize({ width: 1440, height: 900 })
    expect(await page.locator('.workspace').evaluate(element => element.inert)).toBeFalsy()
    expect(await sidebar.evaluate(element => element.inert)).toBeFalsy()
    await expect(sidebar.locator('.nav-link.active')).toBeFocused()
    await page.setViewportSize({ width, height: 900 })
    await expect(sidebar).toBeHidden()
    await expect(toggle).toBeFocused()
  }
  await page.screenshot({ path: `${evidence}/drawer-closed-${width}.png`, fullPage: true })
  expect(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth)).toBeTruthy()
})

test('Premium drawer preserves permission filtering and upcoming module', async ({ page }) => {
  await page.context().clearCookies()
  await page.setViewportSize({ width: 390, height: 900 })
  await page.goto('/login')
  await page.getByLabel('Usuário', { exact: true }).fill(fixtures.CONSULTA.username)
  await page.getByLabel('Senha', { exact: true }).fill(fixtures.CONSULTA.password)
  await page.getByRole('button', { name: 'Entrar', exact: true }).click()
  await expect(page).toHaveURL(/dashboard$/)
  await page.getByRole('button', { name: 'Abrir menu', exact: true }).click()
  await expect(page.locator('.sidebar').getByRole('link', { name: 'Usuários', exact: true })).toHaveCount(0)
  await expect(page.locator('.sidebar .upcoming')).toContainText('Inventário')
  await expect(page.locator('.sidebar .upcoming')).toContainText('Em breve')
  await focusCycle(page, page.locator('.sidebar'))
})

for (const width of [390, 768, 1440]) test(`Premium supplier names retain readable columns ${width}`, async ({ page, request }) => {
  const name = `Premium ${Date.now()} — Suprimentos industriais para montagem e manutenção de equipamentos`
  const response = await request.post('http://localhost:8081/fornecedores', { data: { nome: name, nomeFantasia: 'Distribuidora industrial de materiais e equipamentos', tipoPessoa: 'PJ', ativo: true } })
  expect(response.ok()).toBeTruthy()
  await page.setViewportSize({ width, height: 900 })
  await page.goto('/fornecedores')
  await page.getByLabel('Pesquisar fornecedores').fill(name)
  const row = page.getByRole('row').filter({ has: page.getByRole('cell', { name, exact: true }) })
  await expect(row).toHaveCount(1)
  const cell = row.getByRole('cell', { name, exact: true })
  expect(await cell.evaluate(element => element.getBoundingClientRect().width)).toBeGreaterThanOrEqual(220)
  const metrics = await page.locator('.supplier-list .table-scroll').evaluate(element => ({ width: element.clientWidth, scroll: element.scrollWidth }))
  if (width < 1440) expect(metrics.scroll).toBeGreaterThan(metrics.width)
  expect(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth)).toBeTruthy()
  await page.screenshot({ path: `${evidence}/supplier-${width}.png`, fullPage: true })
})

test('Premium secondary text contrast at all three widths', async ({ page }) => {
  for (const width of [390, 768, 1440]) {
    await page.setViewportSize({ width, height: 900 })
    await page.goto('/dashboard')
    for (const selector of ['.breadcrumb', '.page-footer']) {
      const ratio = await page.locator(selector).evaluate(element => {
        const parse = color => color.match(/[\d.]+/g).map(Number)
        const luminance = rgb => rgb.slice(0, 3).map(value => value / 255).map(value => value <= .04045 ? value / 12.92 : ((value + .055) / 1.055) ** 2.4).reduce((sum, value, index) => sum + value * [.2126, .7152, .0722][index], 0)
        let background = element
        while (background && parse(getComputedStyle(background).backgroundColor)[3] === 0) background = background.parentElement
        const values = [luminance(parse(getComputedStyle(element).color)), luminance(parse(getComputedStyle(background || document.body).backgroundColor))].sort((a, b) => a - b)
        return (values[1] + .05) / (values[0] + .05)
      })
      expect(ratio, `${selector} at ${width}`).toBeGreaterThanOrEqual(4.5)
    }
  }
})

for (const [route, button] of [['/produtos', 'Novo produto'], ['/categorias', 'Novo cadastro'], ['/fornecedores', 'Novo fornecedor'], ['/epis', 'Configurar EPI']]) test(`Premium shared modal cycle and focus return ${route}`, async ({ page }) => {
  for (const width of [390, 768, 1440]) {
    await page.setViewportSize({ width, height: 900 })
    await page.goto(route)
    const opener = page.getByRole('button', { name: button, exact: true })
    await opener.click()
    const dialog = page.getByRole('dialog')
    await expect(dialog).toBeVisible()
    await expect(dialog.locator('.dialog-header').getByRole('button', { name: 'Fechar', exact: true })).toBeFocused()
    await focusCycle(page, dialog)
    await page.screenshot({ path: `${evidence}/modal-${route.slice(1)}-${width}.png`, fullPage: true })
    await page.keyboard.press('Escape')
    await expect(dialog).toHaveCount(0)
    await expect(opener).toBeFocused()
    await opener.click()
    await dialog.locator('.dialog-header').getByRole('button', { name: 'Fechar', exact: true }).click()
    await expect(opener).toBeFocused()
  }
})

test('Premium busy modal retains focus and prevents dismissal', async ({ page }) => {
  await page.goto('/fornecedores')
  const opener = page.getByRole('button', { name: 'Novo fornecedor', exact: true })
  await opener.click()
  const dialog = page.getByRole('dialog')
  await dialog.getByLabel('Nome / razão social *', { exact: true }).fill('Fornecedor fictício — foco durante gravação')
  let release, intercepted
  const started = new Promise(resolve => { intercepted = resolve })
  const gate = new Promise(resolve => { release = resolve })
  await page.route('**/api/fornecedores', async route => {
    if (route.request().method() !== 'POST') return route.continue()
    intercepted()
    await gate
    await route.fulfill({ status: 409, contentType: 'application/json', body: JSON.stringify({ message: 'Conflito simulado para verificar foco' }) })
  })
  await dialog.getByRole('button', { name: 'Salvar fornecedor', exact: true }).click()
  await started
  await expect(dialog.getByRole('button', { name: 'Fechar', exact: true })).toBeDisabled()
  await page.keyboard.press('Escape')
  await expect(dialog).toBeVisible()
  await focusCycle(page, dialog)
  release()
  await expect(dialog.getByRole('button', { name: 'Salvar fornecedor', exact: true })).toBeEnabled()
  await page.keyboard.press('Escape')
  await expect(opener).toBeFocused()
})

test('Premium asset modal keeps optional context disclosure keyboard accessible', async ({ page, request }) => {
  const warehouseResponse = await request.post('http://localhost:8081/almoxarifados', { data: { nome: 'Central de foco ' + Date.now() } })
  expect(warehouseResponse.ok()).toBeTruthy()
  const warehouse = await warehouseResponse.json()
  const assetResponse = await request.post('http://localhost:8081/ativos', { headers: { 'Idempotency-Key': crypto.randomUUID() }, data: { cadastro: { nome: 'Ativo para contexto por teclado' }, almoxarifadoId: warehouse.id, condicao: 'BOM' } })
  expect(assetResponse.ok()).toBeTruthy()
  const asset = await assetResponse.json()
  await page.goto('/ativos/' + asset.id)
  const opener = page.getByRole('button', { name: 'Registrar empréstimo', exact: true })
  await opener.click()
  const dialog = page.getByRole('dialog'), disclosure = dialog.locator('summary')
  await expect(disclosure).toBeVisible()
  let reached = false
  for (let index = 0; index < 30; index++) {
    await page.keyboard.press('Tab')
    if (await disclosure.evaluate(element => element === document.activeElement)) { reached = true; break }
  }
  expect(reached).toBeTruthy()
  await page.keyboard.press('Enter')
  await expect(disclosure.locator('..')).toHaveAttribute('open', '')
  await expect(dialog.getByLabel('Obra', { exact: true })).toBeVisible()
  await page.keyboard.press('Escape')
  await expect(opener).toBeFocused()
})
