<template>
  <div ref="editorRef"     class="p-4 border rounded-lg border-base-300 bg-base-100 prose-img:max-w-full prose-img:h-auto prose-img:my-2 prose-img:rounded-lg prose-img:shadow-md"></div>
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


