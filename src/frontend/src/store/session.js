import { ref } from 'vue'

export const isLoggedIn = ref(false)

export function loginMock() {
    isLoggedIn.value = true
}

export function logoutMock() {
    isLoggedIn.value = false
}
