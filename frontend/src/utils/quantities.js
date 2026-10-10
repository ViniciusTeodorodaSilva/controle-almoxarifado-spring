// Decimal comparison at the legacy JSON Number boundary. No rounding or epsilon.
function parts(value) {
  const text = String(value).trim()
  if (text.length > 1000) return null
  const match = /^\+?(\d*)(?:\.(\d*))?(?:[eE]([+-]?\d+))?$/.exec(text)
  if (!match || !(match[1] || match[2])) return null
  const exponent = Number(match[3] || 0) - (match[2]?.length || 0)
  if (!Number.isSafeInteger(exponent) || Math.abs(exponent) > 2000) return null
  return { coefficient: BigInt((match[1] || '') + (match[2] || '')), exponent }
}
export function compareQuantities(a, b) {
  const left = parts(a), right = parts(b)
  if (!left || !right) return null
  const exponent = Math.min(left.exponent, right.exponent)
  const x = left.coefficient * 10n ** BigInt(left.exponent - exponent)
  const y = right.coefficient * 10n ** BigInt(right.exponent - exponent)
  return x === y ? 0 : x > y ? 1 : -1
}
export function fitsLegacyQuantity(value) {
  const converted = Number(value)
  return Number.isFinite(converted) && converted > 0 && compareQuantities(value, String(converted)) === 0
}
export function sumQuantities(a, b) {
  const left = parts(a), right = parts(b)
  if (!left || !right) return null
  const exponent = Math.min(left.exponent, right.exponent)
  const coefficient = left.coefficient * 10n ** BigInt(left.exponent - exponent)
    + right.coefficient * 10n ** BigInt(right.exponent - exponent)
  return `${coefficient}e${exponent}`
}
export function quantityNumber(value) {
  if (!fitsLegacyQuantity(value)) throw new Error('A quantidade excede a precisão suportada. Nenhum valor foi arredondado.')
  return Number(value)
}
