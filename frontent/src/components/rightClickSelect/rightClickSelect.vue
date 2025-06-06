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

// 右键事件阻止默认行为
const handleContextMenu = (e: MouseEvent) => {
  e.preventDefault()
}

onMounted(() => {
  nextTick(() => {
    adjustedMenuPosition()
  })
  document.addEventListener('contextmenu', handleContextMenu)
  return () => document.removeEventListener('contextmenu', handleContextMenu)
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
      class="fixed z-[999] origin-top"
      :style="{ top: `${adjustedPosition.top}px`, left: `${adjustedPosition.left}px` }"
      v-bind="$attrs"
    >
      <transition
        enter-active-class="transition duration-100 ease-out"
        leave-active-class="transition duration-75 ease-in"
        enter-from-class="opacity-0 scale-95"
        leave-to-class="opacity-0 scale-95"
      >
        <div  v-if="menuItems.length" class="min-w-[160px] rounded-lg bg-white shadow-xl ring-1 ring-gray-100 py-2">
          <div
            v-for="(group, index) in props.menuItems"
            :key="index"
            class="border-t border-gray-100 first:border-0"
          >
            <button
              v-for="(item, idx) in group"
              :key="idx"
              v-show="item.show()"
              @click="item.onClick"
              class="flex w-full items-center px-4 py-2 text-sm hover:bg-gray-50 transition-colors"
            >
              <component :is="item.icon" class="size-4 text-gray-400 mr-3" />
              {{ item.label }}
            </button>
          </div>
        </div>
      </transition>
    </div>
  </teleport>
</template>


