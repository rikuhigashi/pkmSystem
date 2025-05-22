<template>
  <div class="flex min-h-full flex-col justify-center py-8 sm:px-6 lg:px-8">
    <div class="sm:mx-auto sm:w-full sm:max-w-md">
      <slot name="header"></slot>
    </div>

    <div class="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
      <div class="bg-white py-8 px-4 shadow sm:rounded-lg sm:px-10">
        <slot></slot>
      </div>
      <div class="mt-6 text-center">
        <slot name="footer"></slot>
      </div>
    </div>
  </div>
</template>
