<template>
  <div ref="editorRef" class="tiptap-container"></div>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { Editor } from '@tiptap/core'
import StarterKit from '@tiptap/starter-kit'
import Image from '@tiptap/extension-image'
import type { JSONContent } from '@tiptap/vue-3'

const props = defineProps<{
  content: JSONContent | null
}>()

const editorRef = ref<HTMLElement>()
let editor: Editor | null = null

onMounted(() => {
  if (!editorRef.value) return

  editor = new Editor({
    element: editorRef.value,
    extensions: [
      StarterKit,
      Image.configure({
        inline: true,
        allowBase64: true
      })
    ],
    content: props.content,
    editable: false
  })
})

watch(() => props.content, (newContent) => {
  if (editor && newContent) {
    editor.commands.setContent(newContent)
  }
})
</script>

<style>
.tiptap-container {
  @apply p-4 border rounded-lg;
}

.tiptap-container img {
  @apply max-w-full h-auto my-2;
}

.tiptap-container :deep(.ProseMirror) {
  @apply min-h-[300px]! !p-4;
}
</style>
