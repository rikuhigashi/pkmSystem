import type { toolbarItem } from '@/views/main/types/mainTypes'
import type {Ref} from 'vue'
import {computed} from "vue";

interface ToolbarConfigOptions {
  editor: any
  bubbleMenuRef: any
  linkPosition: Ref<{ top: number; left: number }>
  inputLinkValue: Ref<string>
  colorPickerPosition: Ref<{ top: number; left: number }>
  showColorPicker: Ref<boolean>
  colorActionType: Ref<'text' | 'highlight'>
  openLinkInput: () => void
  openColorPicker: (type: 'text' | 'highlight', event?: MouseEvent) => void
}

export const toolbarButtons = (options: ToolbarConfigOptions): toolbarItem[] => {
  const { editor } = options

  const editorInstance = computed(() => editor.value?.value)

  return [
    // 标题正文切换
    {
      type: 'dropdown',
      icon: 'iconHeading',
      title: 'Heading',
      options: [
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
        if (!editor.value) return

        if (value === 'bullet') {
          editorInstance.value?.chain().focus().toggleBulletList().run()
        } else if (value === 'ordered') {
          editorInstance.value?.chain().focus().toggleOrderedList().run()
        } else {
          const level = parseInt(value)
          if (level > 0) {
            editorInstance.value?.chain().focus().toggleHeading({ level: level as 1 | 2 | 3 | 4 }).run()
          } else {
            editorInstance.value?.chain().focus().setParagraph().run()
          }
        }
      },
      isActive: (value?: string) => {
        if (!editorInstance.value) return false

        if (value === 'bullet') return editorInstance.value?.isActive('bulletList')
        if (value === 'ordered') return editorInstance.value?.isActive('orderedList')

        if (value) {
          const level = parseInt(value)
          return level > 0
            ? editorInstance.value?.isActive('heading', { level })
            : editorInstance.value?.isActive('paragraph')
        }
        return [1, 2, 3, 4].some((level) =>editorInstance.value?.isActive('heading', { level }))
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
          editorInstance.value?.chain().focus().setFontFamily(value).run()
        } else {
          editorInstance.value?.chain().focus().unsetFontFamily().run()
        }
      },
      isActive: (value?: string) => {
        if (value === 'unset') {
          // 默认字体激活条件：未应用任何字体
          return !editorInstance.value?.isActive('textStyle')
        } else if (value) {
          // 其他字体激活条件：精确匹配字体
          return editorInstance.value?.isActive('textStyle', { fontFamily: value }) ?? false
        }
        // 按钮整体激活状态：应用了任意字体
        return editorInstance.value?.isActive('textStyle') ?? false
      },
      meta: { type: 'fontFamily' },
    },
    // 左对齐
    {
      type: 'button',
      icon: 'iconAlignLeft',
      title: 'AlignLeft Ctrl+Shift+L',
      action: () => editorInstance.value?.chain().focus().setTextAlign('left').run(),
      isActive: () => editorInstance.value?.isActive({ textAlign: 'left' }) ?? false,
      disabled: () => !editorInstance.value?.can().chain().focus().setTextAlign('left').run(),
    },
    // 居中对齐
    {
      type: 'button',
      icon: 'iconAlignCenter',
      title: 'AlignCenter Ctrl+Shift+E',
      action: () => editorInstance.value?.chain().focus().setTextAlign('center').run(),
      isActive: () => editorInstance.value?.isActive({ textAlign: 'center' }) ?? false,
      disabled: () => !editorInstance.value?.can().chain().focus().setTextAlign('center').run(),
    },
    // 右对齐
    {
      type: 'button',
      icon: 'iconAlignRight',
      title: 'Justify Ctrl+Shift+R',
      action: () => editorInstance.value?.chain().focus().setTextAlign('right').run(),
      isActive: () =>editorInstance.value?.isActive({ textAlign: 'right' }) ?? false,
      disabled: () => !editorInstance.value?.can().chain().focus().setTextAlign('right').run(),
    },
    // 两端对齐
    {
      type: 'button',
      icon: 'iconJustify',
      title: 'AlignRight Ctrl+Shift+J',
      action: () => editorInstance.value?.chain().focus().setTextAlign('justify').run(),
      isActive: () => editorInstance.value?.isActive({ textAlign: 'justify' }) ?? false,
      disabled: () => !editorInstance.value?.can().chain().focus().setTextAlign('justify').run(),
    },
    // 分割线
    { type: 'divider' },
    // 字体加粗
    {
      type: 'button',
      icon: 'iconBoldFont',
      title: 'Bold Ctrl+B',
      action: () => editorInstance.value?.chain().focus().toggleBold().run(),
      isActive: () => editorInstance.value?.isActive('bold') ?? false,
      disabled: () => !editorInstance.value?.can().chain().focus().toggleBold().run(),
    },
    // 字体斜体
    {
      type: 'button',
      icon: 'iconItalic',
      title: 'Italic Ctrl+I',
      action: () => editorInstance.value?.chain().focus().toggleItalic().run(),
      isActive: () => editorInstance.value?.isActive('italic') ?? false,
      disabled: () => !editorInstance.value?.can().chain().focus().toggleItalic().run(),
    },
    // 下划线
    {
      type: 'button',
      icon: 'iconUnderline',
      title: 'Underline Ctrl+U',
      action: () => editorInstance.value?.chain().focus().toggleUnderline().run(),
      isActive: () => editorInstance.value?.isActive('underline') ?? false,
      disabled: () => !editorInstance.value?.can().chain().focus().toggleUnderline().run(),
    },
    // 删除线
    {
      type: 'button',
      icon: 'iconStrikethrough',
      title: 'Strike Ctrl+X',
      action: () => editorInstance.value?.chain().focus().toggleStrike().run(),
      isActive: () => editorInstance.value?.isActive('strike') ?? false,
      disabled: () => !editorInstance.value?.can().chain().focus().toggleStrike().run(),
    },
    // 代码
    {
      type: 'button',
      icon: 'iconCode',
      title: 'Inline_Code Ctrl+E',
      action: () => editorInstance.value?.chain().focus().toggleCode().run(),
      isActive: () => editorInstance.value?.isActive('code') ?? false,
      disabled: () => !editorInstance.value?.can().chain().focus().toggleCode().run(),
    },
    // 代码块
    {
      type: 'button',
      icon: 'iconCodeBlock',
      title: 'Code_Block Ctrl+Alt+C',
      action: () => editorInstance.value?.chain().focus().toggleCodeBlock().run(),
      isActive: () => editorInstance.value?.isActive('codeBlock') ?? false,
      disabled: () => !editorInstance.value?.can().chain().focus().toggleCodeBlock().run(),
    },
    // 给文字设置链接
    {
      type: 'button',
      icon: 'iconFontLink',
      title: 'Link',
      action: () => options.openLinkInput(),
      isActive: () => editorInstance.value?.isActive('link') ?? false,
      disabled: () => !editorInstance.value?.can().setLink({ href: '' }),
      meta: { type: 'link' },
    },
    // 视频插入
    {
      type: 'button',
      icon: 'iconVideo',
      title: 'video',
      action: () => {
        const url = prompt('请输入视频链接（支持Bilibili/YouTube）', '')
        if (url && editorInstance.value) {
          editorInstance.value?.chain().focus().setVideo({ src: url }).run()
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
        options.openColorPicker('highlight', event)
      },
      isActive: () => editorInstance.value?.isActive('highlight') ?? false,
      disabled: () => !editorInstance.value?.can().chain().focus().toggleHighlight().run(),
      meta: { type: 'highlight' },
    },
    // 给文字添加颜色
    {
      type: 'button',
      icon: 'iconFontColor',
      title: 'Text Color',
      action: (event?: MouseEvent) => {
        options.openColorPicker('text', event)
      },
      isActive: () => editorInstance.value?.isActive('textStyle') ?? false,
      meta: { type: 'textColor' },
    },
    // 分割线
    { type: 'divider' },
    // 下标
    {
      type: 'button',
      icon: 'iconSubscript',
      title: 'Subscript Ctrl+,',
      action: () =>editorInstance.value?.chain().focus().toggleSubscript().run(),
      isActive: () => editorInstance.value?.isActive('subscript') ?? false,
      disabled: () => !editorInstance.value?.can().chain().focus().toggleSubscript().run(),
    },
    // 上标
    {
      type: 'button',
      icon: 'iconSuperscript',
      title: 'Superscript Ctrl+.',
      action: () => editorInstance.value?.chain().focus().toggleSuperscript().run(),
      isActive: () => editorInstance.value?.isActive('superscript') ?? false,
      disabled: () => !editorInstance.value?.can().chain().focus().toggleSuperscript().run(),
    },
  ]
}
