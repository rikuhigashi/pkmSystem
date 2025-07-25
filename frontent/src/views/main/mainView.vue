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
          <template v-for="(item, index) in toolbarButtons" :key="index">
            <!-- 保持原有按钮结构 -->
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

      <!--    class="editor-container p-6 border-t-0 border-gray-200 rounded-b-lg prose prose-sm max-w-none bg-gray-200"-->

      <editor-content
        :editor="editor"
        class="flex-1 p-6 rounded-box bg-base-100 border border-base-300 shadow-sm prose prose-sm max-w-none min-h-screen relative visible"
        @dragover.prevent="handleDragOver"
        @drop.prevent="handleDrop"
      >
      </editor-content>
    </div>
  </div>
</template>

<script setup lang="ts">
import { nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'

// -------------- tipTap各种导入 --------------
import { useEditor, EditorContent, BubbleMenu } from '@tiptap/vue-3'
import StarterKit from '@tiptap/starter-kit'
import Underline from '@tiptap/extension-underline'
import TextStyle from '@tiptap/extension-text-style'
import Color from '@tiptap/extension-color'
import Link from '@tiptap/extension-link'
import Highlight from '@tiptap/extension-highlight'
import Subscript from '@tiptap/extension-subscript'
import Superscript from '@tiptap/extension-superscript'
import TextAlign from '@tiptap/extension-text-align'
import FontFamily from '@tiptap/extension-font-family'
import ListItem from '@tiptap/extension-list-item'
import OrderedList from '@tiptap/extension-ordered-list'
import ImageResize from 'tiptap-extension-resize-image'
import type { Editor } from '@tiptap/core'
import Placeholder from '@tiptap/extension-placeholder'
// -------------- tipTap各种导入 --------------

// -------------- 高光代码块 --------------
import CodeBlockLowlight from '@tiptap/extension-code-block-lowlight'
import css from 'highlight.js/lib/languages/css'
import js from 'highlight.js/lib/languages/javascript'
import ts from 'highlight.js/lib/languages/typescript'
import html from 'highlight.js/lib/languages/xml'
import { all, createLowlight } from 'lowlight'

import 'highlight.js/styles/vs2015.css'

const lowlight = createLowlight(all)

lowlight.register('html', html)
lowlight.register('css', css)
lowlight.register('javascript', js)
lowlight.register('typescript', ts)
// -------------- 高光代码块 --------------

// -------------- stores --------------
import { userInputStore } from '@/stores/inputState'
import { useEditorStore } from '@/stores/main/editorStore'
import { useAlertStore } from '@/stores/alert'

const inputStore = userInputStore()
const editorStore = useEditorStore()
const alertStore = useAlertStore()
// -------------- stores --------------

// -------------- 引入组件 --------------
import noCollingBlur from '@/utils/noCollingBlurMethod'
import { loadMainData } from '@/views/main/configs/mainConfigs'
// -------------- 引入组件 --------------

// 自定义图片扩展
const CustomImage = ImageResize.extend({
  addAttributes() {
    return {
      ...this.parent?.(),
      width: {
        default: '100%',
        parseHTML: (element) => {
          const currentWidth = element.getAttribute('width')
          return currentWidth || '100%'
        },
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

// ------------------- tiptap编辑器配置 -------------------

const editor = useEditor({
  onUpdate: ({ editor }) => {
    // 获取到编辑器内部内容

    editorStore.editorContent = editor.getJSON()
  },
  extensions: [
    StarterKit.configure({
      codeBlock: false, // 禁用默认的代码块扩展
      listItem: false,
      orderedList: false,
      heading: {
        levels: [1, 2, 3, 4],
        HTMLAttributes: {
          class: 'my-heading',
        },
      },
      paragraph: {
        HTMLAttributes: {
          class: 'prose-paragraph',
          contenteditable: 'true',
        },
      },
    }),
    Placeholder.configure({
      placeholder: ({ node }) => {
        // 针对段落节点显示提示
        if (node.type.name === 'paragraph') {
          return '开始输入内容...'
        }
        return ''
      },
      showOnlyWhenEditable: true, // 在可编辑时显示
      showOnlyCurrent: true, // 在当前空节点显示
      emptyNodeClass: 'is-empty',
    }),
    TextStyle,
    Color,
    Underline,
    Link.configure({
      openOnClick: true,
      autolink: true,
    }),
    CodeBlockLowlight.configure({
      lowlight,
      defaultLanguage: 'typescript',
    }),
    Highlight.configure({ multicolor: true }),
    Subscript,
    Superscript,
    TextAlign.configure({
      types: ['heading', 'paragraph'],
      defaultAlignment: 'left',
    }),
    FontFamily.configure({
      types: ['textStyle'],
    }),
    ListItem,
    OrderedList,
    CustomImage,
    VideoExtension,
  ],
})
// ------------------- tiptap编辑器配置 -------------------

// ------------------- 图片配置 -------------------
import { useImageStore } from '@/stores/imageStore'
import {
  extractImageUrls,
  handleImageDrop as handleImageDropUtil,
  processEditorImages,
} from '@/utils/editorImage/editorImageUtils'

const imageStore = useImageStore()

// Bubble菜单显示条件判断
const shouldShowBubble = ({ editor }: { editor: Editor }) => {
  // 选中图片节点时不显示
  if (editor.isActive('image')) return false
  // 仅在选择文本内容时显示
  return editor.state.selection.content().size > 0
}

// 图片拖放处理
const handleDrop = (event: DragEvent) => {
  if (editor.value) {
    handleImageDropUtil(editor.value, imageStore, event)
  }
}

const handleDragOver = (event: DragEvent) => {
  event.dataTransfer!.dropEffect = 'copy'
}

const extractKeyFromCosUrl = (url: string): string | null => {
  try {
    const urlObj = new URL(url)
    return urlObj.pathname.substring(1) // 移除开头的斜杠
  } catch (e) {
    console.error('解析URL失败:', e, url)
    return null
  }
}

// 保存文档数据
const saveDocument = async () => {
  if (!editor.value) return

  alertStore.showLoading('正在保存中... 请稍后...')

  try {
    // 获取当前编辑器内容
    const content = editor.value.getJSON()

    // 处理并上传所有图片
    const processedContent = await processEditorImages(content, imageStore)
    // 更新编辑器内容
    editor.value.commands.setContent(processedContent)

    // 提取当前文档中的所有图片URL
    const currentImageUrls = extractImageUrls(processedContent)
    imageStore.setCurrentDocImages(currentImageUrls)

    // 获取不再使用的图片
    const unusedImages = imageStore.getUnusedImages()

    // 删除不再使用的图片
    for (const url of unusedImages) {
      // 确保这个URL不是当前文档正在使用的
      if (!currentImageUrls.includes(url)) {
        try {
          // 从URL中提取key
          const key = extractKeyFromCosUrl(url)
          if (key) {
            await deleteImage(key)
            imageStore.removeUploadedImage(url)
          }
        } catch (error) {
          console.error('删除图片失败:', url, error)
        }
      }
    }

    // 保存到store
    editorStore.editorContent = processedContent

    // 调用保存API
    await sideMenuMethod.saveMainData()
    alertStore.showAlert('保存成功', 'success')
  } catch (error) {
    console.error('保存文档失败:', error)
    alertStore.showAlert('保存失败', 'error')
  }
}

// ------------------- 图片配置 -------------------

// ------------------- 工具栏按钮配置 -------------------
const bubbleMenuRef = ref<InstanceType<typeof BubbleMenu> | null>(null)

const toolbarButtons: toolbarItem[] = [
  // 标题正文切换
  {
    type: 'dropdown',
    icon: 'iconHeading',
    title: 'Heading',
    options: [
      // 默认选项
      { label: '正文', value: '0' },
      { label: 'H1', value: '1' },
      { label: 'H2', value: '2' },
      { label: 'H3', value: '3' },
      { label: 'H4', value: '4' },
      { label: '---', value: 'divider' },
      { label: { type: 'icon', name: 'iconBulletList' }, value: 'bullet' },
      { label: { type: 'icon', name: 'iconOrderedList' }, value: 'ordered' },
    ],
    action: (value: string) => {
      if (value === 'bullet') {
        editor.value?.chain().focus().toggleBulletList().run()
      } else if (value === 'ordered') {
        editor.value?.chain().focus().toggleOrderedList().run()
      } else {
        const level = parseInt(value)
        if (level > 0) {
          editor.value
            ?.chain()
            .focus()
            .toggleHeading({ level: level as 1 | 2 | 3 | 4 }) // 类型断言
            .run()
        } else {
          editor.value?.chain().focus().setParagraph().run()
        }
      }
    },
    isActive: (value?: string) => {
      if (value === 'bullet') {
        return editor.value?.isActive('bulletList') ?? false
      } else if (value === 'ordered') {
        return editor.value?.isActive('orderedList') ?? false // 新增有序列表检测
      }

      if (value) {
        const level = parseInt(value)
        return level > 0
          ? (editor.value?.isActive('heading', { level }) ?? false)
          : (editor.value?.isActive('paragraph') ?? false)
      }
      return [1, 2, 3, 4].some((level) => editor.value?.isActive('heading', { level }))
    },
    meta: { type: 'heading' },
  },
  // 字体
  {
    type: 'dropdown',
    icon: 'iconFontFamily',
    title: 'Font Family',
    options: [
      // 默认选项
      { label: '默认字体', value: 'unset' },

      // === 中文字体 ===
      { label: '苹方 (iOS)', value: 'PingFang SC' },
      { label: '微软雅黑 (Windows)', value: 'Microsoft YaHei' },
      { label: '思源黑体', value: 'Noto Sans SC' },
      { label: '思源宋体', value: 'Noto Serif SC' },
      { label: '仿宋', value: 'FangSong' },
      { label: '仿宋_GB2312', value: 'FangSong_GB2312' },
      { label: '站酷小薇', value: 'ZCOOL XiaoWei' },

      // === 英文字体 ===
      { label: 'Arial', value: 'Arial' },
      { label: 'Helvetica', value: 'Helvetica' },
      { label: 'Georgia', value: 'Georgia' },
      { label: 'Lato', value: 'Lato' },
      { label: 'Roboto', value: 'Roboto' },
      { label: 'Times New Roman', value: 'Times New Roman' },

      // === 代码/等宽字体 ===
      { label: 'Fira Code', value: 'Fira Code' },
      { label: 'Source Code Pro', value: 'Source Code Pro' },

      // === 特色字体 ===
      { label: '优雅衬线', value: 'Playfair Display' },
      { label: '现代UI字体 - Inter', value: 'Inter' },
      { label: '标题字体', value: 'Oswald' },
      { label: '手写体', value: 'Dancing Script' },
      { label: '可爱体', value: 'Pacifico' },
      { label: '中文书法', value: 'Zhi Mang Xing' },
      { label: '马善政体', value: 'Ma Shan Zheng' },

      // === 日文字体 ===
      { label: 'Noto Sans JP', value: 'Noto Sans JP' },
      { label: '游ゴシック', value: 'Yu Gothic' },
    ],
    action: (value: string) => {
      if (value) {
        editor.value?.chain().focus().setFontFamily(value).run()
      } else {
        editor.value?.chain().focus().unsetFontFamily().run()
      }
    },
    isActive: (value?: string) => {
      if (value === 'unset') {
        // 默认字体激活条件：未应用任何字体
        return !editor.value?.isActive('textStyle')
      } else if (value) {
        // 其他字体激活条件：精确匹配字体
        return editor.value?.isActive('textStyle', { fontFamily: value }) ?? false
      }
      // 按钮整体激活状态：应用了任意字体
      return editor.value?.isActive('textStyle') ?? false
    },
    meta: { type: 'fontFamily' },
  },
  // 左对齐
  {
    type: 'button',
    icon: 'iconAlignLeft',
    title: 'AlignLeft Ctrl+Shift+L',
    action: () => editor.value?.chain().focus().setTextAlign('left').run(),
    isActive: () => editor.value?.isActive({ textAlign: 'left' }) ?? false,
    disabled: () => !editor.value?.can().chain().focus().setTextAlign('left').run(),
  },
  // 居中对齐
  {
    type: 'button',
    icon: 'iconAlignCenter',
    title: 'AlignCenter Ctrl+Shift+E',
    action: () => editor.value?.chain().focus().setTextAlign('center').run(),
    isActive: () => editor.value?.isActive({ textAlign: 'center' }) ?? false,
    disabled: () => !editor.value?.can().chain().focus().setTextAlign('center').run(),
  },
  // 右对齐
  {
    type: 'button',
    icon: 'iconAlignRight',
    title: 'Justify Ctrl+Shift+R',
    action: () => editor.value?.chain().focus().setTextAlign('right').run(),
    isActive: () => editor.value?.isActive({ textAlign: 'right' }) ?? false,
    disabled: () => !editor.value?.can().chain().focus().setTextAlign('right').run(),
  },
  // 两端对齐
  {
    type: 'button',
    icon: 'iconJustify',
    title: 'AlignRight Ctrl+Shift+J',
    action: () => editor.value?.chain().focus().setTextAlign('justify').run(),
    isActive: () => editor.value?.isActive({ textAlign: 'justify' }) ?? false,
    disabled: () => !editor.value?.can().chain().focus().setTextAlign('justify').run(),
  },
  // 分割线
  { type: 'divider' },
  // 字体加粗
  {
    type: 'button',
    icon: 'iconBoldFont',
    title: 'Bold Ctrl+B',
    action: () => editor.value?.chain().focus().toggleBold().run(),
    isActive: () => editor.value?.isActive('bold') ?? false,
    disabled: () => !editor.value?.can().chain().focus().toggleBold().run(),
  },
  // 字体斜体
  {
    type: 'button',
    icon: 'iconItalic',
    title: 'Italic Ctrl+I',
    action: () => editor.value?.chain().focus().toggleItalic().run(),
    isActive: () => editor.value?.isActive('italic') ?? false,
    disabled: () => !editor.value?.can().chain().focus().toggleItalic().run(),
  },
  // 下划线
  {
    type: 'button',
    icon: 'iconUnderline',
    title: 'Underline Ctrl+U',
    action: () => editor.value?.chain().focus().toggleUnderline().run(),
    isActive: () => editor.value?.isActive('underline') ?? false,
    disabled: () => !editor.value?.can().chain().focus().toggleUnderline().run(),
  },
  // 删除线
  {
    type: 'button',
    icon: 'iconStrikethrough',
    title: 'Strike Ctrl+X',
    action: () => editor.value?.chain().focus().toggleStrike().run(),
    isActive: () => editor.value?.isActive('strike') ?? false,
    disabled: () => !editor.value?.can().chain().focus().toggleStrike().run(),
  },
  // 代码
  {
    type: 'button',
    icon: 'iconCode',
    title: 'Inline_Code Ctrl+E',
    action: () => editor.value?.chain().focus().toggleCode().run(),
    isActive: () => editor.value?.isActive('code') ?? false,
    disabled: () => !editor.value?.can().chain().focus().toggleCode().run(),
  },
  // 代码块
  {
    type: 'button',
    icon: 'iconCodeBlock',
    title: 'Code_Block Ctrl+Alt+C',
    action: () => editor.value?.chain().focus().toggleCodeBlock().run(),
    isActive: () => editor.value?.isActive('codeBlock') ?? false,
    disabled: () => !editor.value?.can().chain().focus().toggleCodeBlock().run(),
  },
  // 给文字设置链接
  {
    type: 'button',
    icon: 'iconFontLink',
    title: 'Link',
    action: () => {
      const bubbleMenuEl = bubbleMenuRef.value?.$el as HTMLElement

      if (bubbleMenuEl) {
        const linkBtn = bubbleMenuEl.querySelector('[data-toolbar-button="link"]') as HTMLElement
        if (linkBtn) {
          const rect = linkBtn.getBoundingClientRect()
          linkPosition.value = {
            top: rect.bottom + 10, // 下移 10px
            left: rect.left, // 对齐按钮左侧
          }
        }
      }

      if (editor.value?.isActive('link')) {
        inputLinkValue.value = editor.value.getAttributes('link').href
      }
      inputStore.toggleLinkInput()
    },
    isActive: () => editor.value?.isActive('link') ?? false,
    disabled: () => !editor.value?.can().setLink({ href: '' }),
    meta: { type: 'link' },
  },
  // 视频插入
  {
    type: 'button',
    icon: 'iconVideo',
    title: 'video',
    action: () => {
      const url = prompt('请输入视频链接（支持Bilibili/YouTube）', '')
      if (url && editor.value) {
        editor.value.chain().focus().setVideo({ src: url }).run()
      }
    },

    meta: { type: 'embed' },
  },

  // 给文字添加背景色
  {
    type: 'button',
    icon: 'iconTextSmudging',
    title: 'Text highlight',
    action: (event?: MouseEvent) => {
      event?.stopPropagation()

      const bubbleMenuEl = bubbleMenuRef.value?.$el as HTMLElement

      const highlightBtn = bubbleMenuEl.querySelector(
        '[data-toolbar-button="highlight"]',
      ) as HTMLElement
      if (highlightBtn) {
        const rect = highlightBtn.getBoundingClientRect()
        colorPickerPosition.value = {
          top: rect.bottom + 10,
          left: rect.left,
        }
      }

      showColorPicker.value = true
      colorActionType.value = 'highlight'
    },
    isActive: () => editor.value?.isActive('highlight') ?? false,
    disabled: () => !editor.value?.can().chain().focus().toggleHighlight().run(),
    meta: { type: 'highlight' },
  },
  // 给文字添加颜色
  {
    type: 'button',
    icon: 'iconFontColor',
    title: 'Text Color',
    action: (event?: MouseEvent) => {
      event?.stopPropagation()
      const bubbleMenuEl = bubbleMenuRef.value?.$el as HTMLElement

      const textColorBtn = bubbleMenuEl.querySelector(
        '[data-toolbar-button="textColor"]',
      ) as HTMLElement

      if (textColorBtn) {
        const rect = textColorBtn.getBoundingClientRect()
        colorPickerPosition.value = {
          top: rect.bottom + 10,
          left: rect.left,
        }
      }
      showColorPicker.value = true
      colorActionType.value = 'text'
    },
    isActive: () => editor.value?.isActive('textStyle') ?? false,
    meta: { type: 'textColor' },
  },
  // 分割线
  { type: 'divider' },
  //   下标
  {
    type: 'button',
    icon: 'iconSubscript',
    title: 'Subscript Ctrl+,',
    action: () => editor.value?.chain().focus().toggleSubscript().run(),
    isActive: () => editor.value?.isActive('subscript') ?? false,
    disabled: () => !editor.value?.can().chain().focus().toggleSubscript().run(),
  },
  //   上标
  {
    type: 'button',
    icon: 'iconSuperscript',
    title: 'Superscript Ctrl+.',
    action: () => editor.value?.chain().focus().toggleSuperscript().run(),
    isActive: () => editor.value?.isActive('superscript') ?? false,
    disabled: () => !editor.value?.can().chain().focus().toggleSuperscript().run(),
  },
]

// ------------------- 工具栏按钮配置 -------------------

// ---------------------- 这是解决enter键和blur事件的冲突问题 ----------------------
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
// ---------------------- 这是解决enter键和blur事件的冲突问题 ----------------------

// ------------------ 颜色选择器 ------------------
import colorSelect from '@/components/mainComponent/colorSelect.vue'
import type { toolbarItem } from '@/views/main/types/mainTypes'
import DropdownMenu from '@/components/mainComponent/DropdownMenu.vue'
import { VideoExtension } from '@/extensions/video'
import sideMenuMethod from '@/views/side/configs/sideMenuMethod'
import { deleteImage } from '@/API/side/sideAPI'

const showColorPicker = ref(false)
const colorPickerPosition = ref({ top: 0, left: 0 })
const colorActionType = ref<'text' | 'highlight'>('text')

// 设置颜色选择器的颜色
const handleColorSelect = (color: string) => {
  // 清除样式
  if (color === 'transparent') {
    if (colorActionType.value === 'highlight') {
      editor.value?.chain().focus().unsetHighlight().run()
    } else {
      editor.value?.chain().focus().unsetColor().run()
    }
    return
  }

  // 设置颜色
  if (colorActionType.value === 'highlight') {
    editor.value?.chain().focus().setHighlight({ color }).run()
  } else {
    editor.value?.chain().focus().setColor(color).run()
  }
}

// 关闭颜色选择器
const handleColorPickerClose = () => {
  showColorPicker.value = false
  colorActionType.value = 'text'
}

// ------------------ 颜色选择器 ------------------

// ------------------ 设置文字链接输入框 ------------------
const linkPosition = ref({ top: 0, left: 0 })

const inputLinkValue = ref('')
const setLinkValue = () => {
  if (inputLinkValue.value) {
    let href = inputLinkValue.value.trim()
    // 设置链接，为链接默认添加https
    if (!/^https?:\/\//i.test(href)) {
      href = 'https://' + href
    }

    editor.value?.chain().focus().setLink({ href }).run()
  } else {
    editor.value?.chain().focus().unsetLink().run()
  }
  inputStore.toggleLinkInput()
  inputLinkValue.value = ''
}

// ------------------ 设置文字链接输入框 ------------------

// ------------------ watch ------------------

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
  { immediate: true },
)

onMounted(() => {
  if (editor.value && !editor.value.getHTML()) {
    editor.value.commands.insertContent('<p></p><p></p>')
  }
})

onMounted(() => {
  if (editor.value && !editor.value.getHTML()) {
    editor.value.commands.insertContent('<p></p><p></p>')
  }

  // 设置保存方法到store
  editorStore.saveDocument = saveDocument
})

onBeforeUnmount(() => {
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
</style>
