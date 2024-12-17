<script lang="ts" setup>
import { navigation, teams, icon, userNavigation, isVipActive,iconVipActive } from './side'
import { useSidebarStore } from '@/stores/sidebar'

import {
    Menu,
    MenuButton,
    MenuItem,
    MenuItems,
} from '@headlessui/vue'

import { ChevronDownIcon } from '@heroicons/vue/20/solid'


const { XMarkIcon, Cog6ToothIcon } = icon

const sidebarStore = useSidebarStore()

</script>


<template>
    <!-- 桌面端侧边栏 -->
    <!-- 'hidden lg:fixed lg:inset-y-0 lg:z-50 lg:flex lg:w-72 lg:flex-col'  -->

    <div :class="['lg:w-72 lg:fixed lg:inset-y-0 lg:flex lg:flex-col transition-transform ease-in-out duration-500',
        sidebarStore.sidebarOpen ? '-translate-x-sidebarTranlate' : ''
    ]">


        <!-- Sidebar component, swap this element with another sidebar if you like -->
        <div class="flex grow flex-col gap-y-5 overflow-y-auto bg-gray-900 px-6 pb-4">
            <div class="flex h-16 shrink-0 items-center group">

                <Menu as="div" class="relative">
                    <MenuButton class="-m-1.5 flex items-center p-1.5">
                        <!-- 使用无障碍 -->
                        <span class="sr-only">打开菜单</span>
                        <!-- 使用无障碍 -->
                        <img class="size-8 rounded-full bg-gray-50"
                            src="https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80"
                            alt="" />
                        <span class="hidden lg:flex lg:items-center">
                            <span class="ml-4 text-sm/6 font-semibold text-gray-400" aria-hidden="true">这是用户Id</span>
                            <ChevronDownIcon class="ml-2 size-5 text-gray-400" aria-hidden="true" />
                            <!-- <iconVipAct class="h-6 w-auto ml-2" /> -->
                            <!-- <iconVip class="h-6 w-auto ml-2" @click="changeVipIcon"/> -->
                            <component :is="isVipActive ? 'iconVipAct' : 'iconVip'" class="h-6 w-auto ml-2" @click="iconVipActive"></component>
                        </span>

                    </MenuButton>
                    <transition enter-active-class="transition ease-out duration-100"
                        enter-from-class="transform opacity-0 scale-95" enter-to-class="transform opacity-100 scale-100"
                        leave-active-class="transition ease-in duration-75"
                        leave-from-class="transform opacity-100 scale-100"
                        leave-to-class="transform opacity-0 scale-95">
                        <MenuItems
                            class="absolute right-0 z-10 mt-2.5 w-32 origin-top-right rounded-md bg-white py-2 shadow-lg ring-1 ring-gray-900/5 focus:outline-none">
                            <MenuItem v-for="item in userNavigation" :key="item.name" v-slot="{ active }">
                            <a :href="item.href"
                                :class="[active ? 'bg-gray-50 outline-none' : '', 'block px-3 py-1 text-sm/6 text-gray-900']">{{
                                    item.name }}</a>
                            </MenuItem>
                        </MenuItems>
                    </transition>
                </Menu>

                <!-- 左收缩图标 -->
                <iconLeftArrow
                    class="h-4 w-auto ml-auto hidden group-hover:block transition-opacity duration-800 opacity-0 group-hover:opacity-100"
                    @click="sidebarStore.toggleSidebar" />
                <!-- 左收缩图标 -->

            </div>
            <nav class="flex flex-1 flex-col">
                <ul role="list" class="flex flex-1 flex-col gap-y-7">
                    <li>
                        <ul role="list" class="-mx-2 space-y-1">
                            <li v-for="item in navigation" :key="item.name">
                                <a :href="item.href"
                                    :class="[item.current ? 'bg-gray-800 text-white' : 'text-gray-400 hover:bg-gray-800 hover:text-white', 'group flex gap-x-3 rounded-md p-2 text-sm/6 font-semibold']">
                                    <component :is="item.icon" class="size-6 shrink-0" aria-hidden="true" />
                                    {{ item.name }}
                                </a>
                            </li>
                        </ul>
                    </li>
                    <li>
                        <div class="text-xs/6 font-semibold text-gray-400">Your teams</div>
                        <ul role="list" class="-mx-2 mt-2 space-y-1">
                            <li v-for="team in teams" :key="team.name">
                                <a :href="team.href"
                                    :class="[team.current ? 'bg-gray-800 text-white' : 'text-gray-400 hover:bg-gray-800 hover:text-white', 'group flex gap-x-3 rounded-md p-2 text-sm/6 font-semibold']">
                                    <span
                                        class="flex size-6 shrink-0 items-center justify-center rounded-lg border border-gray-700 bg-gray-800 text-[0.625rem] font-medium text-gray-400 group-hover:text-white">{{
                                            team.initial }}</span>
                                    <span class="truncate">{{ team.name }}</span>
                                </a>
                            </li>
                        </ul>
                    </li>
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
    <!-- 桌面端侧边栏 -->




</template>
