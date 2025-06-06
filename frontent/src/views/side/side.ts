// --------------------------------- 导入图标 ---------------------------
import {
  ArchiveBoxIcon,
  ArrowRightCircleIcon,
  CalendarIcon,
  ChartPieIcon,
  Cog6ToothIcon,
  DocumentDuplicateIcon,
  FolderIcon,
  HeartIcon,
  HomeIcon,
  PencilSquareIcon,
  TrashIcon,
  UserPlusIcon,
  UsersIcon,
  XMarkIcon,
  ShareIcon
} from '@heroicons/vue/24/outline'
// --------------------------------- 导入图标 ---------------------------

// --------------------------------- 各种import ---------------------------
import { type Component, ref } from 'vue'
import type { sideListItem } from '@/views/side/types/sideTypes'
// --------------------------------- 各种import ---------------------------

// --------------------------------- 图标映射 ---------------------------
export const iconMap: Record<string, Component> = {
  CalendarIcon,
  ChartPieIcon,
  Cog6ToothIcon,
  DocumentDuplicateIcon,
  FolderIcon,
  HomeIcon,
  UsersIcon,
  XMarkIcon,
  ArchiveBoxIcon,
  ArrowRightCircleIcon,
  HeartIcon,
  PencilSquareIcon,
  TrashIcon,
  UserPlusIcon,
  ShareIcon
}
// --------------------------------- 各种import ---------------------------

// -------------------------- 侧边栏内容 --------------------

// 侧边栏主内容  current为默认选择
export const sideNavigation = ref<sideListItem[]>([])

// 主内容内部的分组
// export const teams = [
//     {
//         id: 1,
//         name: '回收站',
//         href: '#',
//         initial: 't',
//         current: false,
//     }
// ]

// -------------------------- 侧边栏内容 --------------------



// 添加方法

export const inputForm = ref({
  name: '',
  herf: '#',
  icon: 'HomeIcon',
})

export const defaultInputForm = ref({
  name: '默认创建',
  herf: '#',
  icon: 'HomeIcon',
})

// 输入框
export const inputValue = ref()
// 输入框

export default {
  sideNavigation,
  // teams,
  iconMap,
  inputValue,
  inputForm,
  defaultInputForm,
}
