<!-- 第三方登录显示组件 -->
<template>
  <button
    class="w-full inline-flex items-center justify-center rounded-md border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 shadow-sm hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2"
    @click="$emit('click')"
  >
    <slot name="icon"></slot>
    <span class="ml-2">{{ text }}</span>
  </button>
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
