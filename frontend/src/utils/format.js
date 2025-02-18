/**
 * 格式化金额
 * @param {number} amount - 金额
 * @param {number} decimals - 小数位数，默认2位
 * @returns {string} 格式化后的金额字符串
 */
export function formatMoney(amount, decimals = 2) {
  if (amount === null || amount === undefined) {
    return '¥0.00';
  }
  
  const num = Number(amount);
  if (isNaN(num)) {
    return '¥0.00';
  }

  return new Intl.NumberFormat('zh-CN', {
    style: 'currency',
    currency: 'CNY',
    minimumFractionDigits: decimals,
    maximumFractionDigits: decimals
  }).format(num);
}

/**
 * 格式化百分比
 * @param {number} value - 小数值
 * @param {number} decimals - 小数位数，默认0位
 * @returns {string} 格式化后的百分比字符串
 */
export function formatPercent(value, decimals = 0) {
  if (value === null || value === undefined) {
    return '0%';
  }
  
  const num = Number(value) * 100;
  if (isNaN(num)) {
    return '0%';
  }

  return num.toFixed(decimals) + '%';
}

export function formatAmount(amount) {
  if (!amount) return '0.00'
  return Number(amount).toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
} 