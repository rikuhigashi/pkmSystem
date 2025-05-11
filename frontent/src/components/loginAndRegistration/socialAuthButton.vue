<!-- 第三方登录显示组件 -->
<template>
  <a
    href="#"
    class="flex w-full items-center justify-center gap-3 rounded-md bg-white px-3 py-2 text-sm font-semibold text-gray-900 shadow-xs ring-1 ring-gray-300 ring-inset hover:bg-gray-50 focus-visible:ring-transparent"
    @click.prevent="$emit('click')"
  >
    <slot name="icon"></slot>
    <span class="text-sm/6 font-semibold">{{ text }}</span>
  </a>
</template>

<script setup lang="ts">
defineProps({
  text: {
    type: String,
    required: true
  }
})

defineEmits(['click'])
</script>
