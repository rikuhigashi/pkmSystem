import { defineStore } from 'pinia'
import { ref, type Ref } from 'vue'

interface sidebarState {
    sidebarOpen: Ref<boolean>
}

interface sidebarActicon {
    openSidebar: () => void
    closeSidebar: () => void
    toggleSidebar: () => void
}

export const useSidebarStore = defineStore('sidebar', (): sidebarState & sidebarActicon => {
    const sidebarOpen = ref(false)

    const openSidebar = () => {
        sidebarOpen.value = true
    }

    const closeSidebar = () => {
        sidebarOpen.value = false
    }

    const toggleSidebar = () => {
        sidebarOpen.value = !sidebarOpen.value
    }



    return { sidebarOpen, openSidebar, closeSidebar, toggleSidebar }
})