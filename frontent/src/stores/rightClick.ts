import { defineStore } from "pinia";
import { ref } from "vue";

export const useRightClickStore = defineStore('rightClick', () => {

    const isRightChickActive = ref(false)

    const menuPosition = ref({ x: 0, y: 0 })

    let debounceTimeout: number | null = null

    const toggleRightClick = (event: MouseEvent) => {

        if (debounceTimeout) {
            clearTimeout(debounceTimeout)
        }

        isRightChickActive.value = false

        debounceTimeout = window.setTimeout(() => {
            isRightChickActive.value = true
            menuPosition.value = { x: event.clientX, y: event.clientY }
        }, 100)


    }

    const closeRightClickMenu = () => {
        isRightChickActive.value = false
    }

    return { isRightChickActive, menuPosition, toggleRightClick, closeRightClickMenu }
})