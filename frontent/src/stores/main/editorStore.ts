import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useEditorStore = defineStore('editor', () => {
  const currentDocId = ref()

  const editorContent = ref()

  // 保存方法
  // const saveContent = async () => {
  //   if (!editorContent.value || !currentDocId.value) return
  //   try {
  //     // console.log("leftClickItemId",rightClickStore.leftClickItemId)
  //     // await saveMainData(editorContent.value, rightClickStore.leftClickItemId)
  //     await saveMainData(editorContent.value, currentDocId.value)
  //   } catch (error) {
  //     console.error('保存失败:', error)
  //   }
  // }

  // 设置当前文档ID
  const setCurrentDocId = (id: number) => {
    currentDocId.value = id
  }

  return { editorContent, currentDocId, setCurrentDocId }
})
