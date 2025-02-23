<script lang="ts" setup>
// ==================== import .ts====================
import sideUtils from './side'
import { inputValue } from './side'
import sideMenuMethod from '@/utils/sideMenuMethod'
// ==================== import .ts====================

// ==================== import store ====================
import { useSidebarStore } from '@/stores/sidebar'
import { useRightClickStore } from '@/stores/rightClick'
import { userInputStore } from '@/stores/inputState'
// ==================== import store ====================


// ==================== Component ====================
import clickAccountSelect from './sideComponents/clickAccountSelect.vue'
import rightClickSelect from '../rightClickSelect/select.vue'
import sideList from './sideComponents/sideList.vue'
// ==================== Component ====================

// ==================== icon ====================
import {
    Menu,
    MenuButton,
} from '@headlessui/vue'

const {
    Cog6ToothIcon,
    ArchiveBoxIcon,
    ArrowRightCircleIcon,
    HeartIcon,
    PencilSquareIcon,
    TrashIcon,
    UserPlusIcon,
    DocumentDuplicateIcon
} = sideUtils.icon
// ==================== icon ====================


import { ChevronDownIcon } from '@heroicons/vue/20/solid'
import { onUpdated, ref } from 'vue'



// ==================== userStore ====================
const sidebarStore = useSidebarStore()
const rightClickStore = useRightClickStore()
const inputStore = userInputStore()
// ==================== store ====================



// ==================== method ====================



const handleRightClick = (item: any, event: MouseEvent) => {
    rightClickStore.toggleRightClick(event)
    // 把item中的name传递给selectItem，这个item是从sideLide.vue中传递过来的
    rightClickStore.selectItem = item.name
}


// 点击右键出现的菜单内容
const rightClickSelectMenuItems = [
    [
        {
            icon: PencilSquareIcon,
            label: '添加',
            onClick: (event: MouseEvent) => {
                // 点击后关闭菜单栏
                rightClickStore.closeRightClickMenu()
                // 出现输入框
                inputStore.openInput()

                // console.log("addMethod");
            },
            show: () => true
        },
        {
            icon: PencilSquareIcon,
            label: '编辑',
            onClick: (event: MouseEvent) => {
                const item = rightClickStore.selectItem
                if (item) {
                    // 点击编辑后激活isEditingActive状态
                    inputStore.isEditingActive = true
                    // console.log("test:",rightClickStore.isEditingActive); //可删除

                    // 点击后关闭菜单栏,一定要放到最后确保上面的代码执行完毕
                    rightClickStore.closeRightClickMenu()
                }
                // console.log("编辑");
            },
            show: () => true
        },
        {
            icon: DocumentDuplicateIcon,
            label: '复制',
            onClick: (event: MouseEvent) => {
                rightClickStore.copiedItem = rightClickStore.selectItem
                // 测试输出复制的值
                // console.log("copy:", rightClickStore.copiedItem);

                // 关闭右键菜单
                rightClickStore.closeRightClickMenu()

                // console.log("复制");
            },
            show: () => true
        },
        {
            icon: DocumentDuplicateIcon,
            label: '粘贴',
            onClick: (event: MouseEvent) => {
                // 测试输出复制的值
                // console.log("copiedBefore:", rightClickStore.copiedItem);

                if (rightClickStore.copiedItem) {
                    sideUtils.navigation.value.push({
                        name: rightClickStore.copiedItem,
                        href: '#',
                        icon: sideUtils.icon.HomeIcon,
                        current: false,
                    })
                    // 粘贴后清空复制的值
                    rightClickStore.copiedItem = null
                    // 关闭右键菜单
                    rightClickStore.closeRightClickMenu()
                }

                // 测试粘贴后的值
                // console.log("copiedAfter:", rightClickStore.copiedItem);
                // console.log("粘贴");
            },
            show: () => !!rightClickStore.copiedItem,
        },
    ],
    [
        {
            icon: ArchiveBoxIcon,
            label: '档案',
            onClick: (event: MouseEvent) => {
                console.log("档案");
            },
            show: () => true
        },
        {
            icon: ArrowRightCircleIcon,
            label: '移动',
            onClick: (event: MouseEvent) => {
                console.log("移动");
            },
            show: () => true
        },
    ],
    [
        {
            icon: UserPlusIcon,
            label: '共享',
            onClick: (event: MouseEvent) => {
                console.log("共享");
            },
            show: () => true
        },
        {
            icon: HeartIcon,
            label: '添加到收藏夹',
            onClick: (event: MouseEvent) => {
                console.log("添加到收藏夹");
            },
            show: () => true
        },
    ],
    [
        {
            icon: TrashIcon,
            label: '删去',
            onClick: (event: MouseEvent) => {
                // console.log("删去");

                const itemToDelete = rightClickStore.selectItem;
                if (itemToDelete) {
                    sideUtils.navigation.value = sideUtils.navigation.value.filter(item => item.name !== itemToDelete);
                    console.log("删去", itemToDelete);
                }
                rightClickStore.closeRightClickMenu();

            },
            show: () => true
        },
    ],
];
// 点击右键出现的菜单内容


// 这是解决enter键和blur事件的冲突问题
const handleEnterKey = () => {
    sideMenuMethod.handleKeyup()
    addSaveData();
}

const handleBlur = () => {
    if (!sideMenuMethod.isEnterPressed.value) {
        addSaveData();
    }
    sideMenuMethod.handleBlur()
}

// 判断输入是否为空，为空给与默认值
const addSaveData = () => {
    // 检查输入框的值
    if (!inputValue.value) {
        inputValue.value = '默认创建';
    }
    // 调用sideMenuMethod.ts的保存方法
    sideMenuMethod.saveInputDate();
}
// 这是解决enter键和blur事件的冲突问题








// ==================== method ====================





</script>


<template>


    <div :class="['lg:w-72 lg:fixed lg:inset-y-0 lg:flex lg:flex-col transition-transform ease-in-out duration-500 sticky top-0',
        sidebarStore.sidebarOpen ? '-translate-x-sidebarTranlate' : ''
    ]">
        <div class="flex grow flex-col gap-y-5 overflow-y-auto bg-gray-900 px-6 pb-4">
            <div class="flex h-16 shrink-0 items-center group">
                <Menu as="div" class="relative">
                    <MenuButton class="-m-1.5 flex items-center p-1.5">
                        <span class="sr-only">打开菜单</span>
                        <img class="size-8 rounded-full bg-gray-50"
                            src="https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80"
                            alt="" />
                        <span class="hidden lg:flex lg:items-center">
                            <span class="ml-4 text-sm/6 font-semibold text-gray-400" aria-hidden="true">这是用户Id</span>
                            <ChevronDownIcon class="ml-2 size-5 text-gray-400" aria-hidden="true" />
                            <component :is="sideUtils.isVipActive.value ? 'iconVipAct' : 'iconVip'"
                                class="h-6 w-auto ml-2" @click="sideUtils.iconVipActive"></component>
                        </span>
                    </MenuButton>

                    <clickAccountSelect :account-select="sideUtils.accountSelect" />

                </Menu>

                <iconLeftArrow
                    class="h-4 w-auto ml-auto hidden group-hover:block transition-opacity duration-800 opacity-0 group-hover:opacity-100"
                    @click="sidebarStore.toggleSidebar" />
            </div>


            <nav class="relative flex flex-1 flex-col">


                <rightClickSelect class="absolute" :menu-items=rightClickSelectMenuItems
                    :menu-position="rightClickStore.menuPosition" v-if="rightClickStore.isRightClickActive" />





                <ul role="list" class="flex flex-1 flex-col gap-y-7">

                    <sideList :navigation="sideUtils.navigation" @right-click="handleRightClick" />

                    <!-- @blur="inputStore.closeInput" -->
                    <input type="text" v-model="inputValue" placeholder="请输入名称"
                        class="input input-bordered input-sm w-4/5" v-if="inputStore.isInputActive" @click.stop
                        @keyup.enter.prevent="handleEnterKey" @blur="handleBlur" v-focus="true" />



                    <li class="mt-auto">
                        <a href="#"
                            class="group -mx-2 flex gap-x-3 rounded-md p-2 text-sm/6 font-semibold text-gray-400 hover:bg-gray-800 hover:text-white">
                            <Cog6ToothIcon class="size-6 shrink-0" aria-hidden="true" />
                            Settings
                        </a>
                    </li>

                </ul>



            </nav>





        </div>

    </div>

</template>
