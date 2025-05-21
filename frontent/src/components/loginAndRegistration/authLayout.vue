<template>
  <div class="flex min-h-full flex-1 flex-col justify-center py-8 sm:px-6 lg:px-8">
    <!-- 头部区域 -->
    <div class="sm:mx-auto sm:w-full sm:max-w-md">
      <slot name="header"></slot>
    </div>

    <!-- 主卡片区域 -->
    <div class="mt-8 mx-auto w-full max-w-md">
      <div class="bg-white px-6 py-8 shadow-md rounded-lg sm:px-12">
        <slot></slot>
      </div>
      <slot name="footer"></slot>
    </div>
  </div>
</template>
