<template>
  <div class="flex flex-col h-screen bg-gray-50">
    <!-- 顶部协作信息栏 -->
    <div class="flex items-center justify-between px-6 py-3 bg-white shadow-sm border-b">
      <div class="flex items-center space-x-4">
        <div class="flex items-center">
          <div class="text-gray-600 text-sm">房间ID:</div>
          <div class="ml-2 px-3 py-1 bg-blue-50 text-blue-700 rounded-full font-medium text-sm">
            {{ roomId }}
          </div>
        </div>

        <div class="flex items-center">
          <div class="text-gray-600 text-sm">我的名称:</div>
          <div class="ml-2 px-3 py-1 bg-purple-50 text-purple-700 rounded-full font-medium text-sm">
            {{ username }}
          </div>
        </div>

        <!-- 保存按钮 -->
        <button
          @click="saveCollaboration"
          class="btn btn-primary btn-sm shadow-md"
          :disabled="isSaving"
        >
          <template v-if="isSaving">
            <svg
              class="animate-spin -ml-1 mr-2 h-4 w-4 text-white"
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
            >
              <circle
                class="opacity-25"
                cx="12"
                cy="12"
                r="10"
                stroke="currentColor"
                stroke-width="4"
              ></circle>
              <path
                class="opacity-75"
                fill="currentColor"
                d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
              ></path>
            </svg>
            保存中...
          </template>
          <template v-else>
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="h-4 w-4 mr-1"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M5 13l4 4L19 7"
              />
            </svg>
            保存
          </template>
        </button>
      </div>

      <div class="flex items-center space-x-4">
        <div class="flex items-center">
          <div class="w-2.5 h-2.5 rounded-full bg-green-500 mr-2"></div>
          <span class="text-gray-700 text-sm">在线用户: {{ onlineUsers.length }}</span>
        </div>

        <div class="flex items-center text-gray-600 text-sm">
          <template v-if="isSaving">
            <svg
              class="animate-spin -ml-1 mr-2 h-4 w-4 text-gray-600"
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
            >
              <circle
                class="opacity-25"
                cx="12"
                cy="12"
                r="10"
                stroke="currentColor"
                stroke-width="4"
              ></circle>
              <path
                class="opacity-75"
                fill="currentColor"
                d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
              ></path>
            </svg>
            保存中...
          </template>
          <template v-else>
            <svg
              class="w-4 h-4 mr-1 text-green-500"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
              xmlns="http://www.w3.org/2000/svg"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M5 13l4 4L19 7"
              ></path>
            </svg>
            已同步 {{ lastSavedTime }}
          </template>
        </div>
      </div>
    </div>

    <!-- 编辑器区域 -->
    <div class="flex-1 flex flex-col overflow-hidden min-h-screen">
      <div class="flex-1 overflow-hidden relative">
        <bubble-menu
          v-if="editor"
          ref="bubbleMenuRef"
          class="bubble-menu bg-white shadow-lg rounded-md p-1 flex items-center border border-gray-200 gap-1 overflow-x-auto max-w-[80vw]"
          :editor="editor"
          :tippy-options="{ duration: 100, placement: 'top' }"
        >
          <template v-for="(item, index) in toolbarButtons" :key="index">
            <!-- 分隔线 -->
            <div v-if="item.type === 'divider'" class="w-px h-6 bg-gray-200 mx-1"></div>

            <!-- 按钮 -->
            <button
              v-else
              @click="item.action?.()"
              :class="{ 'bg-gray-100': item.isActive?.() }"
              class="p-2 rounded hover:bg-gray-100 tooltip mx-0.5"
              :data-tip="item.title"
            >
              <component :is="item.icon" class="h-5 w-5"></component>
            </button>
          </template>
        </bubble-menu>

        <!-- 链接输入框 -->
        <input
          type="text"
          placeholder="输入链接地址"
          v-if="isInputLink"
          v-model="inputLinkValue"
          @keyup.enter.prevent="confirmLink"
          @blur="confirmLink"
          :style="{
            top: `${linkPosition.top}px`,
            left: `${linkPosition.left}px`,
          }"
          class="input fixed z-50 bg-white p-2 shadow-lg border border-gray-300 rounded-md w-64 focus:ring-2 focus:ring-blue-500 focus:border-transparent"
        />

        <editor-content
          :editor="editor"
          class="min-h-screen max-w-6xl mx-auto p-4 border-2 border-gray-300 rounded-lg shadow-lg overflow-auto"
        />
      </div>
    </div>

    <!-- 右下角在线用户列表 -->
    <div class="fixed bottom-4 right-4 flex flex-col items-end space-y-2">
      <div class="flex -space-x-2">
        <div
          v-for="(user, index) in onlineUsers"
          :key="user.id"
          class="relative group"
          :style="{ zIndex: onlineUsers.length - index }"
        >
          <div
            class="w-10 h-10 rounded-full flex items-center justify-center text-white font-medium text-sm shadow-md"
            :style="{ backgroundColor: user.color }"
          >
            {{ getUserInitials(user.name) }}
          </div>
          <div
            class="absolute bottom-full left-1/2 transform -translate-x-1/2 mb-2 hidden group-hover:block px-2 py-1 bg-gray-800 text-white text-xs rounded whitespace-nowrap"
          >
            {{ user.name }}
          </div>
        </div>
      </div>
      <div
        v-if="onlineUsers.length > 0"
        class="px-3 py-1 bg-white rounded-full shadow text-xs text-gray-600"
      >
        当前在线用户
      </div>
    </div>

    <!-- 退出协作按钮 -->
    <div class="fixed bottom-4 left-4">
      <button @click="exitCollaboration" class="btn btn-sm btn-outline btn-error">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          class="h-4 w-4 mr-1"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"
          />
        </svg>
        退出协作
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { nextTick, onBeforeUnmount, onMounted, ref, toRaw } from 'vue'
import { BubbleMenu, type BubbleMenu as BubbleMenuType } from '@tiptap/vue-3'
import { Editor, EditorContent } from '@tiptap/vue-3'
import Collaboration from '@tiptap/extension-collaboration'
import CollaborationCursor from '@tiptap/extension-collaboration-cursor'
import * as Y from 'yjs'
import { HocuspocusProvider } from '@hocuspocus/provider'
import { upDataMainData } from '@/API/side/sideAPI'

//tiptap导入
import StarterKit from '@tiptap/starter-kit'
import router from '@/router'
import Link from '@tiptap/extension-link'
import { Heading } from '@tiptap/extension-heading'
import { CodeBlock } from '@tiptap/extension-code-block'
import { Blockquote } from '@tiptap/extension-blockquote'
import { HorizontalRule } from '@tiptap/extension-horizontal-rule'
import Underline from '@tiptap/extension-underline'

//store
import { useCollaborationStore } from '@/stores/collaborationStore'
import { createTiptapToken } from '@/API/tiptap/tiptapAPI'

// 从store获取协作信息
const collaborationStore = useCollaborationStore()
const roomId = ref(collaborationStore.room)
const username = ref(collaborationStore.username)

const token = ref({ appId: '', token: '' })

// 编辑器实例
let editor: Editor | undefined = undefined
const doc = new Y.Doc()
const provider = ref<HocuspocusProvider | null>(null)

// 状态管理
const isSaving = ref(false)
const lastSavedTime = ref('刚刚')

// 链接相关状态
const isInputLink = ref(false)
const inputLinkValue = ref('')

const linkPosition = ref({ top: 0, left: 0 })
const bubbleMenuRef = ref<InstanceType<typeof BubbleMenuType> | null>(null)

const onlineUsers = ref<
  Array<{
    id: string
    name: string
    color: string
    avatar?: string
  }>
>([])

// 工具栏按钮配置
const toolbarButtons = ref([
  {
    icon: 'iconBoldFont',
    title: '加粗',
    action: () => editor?.chain().focus().toggleBold().run(),
    isActive: () => editor?.isActive('bold') || false,
  },
  {
    icon: 'iconItalic',
    title: '斜体',
    action: () => editor?.chain().focus().toggleItalic().run(),
    isActive: () => editor?.isActive('italic') || false,
  },
  {
    icon: 'iconUnderline',
    title: '下划线',
    action: () => editor?.chain().focus().toggleUnderline().run(),
    isActive: () => editor?.isActive('underline') ?? false,
    disabled: () => !editor?.can().chain().focus().toggleUnderline().run(),
  },
  {
    icon: 'iconStrikethrough',
    title: '删除线',
    action: () => editor?.chain().focus().toggleStrike().run(),
    isActive: () => editor?.isActive('strike') || false,
  },
  { type: 'divider' },
  {
    icon: 'iconBulletList',
    title: '项目符号',
    action: () => editor?.chain().focus().toggleBulletList().run(),
    isActive: () => editor?.isActive('bulletList') || false,
  },
  {
    icon: 'iconOrderedList',
    title: '编号列表',
    action: () => editor?.chain().focus().toggleOrderedList().run(),
    isActive: () => editor?.isActive('orderedList') || false,
  },
  {
    icon: 'iconCodeBlock',
    title: '代码块',
    action: () => editor?.chain().focus().toggleCodeBlock().run(),
    isActive: () => editor?.isActive('codeBlock') || false,
  },
  {
    icon: 'iconHorizontalRule',
    title: '水平线',
    action: () => editor?.chain().focus().setHorizontalRule().run(),
  },
  { type: 'divider' },
  {
    icon: 'iconFontLink',
    title: '添加链接',
    action: () => setLink(),
    isActive: () => editor?.isActive('link') || false,
  },
])

// 保存方法
const saveCollaboration = async () => {
  if (!editor || !collaborationStore.docId) return

  isSaving.value = true
  try {
    const content = editor.getJSON()
    await upDataMainData(collaborationStore.docId, content)
    lastSavedTime.value = new Date().toLocaleTimeString()
  } catch (error) {
    console.error('保存协作内容失败:', error)
  } finally {
    isSaving.value = false
  }
}

// 设置链接功能
const setLink = () => {
  if (!editor) return
  isInputLink.value = true

  // 计算链接输入框位置
  nextTick(() => {
    if (bubbleMenuRef.value?.$el) {
      const bubbleMenuEl = bubbleMenuRef.value.$el as HTMLElement
      const rect = bubbleMenuEl.getBoundingClientRect()

      // 计算位置：在气泡菜单下方
      linkPosition.value = {
        top: rect.bottom + 10,
        left: rect.left,
      }
    }
  })

  // 如果已有链接，填充值
  if (editor.isActive('link')) {
    inputLinkValue.value = editor.getAttributes('link').href
  }
}

// 确认链接
const confirmLink = () => {
  if (!editor) return

  if (inputLinkValue.value) {
    let href = inputLinkValue.value.trim()
    if (!/^https?:\/\//i.test(href)) {
      href = 'https://' + href
    }
    editor.chain().focus().setLink({ href }).run()
  } else {
    editor.chain().focus().unsetLink().run()
  }

  isInputLink.value = false
  inputLinkValue.value = ''
}

// 生成随机颜色（用于用户光标）
const generateColor = () => {
  const colors = [
    '#4F46E5',
    '#10B981',
    '#F59E0B',
    '#EF4444',
    '#8B5CF6',
    '#EC4899',
    '#06B6D4',
    '#F97316',
  ]
  return colors[Math.floor(Math.random() * colors.length)]
}

// 退出协作功能
const exitCollaboration = async () => {
  if (editor && collaborationStore.docId) {
    try {
      // 保存协作结果到原文档
      const content = editor.getJSON()
      await upDataMainData(collaborationStore.docId, content)

      // 提示用户
      alert('协作内容已保存！')
    } catch (error) {
      console.error('保存协作内容失败:', error)
      alert('保存协作内容失败，请手动复制内容')
    }
  }

  provider.value?.disconnect()
  await router.push({ name: 'home' }) // 跳转到首页
  collaborationStore.clearCollaborationInfo()
}

// 获取用户名称首字母
const getUserInitials = (name: string) => {
  return name
    .split(' ')
    .map((part) => part[0])
    .join('')
    .substring(0, 2)
    .toUpperCase()
}

// 初始化编辑器
onMounted(async () => {
  token.value = await createTiptapToken()

  const rawContent = toRaw(collaborationStore.initialContent)

  try {


    if (rawContent) {
      // 创建临时编辑器将内容写入Yjs文档
      const tempEditor = new Editor({
        extensions: [
          // 必须包含协作扩展以连接到Yjs文档
          Collaboration.configure({
            document: doc,
          }),
          // 其他必要的扩展
          StarterKit,
          Heading.configure({ levels: [1, 2, 3] }),
          Blockquote,
          CodeBlock,
          HorizontalRule,
          Underline,
          Link
        ],
        content: rawContent,
      })

      // 等待编辑器内容应用完成
      await new Promise(resolve => setTimeout(resolve, 100))
      tempEditor.destroy()
    }

    // 检查Yjs文档内容
    console.log('Yjs文档内容:', doc.getXmlFragment('prosemirror').toString())



    // 初始化 Tiptap Collab 提供者
    provider.value = new HocuspocusProvider({
      url: `wss://${token.value.appId}.collab.tiptap.cloud`, // 正确的URL格式
      name: roomId.value,
      token: token.value.token,
      document: doc,
      broadcast: false, // 禁用广播以提高性能

      // 添加WebSocket事件处理
      onOpen: () => console.log('WebSocket连接已打开'),
      onConnect: () => console.log('协作服务已连接'),
      onDisconnect: () => console.log('协作服务已断开'),
      onDestroy: () => console.log('Provider已销毁'),
      onStateless: (payload) => console.log('无状态消息:', payload),

      // 监听用户状态变化
      onAwarenessUpdate: ({ states }) => {
        onlineUsers.value = Array.from(states.entries())
          // eslint-disable-next-line @typescript-eslint/no-unused-vars
          .filter(([_, state]) => state.user) // 确保有用户信息
          .map(([key, state]) => ({
            id: key.toString(), // 转换为字符串
            name: state.user?.name || '匿名用户',
            color: state.user?.color || generateColor(),
          }))

        // console.log('在线用户更新:', onlineUsers.value)
      },
    })

    //设置完整的用户信息
    const userColor = generateColor()
    provider.value.awareness?.setLocalStateField('user', {
      name: username.value,
      color: userColor,
      clientId: provider.value.awareness?.clientID, // 添加客户端ID
      lastActive: Date.now(), // 添加最后活动时间
    })
    // 创建编辑器实例
    editor = new Editor({
      extensions: [
        StarterKit.configure({
          history: false,
        }),
        Heading.configure({
          levels: [1, 2, 3],
        }),
        Blockquote.configure(),
        CodeBlock.configure(),
        HorizontalRule.configure(),
        Underline,
        Collaboration.configure({
          document: doc,
        }),
        CollaborationCursor.configure({
          provider: provider.value,
          user: {
            name: username.value,
            color: userColor,
          },
        }),
        Link.configure({
          openOnClick: true,
          HTMLAttributes: {
            class: 'text-blue-600 hover:underline',
          },
        }),
      ],
      content: {},
      editable: true,
      autofocus: true,
    })

    // 手动连接协作提供者
    provider.value.connect()

    // 定期更新用户活动状态
    const activityInterval = setInterval(() => {
      const currentState = provider.value?.awareness?.getLocalState()?.user || {}
      provider.value?.awareness?.setLocalStateField('user', {
        ...currentState,
        lastActive: Date.now(),
      })
    }, 5000)

    // 在组件卸载时清除定时器
    onBeforeUnmount(() => {
      clearInterval(activityInterval)
    })

    const connectionStatus = ref('connecting')
    provider.value.on('status', ({ status:any }) => {
      connectionStatus.value = status
    })

    // 模拟保存状态
    setInterval(() => {
      isSaving.value = true
      setTimeout(() => {
        isSaving.value = false
        const now = new Date()
        lastSavedTime.value = `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`
      }, 1000)
    }, 30000)
  } catch (e) {
    console.error('初始化协作编辑器失败:', e)
  }
})

// 销毁编辑器实例
onBeforeUnmount(() => {
  editor?.destroy()
  provider.value?.destroy()
  collaborationStore.clearCollaborationInfo()
})
</script>

<style lang="scss">
.ProseMirror pre {
  background-color: #1e1e1e;
  color: #d4d4d4;
  padding: 1rem;
  border-radius: 0.5rem;
  overflow-x: auto;
  font-family: 'Fira Code', Menlo, Consolas, monospace;
  font-size: 14px;
  line-height: 1.5;
}

.ProseMirror code {
  background-color: #1e1e1e;
  color: #d4d4d4;
  padding: 0.2rem 0.4rem;
  border-radius: 0.3rem;
  font-family: 'Fira Code', Menlo, Consolas, monospace;
}

/* 引用样式 */
.ProseMirror blockquote {
  border-left: 3px solid #ccc;
  padding-left: 1rem;
  margin-left: 0;
  font-style: italic;
  color: #555;
}

/* 水平线样式 */
.ProseMirror hr {
  border: none;
  border-top: 2px solid #e2e8f0;
  margin: 1.5rem 0;
}

/* 添加下拉菜单样式 */
.dropdown-content {
  position: absolute;
  top: 100%;
  left: 0;
  margin-top: 0.5rem;
}

/* 工具提示样式 */
.tooltip:before {
  content: attr(data-tip);
  position: absolute;
  bottom: 100%;
  margin-bottom: 0.5rem;
  padding: 0.25rem 0.5rem;
  font-size: 0.75rem;
  background-color: #2d3748;
  color: white;
  border-radius: 0.375rem;
  white-space: nowrap;
  transform: translateX(-50%);
  left: 50%;
  opacity: 0;
  transition: opacity 0.2s;
  pointer-events: none;
}

.tooltip:hover:before {
  opacity: 1;
}

/* 协作光标样式 */
.collaboration-cursor__caret {
  border-left: 2px solid;
  border-right: 2px solid;
  margin-left: -1px;
  margin-right: -1px;
  position: relative;
  word-break: normal;
}

.collaboration-cursor__label {
  border-radius: 0 3px 3px 3px; /* 调整圆角方向 */
  color: white;
  font-size: 12px;
  font-weight: 600;
  left: -1px;
  line-height: normal;
  padding: 0.1rem 0.3rem;
  position: absolute;
  top: 100%; /* 在光标下方 */
  margin-top: 4px; /* 添加一点间距 */
  user-select: none;
  white-space: nowrap;
  font-family: 'Inter', sans-serif;
  z-index: 10; /* 确保标签不被遮挡 */
}

/* 编辑器内容区域样式 */
.ProseMirror {
  min-height: 100%;
  padding: 1.5rem;
  box-sizing: border-box;
  position: relative;
  outline: none;
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 10px;
  height: 10px;
}

::-webkit-scrollbar-track {
  background: #f3f4f6;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: #d1d5db;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #9ca3af;
}

.editor-container {
  min-height: 100%;
}
</style>
