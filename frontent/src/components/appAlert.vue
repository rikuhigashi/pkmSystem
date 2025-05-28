<script setup lang="ts">
import { useAlertStore } from '@/stores/alert'

const alertStore = useAlertStore()
</script>

<template>
  <transition
    name="slide-fade"
    enter-active-class="transition-all duration-300 ease-out"
    leave-active-class="transition-all duration-300 cubic-bezier(1, 0.5, 0.8, 1)"
    enter-from-class="translate-x-5 opacity-0"
    leave-to-class="translate-x-5 opacity-0"
  >
    <div
      v-if="alertStore.show"
      :class="[
        `alert shadow-lg fixed top-4 z-[99] max-w-md  w-1/4`,
        {
          'alert-info': alertStore.type === 'info',
          'alert-success': alertStore.type === 'success',
          'alert-warning': alertStore.type === 'warning',
          'alert-error': alertStore.type === 'error',
        },
      ]"
    >
      <div class="flex items-center gap-2">
        <span v-if="alertStore.type === 'info'" class="loading loading-spinner loading-sm"></span>
        <span>{{ alertStore.message }}</span>
      </div>
    </div>
  </transition>
</template>
