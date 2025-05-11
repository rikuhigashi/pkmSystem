<script lang="ts" setup>
import { Bars3Icon, BellIcon } from '@heroicons/vue/24/outline'
import { MagnifyingGlassIcon } from '@heroicons/vue/20/solid'

// ----------------- store -----------------
import { useSidebarStore } from '@/stores/sidebar'

const sidebarStore = useSidebarStore()

import IconSaveData from '@/assets/icons/iconSaveData.vue'
import sideMenuMethod from '@/views/side/configs/sideMenuMethod'

const handleSaveMainData = async () => {
    await sideMenuMethod.saveMainData()
}
</script>

<template>
  <div
    class="sticky top-0 z-40 flex h-16 shrink-0 items-center gap-x-4 border-b border-gray-200 bg-white px-4 shadow-sm sm:gap-x-6 sm:px-6 lg:px-8"
  >
    <icon-save-data class="h-6 w-6 cursor-pointer" @click="handleSaveMainData"></icon-save-data>

    <button
      type="button"
      :class="['-m-2.5 p-2.5 text-gray-700', sidebarStore.sidebarOpen ? '' : 'hidden']"
      @click="sidebarStore.toggleSidebar"
    >
      <span class="sr-only">Open sidebar</span>
      <Bars3Icon class="size-6" aria-hidden="true" />
    </button>

    <div class="h-6 w-px bg-gray-900/10 lg:hidden" aria-hidden="true" />

    <div class="flex flex-1 gap-x-4 self-stretch lg:gap-x-6">
      <form class="relative flex flex-1" action="#" method="GET">
        <label for="search-field" class="sr-only">Search</label>
        <MagnifyingGlassIcon
          class="pointer-events-none absolute inset-y-0 left-0 h-full w-5 text-gray-400"
          aria-hidden="true"
        />
        <input
          id="search-field"
          class="block size-full border-0 py-0 pl-8 pr-0 text-gray-900 placeholder:text-gray-400 focus:ring-0 sm:text-sm"
          placeholder="Search..."
          type="search"
          name="search"
        />
      </form>
      <div class="flex items-center gap-x-4 lg:gap-x-6">
        <button type="button" class="-m-2.5 p-2.5 text-gray-400 hover:text-gray-500">
          <span class="sr-only">View notifications</span>
          <BellIcon class="size-6" aria-hidden="true" />
        </button>
      </div>
    </div>
  </div>
</template>
