<script lang="ts" setup>
import { MenuItem, MenuItems } from '@headlessui/vue'

interface AccountItem {
  name: string
  href?: string
  isNew: boolean
  action?: string
}

interface Props {
  accountSelect: AccountItem[]
}

const props = defineProps<Props>()
const emit = defineEmits(['action'])

const handleActionClick = (item: AccountItem, event: MouseEvent) => {
  if (item.action) {
    event.preventDefault()
    emit('action', item.action)
  }
}
</script>

<template>
  <transition
    enter-active-class="transition ease-out duration-100"
    enter-from-class="transform opacity-0 scale-95"
    enter-to-class="transform opacity-100 scale-100"
    leave-active-class="transition ease-in duration-75"
    leave-from-class="transform opacity-100 scale-100"
    leave-to-class="transform opacity-0 scale-95"
  >
    <MenuItems
      class="absolute right-0 z-10 mt-2.5 w-44 origin-top-right rounded-md bg-white py-2 shadow-lg ring-1 ring-gray-900/5 focus:outline-none"
    >
      <div v-for="(item, index) in props.accountSelect" :key="item.name">
        <!-- 关键修改：使用 as="div" 并确保单一子元素 -->
        <MenuItem v-slot="{ active }" as="div">
          <!-- 路由项使用单独的 div 容器 -->
          <div
            v-if="!item.action"
            :class="[
              active ? 'bg-gray-50' : '',
              'px-3 py-1 text-sm/6 text-gray-900 cursor-pointer'
            ]"
          >
            <router-link
              :to="item.href || '#'"
              class="block w-full"
            >
              {{ item.name }}
              <span v-if="item.isNew" class="badge badge-primary ml-2">new</span>
            </router-link>
          </div>

          <!-- 动作项使用 button 元素 -->
          <button
            v-else
            @click="handleActionClick(item, $event)"
            :class="[
              active ? 'bg-gray-50' : '',
              'w-full text-left px-3 py-1 text-sm/6 text-gray-900 cursor-pointer'
            ]"
          >
            {{ item.name }}
          </button>
        </MenuItem>
        <div v-if="index === 0 || index === 3" class="divider m-auto"></div>
      </div>
    </MenuItems>
  </transition>
</template>



<!--<template>-->
<!--  <transition-->
<!--    enter-active-class="transition ease-out duration-100"-->
<!--    enter-from-class="transform opacity-0 scale-95"-->
<!--    enter-to-class="transform opacity-100 scale-100"-->
<!--    leave-active-class="transition ease-in duration-75"-->
<!--    leave-from-class="transform opacity-100 scale-100"-->
<!--    leave-to-class="transform opacity-0 scale-95"-->
<!--  >-->
<!--    <MenuItems-->
<!--      class="absolute right-0 z-10 mt-2.5 w-44 origin-top-right rounded-md bg-white py-2 shadow-lg ring-1 ring-gray-900/5 focus:outline-none"-->
<!--    >-->
<!--      <div v-for="(item, index) in props.accountSelect" :key="item.name">-->
<!--        <MenuItem v-slot="{ active }">-->
<!--          <router-link-->
<!--            :to="item.href"-->
<!--            @click.prevent="item.action ? emit('action', item.action) : null"-->
<!--            :class="[-->
<!--              active ? 'bg-gray-50 outline-none' : '',-->
<!--              'block px-3 py-1 text-sm/6 text-gray-900 cursor-pointer',-->
<!--            ]"-->
<!--          >-->
<!--            {{ item.name }}-->
<!--            <span v-if="item.isNew" class="indicator-item badge badge-primary ml-8">new</span>-->
<!--          </router-link>-->
<!--        </MenuItem>-->
<!--        <div v-if="index === 0 || index === 3" class="divider m-auto"></div>-->
<!--      </div>-->
<!--    </MenuItems>-->
<!--  </transition>-->
<!--</template>-->
