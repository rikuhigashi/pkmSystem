import { Editor } from '@tiptap/core'
import {
  extractImageUrls,
  handleImageDrop as handleImageDropUtil,
  processEditorImages
} from '@/utils/editorImage/editorImageUtils'
import { deleteImage } from '@/API/side/sideAPI'
import { useImageStore } from '@/stores/imageStore'
import { useEditorStore } from '@/stores/main/editorStore'
import { useAlertStore } from '@/stores/alert'

const extractKeyFromCosUrl = (url: string): string | null => {
  try {
    const urlObj = new URL(url)
    return urlObj.pathname.substring(1)
  } catch (e) {
    console.error('解析URL失败:', e, url)
    return null
  }
}

export const useImageHandling = () => {
  const handleDrop = (editor: Editor, imageStore: ReturnType<typeof useImageStore>, event: DragEvent) => {
    handleImageDropUtil(editor, imageStore, event)
  }

  const handleDragOver = (event: DragEvent) => {
    event.dataTransfer!.dropEffect = 'copy'
  }

  const saveDocument = async (
    editor: Editor,
    imageStore: ReturnType<typeof useImageStore>,
    editorStore: ReturnType<typeof useEditorStore>,
    alertStore: ReturnType<typeof useAlertStore>
  ) => {
    alertStore.showLoading('正在保存中... 请稍后...')

    try {
      const content = editor.getJSON()
      const processedContent = await processEditorImages(content, imageStore)
      editor.commands.setContent(processedContent)

      const currentImageUrls = extractImageUrls(processedContent)
      imageStore.setCurrentDocImages(currentImageUrls)

      const unusedImages = imageStore.getUnusedImages()
      for (const url of unusedImages) {
        if (!currentImageUrls.includes(url)) {
          const key = extractKeyFromCosUrl(url)
          if (key) {
            await deleteImage(key)
            imageStore.removeUploadedImage(url)
          }
        }
      }

      editorStore.editorContent = processedContent
      alertStore.showAlert('保存成功', 'success')
    } catch (error) {
      console.error('保存文档失败:', error)
      alertStore.showAlert('保存失败', 'error')
    }
  }

  return { handleDrop, handleDragOver, saveDocument }
}
