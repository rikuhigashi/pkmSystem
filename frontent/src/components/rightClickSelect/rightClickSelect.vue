<script lang="ts" setup>
import { nextTick, onMounted, ref, watch } from 'vue'

interface MenuItem {
  icon: unknown
  label: string
  onClick: (event: MouseEvent) => void
  show: () => boolean
}

interface Props {
  menuItems: MenuItem[][] //数据
  menuPosition: { x: number; y: number } //位置
}

const props = defineProps<Props>()

const menuRef = ref<HTMLElement | null>(null)

const adjustedPosition = ref({ top: 0, left: 0 })

const adjustedMenuPosition = () => {
  if (menuRef.value) {
    // 获取元素位置和大小
    const memuRect = menuRef.value.getBoundingClientRect()

    // 获取设备高与宽
    const viewPortHeight = window.innerHeight
    const viewPortWidth = window.innerWidth

    // 默认位置
    let top = props.menuPosition.y
    let left = props.menuPosition.x

    // 检查是否超出底部
    if (memuRect.height + props.menuPosition.y > viewPortHeight) {
      // 超出时向上调整,同时确保不会超出顶部
      top = Math.max(0, props.menuPosition.y - memuRect.height)
    }

    // 检查是否超出右侧
    if (memuRect.width + props.menuPosition.x > viewPortWidth) {
      // 超出像左调整
      left = Math.max(0, viewPortWidth - memuRect.width)
    }

    // 更新位置
    adjustedPosition.value = { top, left }
  }
}

onMounted(() => {
  nextTick(() => {
    adjustedMenuPosition()
  })
})

watch(
  () => props.menuItems,
  () => {
    nextTick(() => {
      adjustedMenuPosition()
    })
  },
)
</script>

<template>
  <teleport to="body">
    <div
      ref="menuRef"
      v-bind="$attrs"
      class="absolute z-50"
      v-if="props.menuItems.length"
      :style="{ top: `${adjustedPosition.top}px`, left: `${adjustedPosition.left}px` }"
      @contextmenu.prevent
    >
      <transition
        enter-active-class="duration-1000 ease-in-out transform transition-all"
        enter-from-class="opacity-0 translate-y-4 scale-95"
        enter-to-class="opacity-100 translate-y-0 scale-100"
        leave-active-class="duration-1000 ease-in-out transform transition-all"
        leave-from-class="opacity-100 translate-y-0 scale-100"
        leave-to-class="opacity-0 translate-y-4 scale-95"
      >
        <div
          class="mt-2 w-40 origin-top-right divide-y divide-gray-300 rounded-md bg-gradient-to-br from-gray-50 to-gray-200 shadow-lg ring-1 ring-black/5 focus:outline-none"
        >
          <div class="py-1" v-for="(group, index) in props.menuItems" :key="index">
            <span
              draggable="false"
              :class="[
                item.show() ? 'text-gray-700 hover:text-blue-500' : 'hidden',
                'group flex items-center px-4 py-2 text-sm cursor-pointer',
              ]"
              v-for="(item, index) in group"
              :key="index"
              @click="item.onClick"
            >
              <component
                :is="item.icon"
                :class="['mr-3 size-5 text-gray-400 group-hover:text-blue-400']"
                aria-hidden="true"
              />
              {{ item.label }}
            </span>
          </div>
        </div>
      </transition>
    </div>
  </teleport>
</template>
