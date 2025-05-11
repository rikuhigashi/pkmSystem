<template>

<!-- 这已经不需要了，留存 -->
  <div
    v-if="visible"
    ref="toolbarRef"
    :style="{ top: `${adjustedPosition.top}px`, left: `${adjustedPosition.left}px` }"
    :class="[
      'fixed  z-[99] flex flex-wrap gap-1 p-2 bg-gray-50 rounded border border-gray-200',
      visible ? 'opacity-100' : 'opacity-0 pointer-events-none',
    ]"
  >

    <template v-for="(item, index) in toolbarButtons" :key="index">

      <DropdownMenu
        v-if="item.type === 'dropdown'"
        :config="item"
        :is-active="item.isActive?.() ?? false"
      />

    <button
      v-else-if="item.type === 'button'"
      :ref="item.meta?.type === 'link' ? 'linkBtnRef' : undefined"
      :key="item.title"
      @mousedown.prevent="item.action($event)"
      :data-toolbar-button="item.meta?.type"
      :disabled="item.disabled?.() ?? false"
      :class="[
        'p-2 rounded hover:bg-gray-200 transition-colors',
        item.isActive?.() ? 'bg-gray-200 text-gray-900' : 'text-gray-600',
      ]"
      :title="item.title"
    >
      <component :is="item.icon" class="h-4 w-4"></component>
    </button>

      <div
        v-else-if="item.type === 'divider'"
        class="h-6 w-px mx-1.5 bg-gray-300 m-auto"
      />

    </template>

  </div>
</template>

<script setup lang="ts">
import type { toolbarItem, mainPosition } from '@/views/main/types/mainTypes'
import { nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'

import { debounce } from 'lodash-es'
import DropdownMenu from "@/components/mainComponent/DropdownMenu.vue";

const props = defineProps<{
  toolbarButtons: toolbarItem[]
  visible: boolean
  toolbarPosition: mainPosition
}>()

const toolbarRef = ref<HTMLElement | null>(null)
const adjustedPosition = ref({ top: 0, left: 0 })

const positionStyle = () => {
  if (!toolbarRef.value) return
  // 获取元素位置和大小
  const toolbarRect = toolbarRef.value.getBoundingClientRect()

  //   // 获取设备高与宽
  const viewportWidth = window.innerWidth

  let { top, left } = props.toolbarPosition

  // 优先上方，空间不足则显示下方
  if (top - toolbarRect.height < 10) {
    top = props.toolbarPosition.top + 20
  } else {
    top = props.toolbarPosition.top - toolbarRect.height - 5
  }

  // 确保不超出视口
  left = Math.max(
    10,
    Math.min(left - toolbarRect.width / 2, viewportWidth - toolbarRect.width - 10),
  )

  adjustedPosition.value = { top, left }
}

const debouncedPositionStyle = debounce(positionStyle, 100)

onMounted(() => {
  window.addEventListener('resize', debouncedPositionStyle)
  nextTick(positionStyle) // 初始定位仍需要nextTick
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', debouncedPositionStyle)
})

watch(
  () => props.toolbarPosition,
  () => {
    nextTick(() => {
      positionStyle()
    })
  },
)
</script>
