import {ref,type Ref} from 'vue'
import { Editor } from '@tiptap/core'

export const useColorPicker = (
  bubbleMenuRef: Ref<any>,
  editor: Ref<Editor | undefined>
) => {
  const showColorPicker = ref(false)
  const colorPickerPosition = ref({ top: 0, left: 0 })
  const colorActionType = ref<'text' | 'highlight'>('text')

  const handleColorSelect = (color: string) => {
    if (!editor.value) return

    if (color === 'transparent') {
      if (colorActionType.value === 'highlight') {
        editor.value.chain().focus().unsetHighlight().run()
      } else {
        editor.value.chain().focus().unsetColor().run()
      }
      return
    }

    if (colorActionType.value === 'highlight') {
      editor.value.chain().focus().setHighlight({ color }).run()
    } else {
      editor.value.chain().focus().setColor(color).run()
    }
  }

  const handleColorPickerClose = () => {
    showColorPicker.value = false
    colorActionType.value = 'text'
  }

  const openColorPicker = (type: 'text' | 'highlight', event?: MouseEvent) => {
    if (!bubbleMenuRef.value?.$el || !editor.value) return

    const selector = type === 'highlight'
      ? '[data-toolbar-button="highlight"]'
      : '[data-toolbar-button="textColor"]'

    const button = bubbleMenuRef.value.$el.querySelector(selector) as HTMLElement | null
    if (button) {
      const rect = button.getBoundingClientRect()
      colorPickerPosition.value = {
        top: rect.bottom + 10,
        left: rect.left,
      }
    }

    showColorPicker.value = true
    colorActionType.value = type
    event?.stopPropagation()
  }

  return {
    showColorPicker,
    colorPickerPosition,
    colorActionType,
    handleColorSelect,
    handleColorPickerClose,
    openColorPicker
  }
}
