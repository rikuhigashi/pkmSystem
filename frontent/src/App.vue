<script setup lang="ts">
import { RouterView } from 'vue-router'
import { useRightClickStore } from './stores/rightClick';

const rightClickStore = useRightClickStore()
</script>

<template>
  <div class="w-full h-screen" @contextmenu.prevent @click="rightClickStore.closeRightClickMenu">
    <RouterView />
  </div>
</template>

<style scoped></style>
