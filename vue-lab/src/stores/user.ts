import { ref, computed } from 'vue'
import type { User } from '../types'

const userInfo = ref<User | null>(null)

export function useUserStore() {
  const isLoggedIn = computed(() => !!userInfo.value?.token)

  const login = (username: string, token: string) => {
    userInfo.value = { username, token }
  }

  const logout = () => {
    userInfo.value = null
  }

  return {
    userInfo,
    isLoggedIn,
    login,
    logout
  }
}