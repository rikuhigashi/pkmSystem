<!-- 登录页面公用按钮 -->
<template>
  <button
    :type="type"
    class="btn btn-primary w-full flex justify-center rounded-md px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-dui-indigo-700 focus:outline-none focus:ring-2 focus:ring-dui-indigo-600 focus:ring-offset-2 transition-colors duration-150"
  >
    <slot name="buttonName"></slot>
  </button>
</template>

<script setup lang="ts">
defineProps({
  type: {
    type: String as () => 'button' | 'submit' | 'reset',
    default: 'button',
  },
})
</script>
