<template>
  <div class="flex-1 w-full">
    <div v-if="editor" class="h-full flex flex-col">
      <!-- 工具栏 -->
      <bubble-menu
        ref="bubbleMenuRef"
        v-if="editor"
        class="bubble-menu shadow-lg rounded-box"
        :editor="editor"
        :tippy-options="{
          duration: 100,
          maxWidth: 'none',
          placement: 'top',
          animation: 'fade',
          appendTo: 'parent',
        }"
        :should-show="shouldShowBubble"
      >
        <div class="flex flex-wrap gap-1 p-2 bg-base-200 rounded border border-base-300">
          <template v-for="(item, index) in toolbarItems" :key="index">
            <DropdownMenu
              v-if="item.type === 'dropdown'"
              :config="item"
              :is-active="item.isActive?.() ?? false"
            />

            <button
              v-else-if="item.type === 'button'"
              @mousedown.prevent="item.action($event)"
              :disabled="item.disabled?.() ?? false"
              :class="['btn btn-xs btn-ghost gap-1', item.isActive?.() ? 'btn-active' : '']"
              :title="item.title"
              :data-toolbar-button="item.meta?.type"
            >
              <component :is="item.icon" class="h-4 w-4"></component>
            </button>

            <div v-else-if="item.type === 'divider'" class="h-6 w-px mx-1.5 bg-gray-300 m-auto" />
          </template>
        </div>
      </bubble-menu>

      <input
        type="text"
        placeholder="Type here"
        v-if="inputStore.isInputLink"
        v-focus="true"
        v-model="inputLinkValue"
        @keyup.enter.prevent="handleEnterKey()"
        @blur="handleBlur()"
        :style="{
          top: `${linkPosition.top}px`,
          left: `${linkPosition.left}px`,
        }"
        class="input fixed z-50 bg-white p-2 shadow-lg border rounded w-64 transition-all duration-150"
      />

      <color-select
        :visible="showColorPicker"
        :position="colorPickerPosition"
        :style="{
          top: `${colorPickerPosition.top}px`,
          left: `${colorPickerPosition.left}px`,
        }"
        @color-change="handleColorSelect"
        @close="handleColorPickerClose"
      />

      <!-- 编辑器内容 -->
      <editor-content
        :editor="editor"
        class="editor-container flex-1 p-6 rounded-box bg-base-100 border border-base-300 shadow-sm prose prose-sm max-w-none min-h-screen"
        @dragover.prevent="handleDragOver"
        @drop.prevent="handleDrop"
      >
      </editor-content>
    </div>
  </div>
</template>

<script setup lang="ts">
import { nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useEditor, EditorContent, BubbleMenu } from '@tiptap/vue-3'
import { Editor } from '@tiptap/core'

// 导入优化后的模块
import { useEditorConfig } from '@/views/main/configs/editorConfig'
import { useEditorCollaboration } from '@/views/main/configs/collaboration'
import { toolbarButtons } from '@/views/main/configs/toolbarConfig'
import { useImageHandling } from '@/views/main/configs/imageHandling'
import { useColorPicker } from '@/views/main/configs/colorPicker'
import { useLinkInput } from '@/views/main/configs/linkInput'

// 导入 stores
import { userInputStore } from '@/stores/inputState'
import { useEditorStore } from '@/stores/main/editorStore'
import { useAlertStore } from '@/stores/alert'
import { useImageStore } from '@/stores/imageStore'
import { userAuthStore } from '@/stores/auth'

// 导入工具函数
import { loadMainData } from '@/views/main/configs/mainConfigs'
import sideMenuMethod from '@/views/side/configs/sideMenuMethod'

// 导入组件
import colorSelect from '@/components/mainComponent/colorSelect.vue'
import DropdownMenu from '@/components/mainComponent/DropdownMenu.vue'
import type { toolbarItem } from '@/views/main/types/mainTypes'

const inputStore = userInputStore()
const editorStore = useEditorStore()
const alertStore = useAlertStore()
const imageStore = useImageStore()
const authStore = userAuthStore()

// 设置协作功能
const { ydoc, websocketProvider, currentUser } = useEditorCollaboration(
  authStore,
  editorStore.currentDocId,
)

// 设置编辑器配置
const editorConfig = useEditorConfig(ydoc, websocketProvider, currentUser)

// 初始化编辑器
const editor = useEditor(editorConfig)
const bubbleMenuRef = ref<InstanceType<typeof BubbleMenu> | null>(null)
const toolbarItems = ref<toolbarItem[]>([])

// 设置图片处理
const { handleDrop, handleDragOver, saveDocument: saveDocumentUtil } = useImageHandling()

// 设置颜色选择器
const {
  showColorPicker,
  colorPickerPosition,
  colorActionType,
  handleColorSelect,
  handleColorPickerClose,
  openColorPicker,
} = useColorPicker(bubbleMenuRef, editor)

// 设置链接输入框
const {
  linkPosition,
  inputLinkValue,
  handleEnterKey,
  handleBlur,
  openLinkInput
} = useLinkInput(bubbleMenuRef, editor, inputStore)

// Bubble菜单显示条件判断
const shouldShowBubble = ({ editor }: { editor: Editor }) => {
  if (editor.isActive('image')) return false
  return editor.state.selection.content().size > 0
}

// 保存文档
const saveDocument = async () => {
  if (editor.value) {
    await saveDocumentUtil(editor.value, imageStore, editorStore, alertStore)
    // 调用保存API
    await sideMenuMethod.saveMainData()
  }
}

// 监听文档ID变化
watch(
  () => editorStore.currentDocId,
  async (newId) => {
    await nextTick()
    if (newId && editor.value) {
      try {
        await loadMainData(newId, editor.value)
        editorStore.editorContent = editor.value.getJSON()
      } catch (error) {
        console.error('加载数据失败:', error)
      }
    }
  },
  { immediate: true }
)

onMounted(() => {
  // 初始化编辑器内容
  if (editor.value && !editor.value.getHTML()) {
    editor.value.commands.insertContent('<p></p><p></p>')
  }

  // 设置保存方法
  editorStore.saveDocument = saveDocument

  // 连接状态监控
  websocketProvider.value?.on('status', (event) => {
    console.log('WebSocket 状态:', event.status)
    if (event.status === 'disconnected') {
      alertStore.showAlert('协作连接断开', 'warning')
    }
  })

  // 创建工具栏配置
  toolbarItems.value = toolbarButtons({
    editor: editor,
    bubbleMenuRef: bubbleMenuRef,
    linkPosition: linkPosition,
    inputLinkValue: inputLinkValue,
    colorPickerPosition: colorPickerPosition,
    showColorPicker: showColorPicker,
    colorActionType: colorActionType,
    openLinkInput: openLinkInput,
    openColorPicker: openColorPicker
  })
})

onBeforeUnmount(() => {
  websocketProvider.value?.destroy()
  ydoc.destroy()
  editor.value?.destroy()
  editorStore.saveDocument = null
})
</script>

<style>
/* 代码块容器 */
.ProseMirror pre {
  background-color: #f8f9fa !important;
  border: 1px solid #e9ecef !important;
  border-radius: 6px !important;
  padding: 1.25rem !important;
  margin: 1rem 0 !important;
  overflow-x: auto;
  font-family: 'Fira Code', Menlo, Consolas, monospace !important;
  font-size: 14px !important;
  line-height: 1.6 !important;
}

/* 代码行样式 */
.ProseMirror code {
  background-color: transparent !important;
  color: #383a42 !important;
  padding: 0 !important;
  font-family: inherit !important;
}

/* 高亮关键字增强 */
.hljs-keyword {
  color: #a626a4 !important;
}

.hljs-built_in {
  color: #e45649 !important;
}

.hljs-title {
  color: #4078f2 !important;
}

.hljs-string {
  color: #50a14f !important;
}

.hljs-comment {
  color: #a0a1a7 !important;
}

.hljs-number {
  color: #986801 !important;
}

.hljs-literal {
  color: #0184bc !important;
}

.hljs-type {
  color: #c18401 !important;
}

.hljs-params {
  color: #383a42 !important;
}

.hljs-function {
  color: #4078f2 !important;
}

.hljs-tag {
  color: #e45649 !important;
}

.hljs-attr {
  color: #986801 !important;
}

.hljs-selector-class {
  color: #c18401 !important;
}

.hljs-ln-numbers {
  color: #adb5bd !important;
  padding-right: 1.5em !important;
  text-align: right !important;
  user-select: none !important;
}

.ProseMirror pre::selection,
.ProseMirror code::selection {
  background-color: #d0e9ff !important;
}

/* 全局样式或组件样式 */
.ProseMirror a {
  color: #3b82f6; /* blue-500 */
  text-decoration: underline;
  cursor: pointer;
}

.ProseMirror a:hover {
  color: #2563eb; /* blue-600 */
}

.ProseMirror p.is-empty::before {
  content: attr(data-placeholder);
  color: #adb5bd;
  float: left;
  height: 0;
  pointer-events: none;
}

.ProseMirror:focus-visible {
  outline: none !important;
  box-shadow: none !important;
}

::selection {
  background: rgba(147, 197, 253, 0.3) !important; /* 半透明蓝色 */
  color: inherit !important;
  text-shadow: none !important;
}

::-moz-selection {
  background: rgba(147, 197, 253, 0.3) !important;
  color: inherit !important;
}

/* 标题样式 */
.ProseMirror h1 {
  font-size: 2em;
  font-weight: bold;
  margin: 0.67em 0;
}

.ProseMirror h2 {
  font-size: 1.5em;
  font-weight: bold;
  margin: 0.83em 0;
}

.ProseMirror h3 {
  font-size: 1.17em;
  font-weight: bold;
  margin: 1em 0;
}

.ProseMirror h4 {
  font-size: 1em;
  font-weight: bold;
  margin: 1.33em 0;
}

.ProseMirror ul {
  list-style-type: disc;
  padding-left: 1.5rem;
  margin: 0.5rem 0;
}

/* 有序列表样式 */
.ProseMirror ol {
  list-style-type: decimal;
  padding-left: 1.5rem;
  margin: 0.5rem 0;
}

/* 列表项样式 */
.ProseMirror li {
  margin: 0.25rem 0;
}

.editor-container {
  position: relative;
  overflow: visible;
}

.prose-paragraph {
  min-height: 2rem;
  margin: 0.5rem 0;
  padding: 0.25rem 0;
}

.prose-img {
  max-width: 600px;
  height: auto;
  display: block;
  margin: 1rem auto;
  transition-property: all;
  transition-duration: 300ms;
  border-radius: 0.5rem;
}

.editor-container img {
  max-width: min(100%, 600px);
  border-radius: 8px;
}

.resize-handle {
  position: absolute;
  width: 0.75rem;
  height: 0.75rem;
  border-width: 2px;
  border-radius: 9999px;
  cursor: nwse-resize;
  border-color: rgba(255, 255, 255, 0.9);
  background-color: rgba(79, 70, 229, 0.5);
  bottom: -6px;
  right: -6px;
  box-shadow: none;
}

/* 选中状态 */
.selected-image {
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.5);
}

/* 协作光标样式 */
.collaboration-cursor__caret {
  border-left: 1px solid;
  border-right: 1px solid;
  margin-left: -1px;
  margin-right: -1px;
  position: relative;
  word-break: normal;
}

.collaboration-cursor__label {
  border-radius: 3px 3px 3px 0;
  font-size: 12px;
  font-style: normal;
  font-weight: 600;
  left: -1px;
  line-height: normal;
  padding: 0.1rem 0.3rem;
  position: absolute;
  top: -1.4em;
  user-select: none;
  white-space: nowrap;
}
</style>
