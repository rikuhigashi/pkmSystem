<script lang="ts" setup>
// ==================== import.ts====================
import sideUtils, { inputForm } from './side'
import sideMenuMethod from '@/views/side/configs/sideMenuMethod'
import noCollingBlur from '@/utils/noCollingBlurMethod'
// ==================== import.ts====================

// ==================== import store ====================
import { useSidebarStore } from '@/stores/sidebar'
import { useRightClickStore } from '@/stores/rightClick'
import { userInputStore } from '@/stores/inputState'
import { useEditorStore } from '@/stores/main/editorStore'
import { userAuthStore } from '@/stores/auth'
import { userVipStore } from '@/stores/vip'
// ==================== import store ====================

// ==================== userStore ====================
const sidebarStore = useSidebarStore()
const rightClickStore = useRightClickStore()
const inputStore = userInputStore()
const editorStore = useEditorStore()
const authStore = userAuthStore()
const useVipStore = userVipStore()
// ==================== userStore ====================

// ==================== import interface ====================
import type { sideListItem } from './types/sideTypes'

// ==================== import interface ====================

// ==================== Component ====================
import AccountSelect from '@/components/sideComponents/AccountSelect.vue'
import RightClickRightClickSelect from '@/components/rightClickSelect/rightClickSelect.vue'
import sideList from '@/components/sideComponents/sideList.vue'
// ==================== Component ====================

// ==================== icon ====================
import { Menu, MenuButton } from '@headlessui/vue'

const { Cog6ToothIcon, PencilSquareIcon, TrashIcon, DocumentDuplicateIcon } = sideUtils.iconMap

// ==================== icon ====================

import { ChevronDownIcon } from '@heroicons/vue/20/solid'
import { onMounted } from 'vue'
import IconLeftArrow from '@/assets/icons/iconLeftArrow.vue'
import router from '@/router'

// ==================== method ====================

const handleVipClick = () => {
  if (!useVipStore.isVipActive) {
    router.push('/sponsorshipView')
    // console.log(authStore.userInfo?.vipActive)
  }
}

// 右键事件
const handleRightClick = (item: sideListItem, event: MouseEvent) => {
  rightClickStore.toggleRightClick(event)
  // 把item中的name传递给selectItem，这个item是从sideLide.vue中传递过来的
  rightClickStore.selectItem = item

  rightClickStore.selectItemId = item.id

  // console.log("this allData:",rightClickStore.selectItem)
}

// 左键事件
const handleLeftClick = (item: sideListItem) => {
  // 把id传输出去
  rightClickStore.leftClickItemId = item.id
  // console.log("sideViewValue:",rightClickStore.leftClickItemId)

  // 设置当前文档ID
  editorStore.setCurrentDocId(rightClickStore.leftClickItemId)

  // 设置当前选中项
  sideUtils.navigation.value = sideUtils.navigation.value.map((navItem) => ({
    ...navItem,
    current: navItem.id === item.id,
  }))
}
// 点击用户名出现的下拉框
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

// 点击右键出现的菜单内容
const rightClickSelectMenuItems = [
  [
    // 保存逻辑在sideView中的handleEnterKey与handleBlur
    {
      icon: PencilSquareIcon,
      label: '添加',
      onClick: () => {
        // 点击后关闭菜单栏
        rightClickStore.closeRightClickMenu()
        // 出现输入框
        inputStore.openInput()

        // console.log("addMethod");
      },
      show: () => true,
    },
    // 保存逻辑在sideList的handleEnterKey与保存逻辑在sideView中的handleEnterKey与handleBlur
    // 为什么在sideList？因为输入框需要在数据上出现
    {
      icon: PencilSquareIcon,
      label: '编辑',
      onClick: () => {
        // const item = rightClickStore.selectItem
        if (rightClickStore.selectItemId) {
          // 点击编辑后激活isEditingActive状态
          inputStore.openEditingInput()
          // console.log("test:",rightClickStore.isEditingActive);

          rightClickStore.closeRightClickMenu()
        }
        // console.log("编辑");
      },
      show: () => true,
    },
    {
      icon: DocumentDuplicateIcon,
      label: '复制',
      onClick: () => {
        if (rightClickStore.selectItem) {
          //只存储被复制的数据
          rightClickStore.copiedItem = {
            ...rightClickStore.selectItem,
          }
          // console.log('复制的数据:', rightClickStore.copiedItem)
        }
        // 关闭右键菜单
        rightClickStore.closeRightClickMenu()

        // console.log("复制");
      },
      show: () => true,
    },
    {
      icon: DocumentDuplicateIcon,
      label: '粘贴',
      onClick: async () => {
        try {
          await sideMenuMethod.copySideData(rightClickStore.copiedItem)
          // console.log(rightClickStore.copiedItem)
          rightClickStore.copiedItem = null // 清空复制的数据
        } catch (error) {
          console.error('copyError', error)
        }

        rightClickStore.closeRightClickMenu()
      },
      show: () => !!rightClickStore.copiedItem,
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
      show: () => true,
    },
  ],


]
// 点击右键出现的菜单内容

// ---------------------- 这是解决enter键和blur事件的冲突问题 ----------------------
// 使用保存方法
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
// ---------------------- 这是解决enter键和blur事件的冲突问题 ----------------------

// ==================== method ====================

onMounted(async () => {
  // 加载side的全部数据
  await sideMenuMethod.loadSideData()

  if (sideUtils.navigation.value?.length > 0) {
    if (sideUtils.navigation.value?.length > 0) {
      // 设置第一个项目为选中状态
      sideUtils.navigation.value = sideUtils.navigation.value.map((item, index) => ({
        ...item,
        current: index === 0,
      }))
    }

    const firstItem = sideUtils.navigation.value[0]
    rightClickStore.leftClickItemId = firstItem.id

    editorStore.setCurrentDocId(rightClickStore.leftClickItemId) // 触发主内容加载
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
            :navigation="sideUtils.navigation"
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
