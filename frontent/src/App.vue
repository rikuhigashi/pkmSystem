<script setup lang="ts">
import { RouterView } from 'vue-router'
import { useRightClickStore } from './stores/rightClick';

const rightClickStore = useRightClickStore()

const close = () =>{
  rightClickStore.closeRightClickMenu()  // 无论点击哪个地方都能(全局)关闭右键菜单

}
</script>

<template>
  <div class="w-full h-screen" @contextmenu.prevent @click="close">
    <RouterView />
  </div>
</template>

<style scoped></style>
