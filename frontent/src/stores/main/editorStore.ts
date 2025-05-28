import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useEditorStore = defineStore('editor', () => {
  const currentDocId = ref()

  const editorContent = ref()



  // 设置当前文档ID
  const setCurrentDocId = (id: number) => {
    currentDocId.value = id
  }

  return { editorContent, currentDocId, setCurrentDocId }
})
