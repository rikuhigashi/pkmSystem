<script lang="ts" setup>
// ==================== 导入模块 ====================
import { ref, onMounted } from 'vue'
import {  useRouter } from 'vue-router'
import {
  Cog6ToothIcon,
  PencilSquareIcon,
  TrashIcon,
  DocumentDuplicateIcon,
  ShareIcon
} from '@heroicons/vue/24/outline'
import { ChevronDownIcon } from '@heroicons/vue/20/solid'
import { Menu, MenuButton } from '@headlessui/vue'

// ==================== 导入工具和存储 ====================
import sideUtils, { inputForm } from './side'
import sideMenuMethod from '@/views/side/configs/sideMenuMethod'
import noCollingBlur from '@/utils/noCollingBlurMethod'
import { useSidebarStore } from '@/stores/sidebar'
import { useRightClickStore } from '@/stores/rightClick'
import { userInputStore } from '@/stores/inputState'
import { useEditorStore } from '@/stores/main/editorStore'
import { userAuthStore } from '@/stores/auth'
import { userVipStore } from '@/stores/vip'
import type { sideListItem } from './types/sideTypes'
import type { ShareFormData } from '@/types/knowledgeTypes'

// ==================== 导入组件 ====================
import AccountSelect from '@/components/sideComponents/AccountSelect.vue'
import RightClickRightClickSelect from '@/components/rightClickSelect/rightClickSelect.vue'
import sideList from '@/components/sideComponents/sideList.vue'
// import ShareKnowledgeModal from '@/components/sideComponents/ShareKnowledgeModal.vue'
import ShareKnowledgeModal from '@/views/side/sideComponents/ShareKnowledgeModal.vue'
import IconLeftArrow from '@/assets/icons/iconLeftArrow.vue'

// ==================== 初始化存储 ====================
const sidebarStore = useSidebarStore()
const rightClickStore = useRightClickStore()
const inputStore = userInputStore()
const editorStore = useEditorStore()
const authStore = userAuthStore()
const useVipStore = userVipStore()
const router = useRouter()

// ==================== 状态管理 ====================
const showShareModal = ref(false)
const shareForm = ref<ShareFormData>({
  title: '',
  isEncrypted: false,
  price: 0,
  tags: []
})

// ==================== 方法定义 ====================
const handleVipClick = () => {
  if (!useVipStore.isVipActive) {
    router.push('/sponsorshipView')
  }
}

const handleRightClick = (item: sideListItem, event: MouseEvent) => {
  event.preventDefault()
  rightClickStore.toggleRightClick(event)
  rightClickStore.selectItem = item
  rightClickStore.selectItemId = item.id
}

const handleLeftClick = (item: sideListItem) => {
  rightClickStore.leftClickItemId = item.id
  editorStore.setCurrentDocId(rightClickStore.leftClickItemId)

  sideUtils.sideNavigation.value = sideUtils.sideNavigation.value.map(navItem => ({
    ...navItem,
    current: navItem.id === item.id
  }))
}

const handleShareSubmit = (formData: ShareFormData) => {
  // 调用API将当前文档内容分享到知识广场
  console.log('分享内容:', {
    ...formData,
    content: editorStore?.editorContent
  })

  // 重置表单
  shareForm.value = {
    title: '',
    isEncrypted: false,
    price: 0,
    tags: []
  }
}

const accountSelect = [
  { name: '账号详情', href: '/AccountDetailView', isNew: false },
  { name: '消息通知', href: '/home', isNew: true },
  { name: '意见反馈', href: '/home', isNew: false },
  { name: '赞助我', href: '/sponsorshipView', isNew: false },
  { name: '退出登录', action: 'logout', isNew: false },
]

const handleAction = async (action: string) => {
  if (action === 'logout') {
    await authStore.logout()
  }
}

const handleEnterKey = () => {
  noCollingBlur.handleKeyup()
  sideMenuMethod.saveInputDate()
}

const handleBlur = () => {
  if (!noCollingBlur.isEnterPressed.value) {
    sideMenuMethod.saveInputDate()
  }
  noCollingBlur.handleBlur()
}

// ==================== 右键菜单项 ====================
const rightClickSelectMenuItems = [
  [
    {
      icon: PencilSquareIcon,
      label: '添加',
      onClick: () => {
        rightClickStore.closeRightClickMenu()
        inputStore.openInput()
      },
      show: () => true
    },
    {
      icon: PencilSquareIcon,
      label: '编辑',
      onClick: () => {
        if (rightClickStore.selectItemId) {
          inputStore.openEditingInput()
          rightClickStore.closeRightClickMenu()
        }
      },
      show: () => true
    },
    {
      icon: DocumentDuplicateIcon,
      label: '复制',
      onClick: () => {
        if (rightClickStore.selectItem) {
          rightClickStore.copiedItem = { ...rightClickStore.selectItem }
          rightClickStore.closeRightClickMenu()
        }
      },
      show: () => true
    },
    {
      icon: DocumentDuplicateIcon,
      label: '粘贴',
      onClick: async () => {
        try {
          if (rightClickStore.copiedItem) {
            await sideMenuMethod.copySideData(rightClickStore.copiedItem)
            rightClickStore.copiedItem = null
          }
        } catch (error) {
          console.error('copyError', error)
        }
        rightClickStore.closeRightClickMenu()
      },
      show: () => !!rightClickStore.copiedItem
    },
  ],
  [
    {
      icon: TrashIcon,
      label: '删去',
      onClick: () => {
        sideMenuMethod.savaDeleteSideData(rightClickStore.selectItemId)
        rightClickStore.closeRightClickMenu()
      },
      show: () => true
    },
  ],
  [
    {
      icon: ShareIcon,
      label: '分享到知识广场',
      onClick: () => {
        if (rightClickStore.selectItem) {

          shareForm.value.title = rightClickStore.selectItem.name
        }
        showShareModal.value = true
        rightClickStore.closeRightClickMenu()
      },
      show: () => true
    },
  ],
]

// ==================== 生命周期钩子 ====================
onMounted(async () => {
  await sideMenuMethod.loadSideData()

  if (sideUtils.sideNavigation.value?.length > 0) {
    sideUtils.sideNavigation.value = sideUtils.sideNavigation.value.map((item, index) => ({
      ...item,
      current: index === 0,
    }))

    const firstItem = sideUtils.sideNavigation.value[0]
    rightClickStore.leftClickItemId = firstItem.id
    editorStore.setCurrentDocId(rightClickStore.leftClickItemId)
  }
})
</script>

<template>
  <div
    :class="[
      'lg:w-72 lg:fixed lg:inset-y-0 lg:flex lg:flex-col transition-transform ease-in-out duration-500 sticky top-0 z-50 bg-gray-900',
      sidebarStore.sidebarOpen ? '-translate-x-full' : '',
    ]"
  >
    <!-- 右键菜单栏 -->
    <RightClickRightClickSelect
      class="absolute z-[99] min-w-[160px] shadow-lg bg-base-100 rounded-box"
      :menu-items="rightClickSelectMenuItems"
      :menu-position="rightClickStore.menuPosition"
      v-if="rightClickStore.isRightClickActive"
    />

    <div class="flex grow flex-col gap-y-5 overflow-y-auto px-6 pb-4 ring-1 ring-gray-700">
      <!-- 用户头像和菜单 -->
      <div class="flex h-16 shrink-0 items-center group">
        <Menu as="div" class="relative">
          <MenuButton class="-m-1.5 flex items-center p-1.5">
            <span class="sr-only">打开菜单</span>
            <img
              class="size-8 rounded-full bg-gray-50"
              src="https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80"
              alt=""
            />
            <span class="hidden lg:flex lg:items-center">
              <span class="ml-4 text-sm/6 font-semibold text-gray-400" aria-hidden="true">
                {{ authStore.userInfo?.username || '未登录用户' }}
              </span>
              <ChevronDownIcon class="ml-2 size-5 text-gray-400" aria-hidden="true" />
              <component
                :is="useVipStore.isVipActive ? 'iconVipAct' : 'iconVip'"
                class="h-6 w-auto ml-2 hover:cursor-pointer"
                @click="handleVipClick()"
              />
            </span>
          </MenuButton>
          <AccountSelect :account-select="accountSelect" @action="handleAction" />
        </Menu>

        <IconLeftArrow
          class="h-4 w-auto ml-auto hidden group-hover:block group-hover:opacity-100 transition-opacity duration-300 opacity-0 hover:cursor-pointer"
          @click="sidebarStore.toggleSidebar"
        />
      </div>

      <!-- 导航列表 -->
      <nav class="relative flex flex-1 flex-col">
        <ul role="list" class="flex flex-1 flex-col gap-y-7">
          <sideList
            :navigation="sideUtils.sideNavigation"
            @right-click="handleRightClick"
            @left-click="handleLeftClick"
          />

          <!-- 输入框 -->
          <input
            type="text"
            v-model="inputForm.name"
            placeholder="请输入名称"
            class="input input-bordered input-sm w-4/5 bg-base-100 text-base-content focus:ring-2 focus:ring-primary mt-2"
            v-if="inputStore.isInputActive"
            @keyup.enter.prevent="handleEnterKey"
            @blur="handleBlur"
            v-focus="true"
          />

          <!-- 分享模态框 -->
          <ShareKnowledgeModal
            :show="showShareModal"
            :form="shareForm"
            @close="showShareModal = false"
            @submit="handleShareSubmit"
          />

          <!-- 底部设置 -->
          <li class="mt-auto">
            <a
              href="#"
              class="group -mx-2 flex gap-x-3 rounded-md p-2 text-sm/6 font-semibold text-gray-400 hover:bg-gray-800 hover:text-white transition-colors duration-200"
            >
              <Cog6ToothIcon class="size-6 shrink-0" aria-hidden="true" />
              Settings
            </a>
          </li>
        </ul>
      </nav>
    </div>
  </div>
</template>
