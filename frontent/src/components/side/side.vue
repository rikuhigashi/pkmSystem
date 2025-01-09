<script lang="ts" setup>
import sideUtils from './side'
import { useSidebarStore } from '@/stores/sidebar'
import { useRightClickStore } from '@/stores/rightClick'

import clickAccountSelect from './sideComponents/clickAccountSelect.vue'
import rightClickSelect from '../rightClickSelect/select.vue'
import sideList from './sideComponents/sideList.vue'

import {
    Menu,
    MenuButton,
} from '@headlessui/vue'

import { ChevronDownIcon } from '@heroicons/vue/20/solid'
import { onMounted } from 'vue'




const { XMarkIcon, Cog6ToothIcon } = sideUtils.icon

const sidebarStore = useSidebarStore()
const rightClickStore = useRightClickStore()


onMounted(() => {
    // 移除拖动行为
    document.addEventListener('dragstart', (event) => {
        event.preventDefault();
    })
});

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


            <nav class="relative flex flex-1 flex-col" >


                <rightClickSelect class="absolute" :menu-items=sideUtils.rightClickSelectMenuItems
                    :menu-position="rightClickStore.menuPosition" v-if="rightClickStore.isRightChickActive" />



                <ul role="list" class="flex flex-1 flex-col gap-y-7" >

                    <sideList :navigation="sideUtils.navigation" :group="sideUtils.teams" @right-click="rightClickStore.toggleRightClick"/>



                    
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
