import {
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
} from '@heroicons/vue/24/outline'
import { ref } from 'vue'

export const icon = {
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


// 侧边栏主内容
export const navigation = ref([
    {
        name: '全部笔记',
        href: '#',
        icon: HomeIcon,
        current: true,

    },
    {
        name: '2',
        href: '#',
        icon: UsersIcon,
        current: false,

    },
    {
        name: '3',
        href: '#',
        icon: FolderIcon,
        current: false,

    },
    {
        name: '4',
        href: '#',
        icon: CalendarIcon,
        current: false,

    },
    {
        name: '5',
        href: '#',
        icon: DocumentDuplicateIcon,
        current: false,

    },
    {
        name: 'test',
        href: 'testView',
        icon: ChartPieIcon,
        current: false,

    },
])

// 主内容内部的分组
// export const teams = [
//     {
//         id: 1,
//         name: '回收站',
//         href: '#',
//         initial: 't',
//         current: false,

//     },
//     {
//         id: 2,
//         name: 'Tailwind Labs',
//         href: '#',
//         initial: 'T',
//         current: false,

//     },
//     {
//         id: 3,
//         name: '回收站3',
//         href: '#',
//         initial: 'W',
//         current: false,

//     },
// ]
// 侧边栏主内容



// 点击用户名出现的下拉框
export const accountSelect = [
    { name: '账号详情', href: '#', isNew: false },
    { name: '会员中心', href: '#', isNew: false },
    { name: '消息通知', href: '#', isNew: true },
    { name: '意见反馈', href: '#', isNew: false },
    { name: '退出登录', href: '#', isNew: false },
]
// 点击用户名出现的下拉框



// 暂时用于测试，vip的图标切换
export const isVipActive = ref(true)
export const iconVipActive = () => {
    isVipActive.value = !isVipActive.value
    console.log("被点击了！", isVipActive.value);
}
// 暂时用于测试，vip的图标切换




// 输入框状态

export const isInputActive = ref(false)

// 

// 添加方法
export const addItem = () => {
    if (inputValue.value) {
        navigation.value.push({
            name: inputValue.value,
            href: '#',
            icon: HomeIcon,
            current: false,
        })
    }
    // console.log('添加数据');
}
// 添加方法

// 输入框
export const inputValue = ref('')
// 输入框





export const clickTest = () => {
    console.log("这是测试的输出",inputValue.value);
}
// 用于测试点击功能是否能够正确点击




export default {
    navigation,
    // teams,
    icon,
    accountSelect,
    isVipActive,
    iconVipActive,
    addItem,
    inputValue,
    clickTest,

}