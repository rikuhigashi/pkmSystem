import { h } from 'vue'
import type { JSONContent } from '@tiptap/core'

// 递归渲染节点
const renderNode = (node: JSONContent): ReturnType<typeof h> | string | null => {
  if (!node) return null

  // 文本节点
  if (node.type === 'text') {
    return node.text || ''
  }

  // 处理子节点
  const children = node.content
    ? node.content.map(renderNode).filter(Boolean)
    : []

  // 根据节点类型渲染
  switch (node.type) {
    case 'paragraph':
      return h('p', {}, children)
    case 'heading':
      const level = node.attrs?.level || 1
      return h(`h${level}`, {}, children)
    case 'bulletList':
      return h('ul', { class: 'list-disc pl-5' }, children)
    case 'orderedList':
      return h('ol', { class: 'list-decimal pl-5' }, children)
    case 'listItem':
      return h('li', {}, children)
    case 'bold':
      return h('strong', {}, children)
    case 'italic':
      return h('em', {}, children)
    case 'underline':
      return h('u', {}, children)
    case 'code':
      return h('code', { class: 'bg-gray-100 p-1 rounded' }, children)
    case 'codeBlock':
      return h('pre', { class: 'bg-gray-800 text-gray-100 p-4 rounded overflow-x-auto' },
        h('code', {}, children)
      )
    case 'blockquote':
      return h('blockquote', { class: 'border-l-4 border-gray-300 pl-4 italic' }, children)
    case 'horizontalRule':
      return h('hr', { class: 'my-4 border-t border-gray-300' })
    case 'image':
      return h('img', {
        src: node.attrs?.src,
        alt: node.attrs?.alt || '',
        class: 'max-w-full my-4 rounded-lg'
      })
    case 'link':
      return h('a', {
        href: node.attrs?.href,
        target: '_blank',
        rel: 'noopener noreferrer',
        class: 'text-blue-500 hover:underline'
      }, children)
    default:
      return h('div', {}, children)
  }
}

// 主渲染函数
export const renderTiptapContent = (content: JSONContent): ReturnType<typeof h> | null => {
  if (!content || !content.content) return null

  // 创建根节点
  return h('div', { class: 'tiptap-content space-y-4' },
    content.content.map(renderNode).filter(Boolean)
  )
}
