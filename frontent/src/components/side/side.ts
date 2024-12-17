import {
    CalendarIcon,
    ChartPieIcon,
    Cog6ToothIcon,
    DocumentDuplicateIcon,
    FolderIcon,
    HomeIcon,
    UsersIcon,
    XMarkIcon,
} from '@heroicons/vue/24/outline'
import { ref } from 'vue'

export const icon = {
    Cog6ToothIcon,
    XMarkIcon,
}


export const navigation = [
    { name: 'Dashboard', href: '#', icon: HomeIcon, current: true },
    { name: 'Team', href: '#', icon: UsersIcon, current: false },
    { name: 'Projects', href: '#', icon: FolderIcon, current: false },
    { name: 'Calendar', href: '#', icon: CalendarIcon, current: false },
    { name: 'Documents', href: '#', icon: DocumentDuplicateIcon, current: false },
    { name: 'Reports', href: '#', icon: ChartPieIcon, current: false },
]

export const teams = [
    { id: 1, name: 'Heroicons', href: '#', initial: 'H', current: false },
    { id: 2, name: 'Tailwind Labs', href: '#', initial: 'T', current: false },
    { id: 3, name: 'Workcation', href: '#', initial: 'W', current: false },
]

export const clickSideLeftArrow = () => {
    console.log("被点击了！");
}

export const userNavigation = [
    { name: '菜单1', href: '#' },
    { name: '菜单2', href: '#' },
    { name: '菜单3', href: '#' },
    { name: '菜单4', href: '#' },
    { name: '菜单5', href: '#' },
    { name: '菜单6', href: '#' },
    { name: '退出登录', href: '#' },
]


export const isVipActive = ref(true)
export const iconVipActive = () =>{
    isVipActive.value =! isVipActive.value
}