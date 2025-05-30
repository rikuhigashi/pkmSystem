import {ref, type Ref} from 'vue'
import { Editor } from '@tiptap/core'
import noCollingBlur from '@/utils/noCollingBlurMethod'
import { userInputStore } from '@/stores/inputState'

export const useLinkInput = (
  bubbleMenuRef: Ref<any>,
  editor: Ref<Editor | undefined>,
  inputStore: ReturnType<typeof userInputStore>
) => {
  const linkPosition = ref({ top: 0, left: 0 })
  const inputLinkValue = ref('')

  const setLinkValue = () => {
    if (!editor.value) return

    if (inputLinkValue.value) {
      let href = inputLinkValue.value.trim()
      if (!/^https?:\/\//i.test(href)) {
        href = 'https://' + href
      }
      editor.value.chain().focus().setLink({ href }).run()
    } else {
      editor.value.chain().focus().unsetLink().run()
    }

    inputStore.toggleLinkInput()
    inputLinkValue.value = ''
  }

  const handleEnterKey = () => {
    noCollingBlur.handleKeyup()
    setLinkValue()
  }

  const handleBlur = () => {
    if (!noCollingBlur.isEnterPressed.value) {
      setLinkValue()
    }
    noCollingBlur.handleBlur()
  }

  const openLinkInput = () => {
    if (!bubbleMenuRef.value || !editor.value) return

    const linkBtn = bubbleMenuRef.value.querySelector(
      '[data-toolbar-button="link"]'
    ) as HTMLElement | null

    if (linkBtn) {
      const rect = linkBtn.getBoundingClientRect()
      linkPosition.value = {
        top: rect.bottom + 10,
        left: rect.left,
      }
    }

    if (editor.value.isActive('link')) {
      inputLinkValue.value = editor.value.getAttributes('link').href
    }

    inputStore.toggleLinkInput()
  }

  return {
    linkPosition,
    inputLinkValue,
    setLinkValue,
    handleEnterKey,
    handleBlur,
    openLinkInput
  }
}
