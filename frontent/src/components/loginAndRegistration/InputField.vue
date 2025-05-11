<!-- 登录页面输入框 -->

<template>
  <div >
    <label :for="id" class="block text-sm/6 font-medium text-gray-900">{{ label }}</label>
    <div class="mt-2 ">
      <input
        :type="type"
        :id="id"
        :autocomplete="autocomplete"
        :required="required"
        :placeholder="placeholder"
        :value="modelValue"
        @input="$emit('update:modelValue', ($event.target as HTMLInputElement).value)"
        class="input input-bordered w-full rounded-md bg-white px-3 py-1.5 text-base text-gray-900 border border-gray-300 placeholder:text-gray-400 focus:outline-2 focus:-outline-offset-2 focus:outline-indigo-600 sm:text-sm/6"
      />
    </div>
  </div>


</template>

<script setup lang="ts">
defineProps({
  id: String,
  label: String,
  type: {type: String, default: 'text'},
  autocomplete: String,
  required: Boolean,
  placeholder: { type: String, required: false },
  modelValue: String
})

defineEmits(['update:modelValue']);

</script>
