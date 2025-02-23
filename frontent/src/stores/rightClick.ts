import { defineStore } from "pinia";
import { ref } from "vue";

export const useRightClickStore = defineStore('rightClick', () => {

    const isRightClickActive = ref(false)  //右键状态

    const menuPosition = ref({ x: 0, y: 0 })  // 菜单栏坐标位置
    let debounceTimeout: number | null = null // 防抖
    const selectItem = ref()  //选中的item
    const copiedItem = ref()  //复制的item 

    // 切换右键菜单
    const toggleRightClick = (event: MouseEvent) => {

        // 清除Timeout，确保只有一个
        if (debounceTimeout) {
            clearTimeout(debounceTimeout)
        }

        // 重置右键状态,确保只有一个右键菜单
        isRightClickActive.value = false


        // 防抖，并出现右键菜单
        debounceTimeout = window.setTimeout(() => {
            isRightClickActive.value = true
            menuPosition.value = { x: event.clientX, y: event.clientY }
        }, 100)

    }

    // 关闭右键菜单
    const closeRightClickMenu = () => {
        isRightClickActive.value = false
    }

    return {
        isRightClickActive, menuPosition, selectItem,copiedItem,
        toggleRightClick, closeRightClickMenu
    }
})