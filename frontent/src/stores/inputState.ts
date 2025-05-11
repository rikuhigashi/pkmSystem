import { defineStore } from 'pinia'
import { ref } from 'vue'
// 输入框状态

export const userInputStore = defineStore('rightMenu', () => {
  const isInputActive = ref(false) //输入框状态
  const isEditingActive = ref(false) // 编辑状态
  const isInputError = ref(false)   // 错误状态
  const isInputLink = ref(false) // 链接状态

  // 激活添加输入框
  const openInput = () => {
    isInputActive.value = true
  }

  // 关闭添加输入框
  const closeInput = () => {
    isInputActive.value = false
  }

  // 激活编辑输入框
  const openEditingInput = () => {
      isEditingActive.value = true
  }

  // 关闭编辑输入框
  const closeEditingInput = () => {
      isEditingActive.value = false
  }

  // 激活错误
  const activationInputError = () => {
    isInputError.value = true
  }

  // 关闭错误
  const closeInputError = () => {
    isInputError.value = false
  }


  // 链接输入框
  const toggleLinkInput = () => {
    isInputLink.value = !isInputLink.value
  }

  return {
    isInputActive,
    isEditingActive,
    isInputError,
    openInput,
    closeInput,
    openEditingInput,
    closeEditingInput,
    activationInputError,
    closeInputError,
    isInputLink,
    toggleLinkInput
  }
})
