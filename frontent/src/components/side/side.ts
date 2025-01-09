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
    Cog6ToothIcon,
    XMarkIcon,
}

// 侧边栏主内容
export const navigation = [
    {
        name: '全部笔记',
        href: '#',
        icon: HomeIcon,
        current: true,
        onclick: (event: MouseEvent) => {
            console.log("1");
        },
    },
    {
        name: '2',
        href: '#',
        icon: UsersIcon,
        current: false,
        onclick: (event: MouseEvent) => {
            console.log("1");
        },
    },
    {
        name: '3',
        href: '#',
        icon: FolderIcon,
        current: false,
        onclick: (event: MouseEvent) => {
            console.log("1");
        },
    },
    {
        name: '4',
        href: '#',
        icon: CalendarIcon,
        current: false,
        onclick: (event: MouseEvent) => {
            console.log("1");
        },
    },
    {
        name: '5',
        href: '#',
        icon: DocumentDuplicateIcon,
        current: false,
        onclick: (event: MouseEvent) => {
            console.log("1");
        },
    },
    {
        name: 'test',
        href: '#',
        icon: ChartPieIcon,
        current: false,
        onclick: (event: MouseEvent) => {
            console.log("1");
        },
    },
]

// 主内容内部的分组
export const teams = [
    {
        id: 1,
        name: '回收站',
        href: '#',
        initial: 't',
        current: false,
        onclick: (event: MouseEvent) => {
            console.log("1");
        },
    },
    {
        id: 2,
        name: 'Tailwind Labs',
        href: '#',
        initial: 'T',
        current: false,
        onclick: (event: MouseEvent) => {
            console.log("1");
        },
    },
    {
        id: 3,
        name: '回收站3',
        href: '#',
        initial: 'W',
        current: false,
        onclick: (event: MouseEvent) => {
            console.log("1");
        },
    },
]
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


// 点击右键出现的菜单内容
const rightClickSelectMenuItems = [
    [
        {
            icon: PencilSquareIcon,
            label: '编辑',
            onClick: (event: MouseEvent) => {
                console.log("编辑");
            },
        },
        {
            icon: DocumentDuplicateIcon,
            label: '复制',
            onClick: (event: MouseEvent) => {
                console.log("复制");
            },
        },
    ],
    [
        {
            icon: ArchiveBoxIcon,
            label: '档案',
            onClick: (event: MouseEvent) => {
                console.log("档案");
            },
        },
        {
            icon: ArrowRightCircleIcon,
            label: '移动',
            onClick: (event: MouseEvent) => {
                console.log("移动");
            },
        },
    ],
    [
        {
            icon: UserPlusIcon,
            label: '共享',
            onClick: (event: MouseEvent) => {
                console.log("共享");
            },
        },
        {
            icon: HeartIcon,
            label: '添加到收藏夹',
            onClick: (event: MouseEvent) => {
                console.log("添加到收藏夹");
            },
        },
    ],
    [
        {
            icon: TrashIcon,
            label: '删去',
            onClick: (event: MouseEvent) => {
                console.log("删去");
            },
        },
    ],
];
// 点击右键出现的菜单内容



// 用于测试点击功能是否能够正确点击
export const clickTest = () => {
    console.log("被点击了！");
}
// 用于测试点击功能是否能够正确点击




export default {
    navigation,
    teams,
    icon,
    accountSelect,
    isVipActive,
    iconVipActive,
    rightClickSelectMenuItems,
    clickTest,
}