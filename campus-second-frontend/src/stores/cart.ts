import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getCartList } from '../api/cart'

export const useCartStore = defineStore('cart', () => {
  const count = ref(0)

  const refreshCount = async (userid: number | undefined) => {
    if (!userid) {
      count.value = 0
      return
    }
    try {
      const list = (await getCartList(userid)) || []
      count.value = list.reduce((sum: number, item: { buynumber?: number }) => sum + (item.buynumber || 1), 0)
    } catch {
      count.value = 0
    }
  }

  return { count, refreshCount }
})
