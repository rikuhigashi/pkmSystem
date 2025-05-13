<template>
  <div class="w-4/5 mx-auto h-full  ">

    <!-- 侧边栏 -->
    <side-view />
    <!-- 侧边栏 -->

    <div
      :class="[
        'transition-all duration-300  overflow-auto',
        sidebarStore.sidebarOpen ? 'flex justify-center items-center' : 'lg:pl-72',
      ]"
    >
      <div class="w-4/5 h-full flex flex-col ">
        <headView />
        <!-- 内容区域           :class="[
            'h-screen flex justify-center items-center duration-300 transition-all',
            sidebarStore.sidebarOpen ? '' : '',
          ]" -->

        <main class=" duration-300 transition-all ">
          <mainView />
        </main>

        <AppAlert />

      </div>
      <!-- 内容区域 -->
    </div>
  </div>
</template>

<script lang="ts" setup>
import headView from '@/views/head/headView.vue'
import SideView from '@/views/side/sideView.vue'
import mainView from '@/views/main/mainView.vue'
import AppAlert from "@/components/appAlert.vue";


import { useSidebarStore } from '@/stores/sidebar'

const sidebarStore = useSidebarStore()

</script>
