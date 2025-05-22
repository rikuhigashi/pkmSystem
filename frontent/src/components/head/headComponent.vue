<script lang="ts" setup>
import { Menu, MenuButton, MenuItem, MenuItems } from '@headlessui/vue'
import { Bars3Icon, BellIcon } from '@heroicons/vue/24/outline'
import { ChevronDownIcon, MagnifyingGlassIcon } from '@heroicons/vue/20/solid'
import { userNavigation } from './head'

import { useSidebarStore } from '@/stores/sidebar'

const sidebarStore = useSidebarStore()
</script>

<template>
  <div
    class="sticky top-0 z-40 navbar bg-base-100 border-b border-base-300 shadow-sm px-4 sm:px-6 lg:px-8"
  >
    <div class="flex-1 gap-2">
      <button class="btn btn-ghost btn-square lg:hidden" @click="sidebarStore.toggleSidebar">
        <Bars3Icon class="size-6" />
      </button>

      <div class="form-control flex-1 max-w-xl">
        <div class="input-group">
          <input
            type="text"
            placeholder="Search..."
            class="input input-bordered input-sm w-full pl-10"
          />
          <MagnifyingGlassIcon class="absolute left-3 top-3 size-4 text-gray-400" />
        </div>
      </div>

      <div class="flex items-center gap-2">
        <button class="btn btn-ghost btn-circle">
          <BellIcon class="size-6" />
        </button>

        <Menu as="div" class="relative">
          <MenuButton class="btn btn-ghost flex items-center gap-1">
            <div class="avatar">
              <div class="w-8 rounded-full">
                <img
                  src="https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80"
                  alt="noImage"
                />
              </div>
            </div>
            <ChevronDownIcon class="size-4" />
          </MenuButton>

          <transition
            enter-active-class="transition ease-out duration-100"
            enter-from-class="transform opacity-0 scale-95"
            enter-to-class="transform opacity-100 scale-100"
            leave-active-class="transition ease-in duration-75"
            leave-from-class="transform opacity-100 scale-100"
            leave-to-class="transform opacity-0 scale-95"
          >
            <MenuItems
              class="absolute right-0 z-10 mt-2.5 w-32 origin-top-right rounded-md bg-white py-2 shadow-lg ring-1 ring-gray-900/5 focus:outline-none"
            >
              <MenuItem v-for="item in userNavigation" :key="item.name" v-slot="{ active }">
                <a
                  :href="item.href"
                  :class="[
                    active ? 'bg-gray-50 outline-none' : '',
                    'block px-3 py-1 text-sm/6 text-gray-900',
                  ]"
                  >{{ item.name }}</a
                >
              </MenuItem>
            </MenuItems>
          </transition>
        </Menu>
      </div>
    </div>
  </div>
</template>
