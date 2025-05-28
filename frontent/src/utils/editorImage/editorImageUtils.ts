import { Editor } from '@tiptap/core'
import { uploadImage } from '@/API/side/sideAPI'
import { useImageStore } from '@/stores/imageStore'
import type { JSONContent } from '@tiptap/core'

/**
 * 处理拖放图片
 */
export const handleImageDrop = (
  editor: Editor,
  imageStore: ReturnType<typeof useImageStore>,
  event: DragEvent
) => {
  const files = event.dataTransfer?.files
  if (!files || files.length === 0) return

  const currentImageAttrs = editor.getAttributes('image') || {
    src: '',
    width: '100%',
    height: 'auto',
    style: 'max-width: 600px; height: auto;',
  }

  for (const file of Array.from(files)) {
    if (file.type.startsWith('image/')) {
      const blobUrl = URL.createObjectURL(file)
      imageStore.addImage(file, blobUrl)

      editor.chain().focus().command(({ tr }) => {
        const node = editor.schema.nodes.image.create({
          ...currentImageAttrs,
          src: blobUrl,
          style: `${currentImageAttrs.style}; --img-width: ${currentImageAttrs.width}; --img-height: ${currentImageAttrs.height};`,
        })
        tr.replaceSelectionWith(node)
        return true
      }).run()
    }
  }
}

/**
 * 递归处理节点中的图片
 */
const processImageNode = async (
  node: JSONContent,
  imageStore: ReturnType<typeof useImageStore>
): Promise<JSONContent> => {
  // 处理当前节点
  if (node.type === 'image' && node.attrs?.src?.startsWith('blob:')) {
    try {
      const files = Array.from(imageStore.pendingImages.keys())
      const blobUrl = node.attrs.src as string
      const file = files.find(f => imageStore.pendingImages.get(f) === blobUrl)

      if (file) {
        const permanentUrl = await uploadImage(file)
        // 更新节点属性
        node.attrs = {
          ...node.attrs,
          src: permanentUrl
        }

        imageStore.removeImage(file)
        imageStore.addUploadedImage(permanentUrl)
        URL.revokeObjectURL(blobUrl)
      }
    } catch (error) {
      console.error('图片上传失败:', error)
    }
  }

  // 递归处理子节点 - 并行处理
  if (node.content) {
    //确保 node.content 存在
    node.content = await Promise.all(
      node.content.map(childNode => processImageNode(childNode, imageStore))
    )
  }

  return node
}

/**
 * 处理编辑器中的所有图片
 */
export const processEditorImages = async (
  content: JSONContent,
  imageStore: ReturnType<typeof useImageStore>
): Promise<JSONContent> => {
  // 深拷贝内容
  const processedContent = JSON.parse(JSON.stringify(content)) as JSONContent

  // 确保 content.content 存在
  if (processedContent.content) {
    // 并行处理所有顶级节点
    processedContent.content = await Promise.all(
      processedContent.content.map(node => processImageNode(node, imageStore))
    )
  }

  return processedContent
}
