import { debounce } from '@/utils/debounce'

export const vResizeObserver = {
  mounted(el, binding) {
    const callback = binding.value
    if (typeof callback !== 'function') {
      console.warn('v-resize-observer requires a function as value')
      return
    }

    const debouncedCallback = debounce(callback)
    const observer = new ResizeObserver(debouncedCallback)
    observer.observe(el)

    el._resizeObserver = observer
  },
  unmounted(el) {
    if (el._resizeObserver) {
      el._resizeObserver.disconnect()
      delete el._resizeObserver
    }
  }
} 