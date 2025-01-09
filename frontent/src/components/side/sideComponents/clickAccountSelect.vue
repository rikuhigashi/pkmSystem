<script lang="ts" setup>

import {
    MenuItem,
    MenuItems,
} from '@headlessui/vue'


interface accountItem {
    name: string,
    href: string,
    isNew: boolean
}

interface Props {
    accountSelect: accountItem[];
}

const props = defineProps<Props>()

</script>


<template>
    <transition enter-active-class="transition ease-out duration-100" enter-from-class="transform opacity-0 scale-95"
        enter-to-class="transform opacity-100 scale-100" leave-active-class="transition ease-in duration-75"
        leave-from-class="transform opacity-100 scale-100" leave-to-class="transform opacity-0 scale-95">
        <MenuItems
            class="absolute right-0 z-10 mt-2.5 w-44 origin-top-right rounded-md bg-white py-2 shadow-lg ring-1 ring-gray-900/5 focus:outline-none">
            <div v-for="(item, index) in props.accountSelect" :key="item.name">
                <MenuItem v-slot="{ active }">
                <a :href="item.href"
                    :class="[active ? 'bg-gray-50 outline-none' : '', 'block px-3 py-1 text-sm/6 text-gray-900 ']">{{
                        item.name }}
                    <span v-if="item.isNew" class="indicator-item badge badge-primary ml-8">new</span>
                </a>
                </MenuItem>
                <div v-if="index === 0 || index === 3" class="divider m-auto"></div>
            </div>
        </MenuItems>
    </transition>
</template>