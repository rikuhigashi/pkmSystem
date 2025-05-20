<template>
  <div class="form-control">
    <label :for="id" class="label">
      <span class="label-text block text-sm/6 font-medium text-gray-900">{{ label }}</span>
    </label>
    <label class="input-group">
      <input
        :type="type"
        :id="id"
        :autocomplete="autocomplete"
        :required="required"
        :placeholder="placeholder"
        :value="modelValue"
        @input="$emit('update:modelValue', ($event.target as HTMLInputElement).value)"
        class="input input-bordered w-2/3 rounded-r-none rounded-md bg-white px-3 py-1.5 text-base text-gray-900 border border-gray-300 placeholder:text-gray-400 focus:outline-2 focus:-outline-offset-2 focus:outline-indigo-600 sm:text-sm/6"
      />
      <button  type="button"  class="btn w-1/3 btn-primary rounded-l-none" @click="$emit('send-code')">
        {{ buttonValue }}
      </button>
    </label>
  </div>
</template>

<script setup lang="ts">
defineProps({
  id: String,
  label: String,
  buttonValue: String,
  type: { type: String, default: 'text' },
  autocomplete: String,
  required: Boolean,
  placeholder: { type: String, required: false },
  modelValue: String,
})
defineEmits(['update:modelValue', 'send-code'])
</script>
