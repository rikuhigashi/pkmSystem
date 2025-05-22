<template>
  <div
    ref="colorPickerRef"
    class="fixed z-[999] select-none bg-base-100 shadow-lg p-2 rounded-box border border-base-300"
    v-if="props.visible"
    :style="{
      top: `${position.top}px`,
      left: `${position.left}px`,
    }"
  >
    <div class="flex justify-between items-center mb-2">
      <span class="text-sm font-medium text-base-content">颜色选择</span>
      <button class="btn btn-circle btn-ghost btn-xs" @click="handleClear">
        <IconRevoke class="w-4 h-4 text-base-content" />
      </button>
    </div>

    <Chrome
      class="!w-full"
      v-model="color"
      @update:modelValue="handleColorChange"
    />
  </div>
</template>
<!--<template>-->

<!--  <div-->
<!--    ref="colorPickerRef"-->
<!--    class="fixed z-999 select-none pointer-events-auto bg-white shadow-lg p-2 rounded"-->
<!--    v-if="props.visible"-->
<!--    :style="{-->
<!--      top: `${position.top}px`,-->
<!--      left: `${position.left}px`,-->
<!--    }"-->
<!--  >-->
<!--    <div class="flex justify-between items-center mb-2">-->
<!--      <span class="text-sm font-medium h-5">颜色选择</span>-->
<!--      <icon-revoke class="text-sm font-medium h-5 w-5" @click="handleClear"></icon-revoke>-->
<!--    </div>-->

<!--    <Chrome-->
<!--      class="pointer-events-auto"-->
<!--      v-model="color"-->
<!--      @update:modelValue="handleColorChange"-->
<!--    ></Chrome>-->
<!--  </div>-->
<!--</template>-->

<script lang="ts" setup>
import { Chrome } from '@ckpack/vue-color'
import { onBeforeUnmount, ref, watch } from 'vue'

// -------------- 图标引入 --------------
import IconRevoke from '@/assets/icons/iconRevoke.vue'
// -------------- 图标引入 --------------

const color = ref('#000000')
const colorPickerRef = ref<HTMLElement | null>(null)

// -------------- props与emit定义 --------------
const props = defineProps({
  visible: Boolean,
  position: {
    type: Object,
    default: () => ({ top: 0, left: 0 }),
  },
})

const emit = defineEmits(['color-change', 'close'])
// -------------- props与emit定义 --------------


// -------------- 清除样式 --------------
const handleClear = () => {
  emit('color-change', 'transparent')
}
// -------------- 清除样式 --------------

// -------------- 更改颜色 --------------
const handleColorChange = (value: any) => {
  emit('color-change', value.hex)
}
// -------------- 更改颜色 --------------

// -------------- 点击外部操作 --------------
const handleClickOutside = (e: MouseEvent) => {
  if (
    colorPickerRef.value &&
    !colorPickerRef.value.contains(e.target as Node) &&
    props.visible // 可见时处理
  ) {
    emit('close')
  }
}

watch(
  () => props.visible,
  (visible) => {
    if (visible) {
      setTimeout(() => {
        // 延迟绑定
        document.addEventListener('click', handleClickOutside)
      }, 150)
    } else {
      document.removeEventListener('click', handleClickOutside)
    }
  },
)

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside)
})
// -------------- 点击外部操作 --------------
</script>
