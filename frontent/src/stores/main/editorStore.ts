import { defineStore } from 'pinia'
import { ref } from 'vue'

type SaveDocumentFunction = () => Promise<void>;


export const useEditorStore = defineStore('editor', () => {
  const currentDocId = ref()

  const editorContent = ref()
  const saveDocument = ref<SaveDocumentFunction | null>(null)

  // 设置当前文档ID
  const setCurrentDocId = (id: number) => {
    currentDocId.value = id
  }

  return { editorContent, currentDocId, setCurrentDocId ,saveDocument}
})
