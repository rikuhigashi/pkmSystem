import { defineStore } from "pinia";
import { ref } from "vue";
// 输入框状态


export const userInputStore = defineStore('rightMenu', () => {

    const isInputActive = ref(false)  //输入框状态
    const isEditingActive = ref(false)  // 编辑状态 


    // 激活输入框
    const openInput = () => {
        isInputActive.value = true
    }
    
    // 关闭输入框
    const closeInput = () => {
        isInputActive.value = false
    }



    return { isInputActive,isEditingActive,
        openInput, closeInput }
})