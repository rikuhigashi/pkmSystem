import ImageResize from 'tiptap-extension-resize-image'

export const CustomImage = ImageResize.extend({
  addAttributes() {
    return {
      ...this.parent?.(),
      width: {
        default: '100%',
        parseHTML: (element) => element.getAttribute('width') || '100%',
        renderHTML: ({ width }) => ({ width }),
        updateDOM: (element: HTMLElement, value: string) => {
          element.setAttribute('width', value)
        },
      },
      height: {
        default: 'auto',
        parseHTML: (element) => {
          const existingStyle = element.getAttribute('style') || ''
          return `${existingStyle}; max-width: 600px; height: auto;`
        },
        renderHTML: ({ height }) => ({ height }),
        updateDOM: (element: HTMLElement, value: string) => {
          element.setAttribute('height', value)
        },
      },
      style: {
        default: 'max-width: 600px; height: auto;',
        parseHTML: (element) => element.getAttribute('style'),
        renderHTML: ({ style }) => ({ style }),
        updateDOM: (element: HTMLElement, value: string) => {
          element.setAttribute('style', value)
        },
      },
      isResizable: {
        default: true,
      },
    }
  },
}).configure({
  inline: true,
  allowBase64: true,
  HTMLAttributes: {
    class: 'prose-img',
  },
})
