<template>

  <div class="w-4/5 m-auto">
    <!-- 侧边栏 -->
    <side />
    <!-- 侧边栏 -->

    <div :class="['transition-all duration-300',
      sidebarStore.sidebarOpen ? 'flex justify-center items-center' : 'lg:pl-72'
    ]">
      <!-- 头部 -->
      <!-- <headCom /> -->
      <!-- 头部 -->

      <!-- 内容区域 -->
      <main :class="['h-screen w-full flex justify-center items-center duration-300 transition-all  ',
        sidebarStore.sidebarOpen ? ' bg-red-500 w-2/3' : ' bg-sky-600 '
      ]">

        <div @click="sidebarStore.toggleSidebar">test</div>
      </main>
      <!-- 内容区域 -->


    </div>

  </div>
</template>

<script setup>
import side from '@/components/side/side.vue';
// import headCom from '@/components/head/head.vue';
import { useSidebarStore } from '@/stores/sidebar';
const sidebarStore = useSidebarStore()

</script>