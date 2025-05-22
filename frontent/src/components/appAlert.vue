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
        <svg
          xmlns="http://www.w3.org/2000/svg"
          class="stroke-current shrink-0 w-6 h-6"
          fill="none"
          viewBox="0 0 24 24"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            :d="
              {
                info: 'M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z',
                success: 'M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z',
                warning:
                  'M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z',
                error: 'M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z',
              }[alertStore.type]
            "
          />
        </svg>
        <span>{{ alertStore.message }}</span>
      </div>
    </div>
  </transition>
</template>
