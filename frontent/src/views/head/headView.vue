<script lang="ts" setup>
import { Bars3Icon } from '@heroicons/vue/24/outline'
import { MagnifyingGlassIcon } from '@heroicons/vue/20/solid'

// ----------------- store -----------------
import { useSidebarStore } from '@/stores/sidebar'
import { useEditorStore } from '@/stores/main/editorStore'

const editorStore = useEditorStore()
const sidebarStore = useSidebarStore()

import IconSaveData from '@/assets/icons/iconSaveData.vue'
import NotificationPanel from '@/components/message/notificationPanel.vue'
import {ref} from "vue";

const isSaving = ref(false)

const handleSaveMainData = async () => {
  if (isSaving.value) return

  try {
    isSaving.value = true
    if (editorStore.saveDocument) {
      await editorStore.saveDocument()
    }
  } catch (error) {
    console.error('保存失败:', error)
  } finally {
    isSaving.value = false
  }
}
</script>
<template>
  <div class="sticky top-0 z-40 flex h-16 shrink-0 items-center gap-x-4 border-b border-gray-200 bg-white px-4 shadow-sm sm:gap-x-6 sm:px-6 lg:px-8">
    <icon-save-data class="h-6 w-6 cursor-pointer text-gray-600 hover:text-primary" @click="handleSaveMainData" />

    <button
      type="button"
      :class="['btn btn-ghost p-2', sidebarStore.sidebarOpen ? '' : 'lg:hidden']"
      @click="sidebarStore.toggleSidebar"
    >
      <Bars3Icon class="size-6 text-gray-600" />
      <span class="sr-only">切换侧边栏</span>
    </button>

    <div class="flex flex-1 items-center gap-x-4">
      <div class="relative flex-1 max-w-xl">
        <MagnifyingGlassIcon class="pointer-events-none absolute left-3 top-1/2 size-5 -translate-y-1/2 text-gray-400" />
        <input
          id="search-field"
          class="input input-bordered w-full pl-10"
          placeholder="搜索..."
          type="search"
        />
      </div>

      <div class="flex items-center gap-x-2">
        <NotificationPanel />
      </div>
    </div>
  </div>
</template>
