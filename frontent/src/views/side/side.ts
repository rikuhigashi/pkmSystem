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
} from '@heroicons/vue/24/outline'
// --------------------------------- 导入图标 ---------------------------

// --------------------------------- 各种import ---------------------------
import {type Component, computed, ref} from 'vue'
import type {sideListItem} from "@/views/side/types/sideTypes";
import {userAuthStore} from "@/stores/auth";


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
}


// --------------------------------- 各种import ---------------------------


// -------------------------- 侧边栏内容 --------------------
// 获取侧边栏数据

// export const loadSideData = async () => {
//   try {
//     const res = await getSideAllData()
//     navigation.value = res.map((item:sideListItem)=>({
//       ...item,
//       iconComponent:iconMap[item.icon]
//
//     }))
//   } catch (error) {
//     console.log('side后端请求失败!', error)
//   }
// }

// 获取侧边栏数据

// 侧边栏主内容  current为默认选择
export const navigation = ref<sideListItem[]>([])

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





// 点击用户名出现的下拉框
export const accountSelect = [
  {name: '账号详情', href: '#', isNew: false},
  {name: '会员中心', href: '#', isNew: false},
  {name: '消息通知', href: '#', isNew: true},
  {name: '意见反馈', href: '#', isNew: false},
  {name: '退出登录',  action: 'logout', isNew: false},
]
// 点击用户名出现的下拉框

// 暂时用于测试，vip的图标切换
// export const isVipActive = ref(false)
// export const iconVipActive = () => {
//   isVipActive.value = !isVipActive.value
// }
// 暂时用于测试，vip的图标切换

// 输入框状态

export const isInputActive = ref(false)

//

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

export const isVipActive = computed(() => {
  return userAuthStore().userInfo?.vipActive || false
})


// 输入框
export const inputValue = ref()
// 输入框

export const clickTest = () => {
  console.log('这是测试的输出', inputValue.value)
}
// 用于测试点击功能是否能够正确点击

export default {
  navigation,
  // teams,
  iconMap,
  inputValue,
  clickTest,
  inputForm,
  defaultInputForm,
  isVipActive
}
