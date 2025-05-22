<!-- InputField.vue -->
<template>
  <div class="form-control w-full">
    <label :for="id" class="label">
      <span class="label-text">{{ label }}</span>
    </label>

    <input
      :type="type"
      :id="id"
      :value="modelValue"
      @input="$emit('update:modelValue', ($event.target as HTMLInputElement).value)"
      class="input input-bordered w-full"
      :class="{
        'input-error': error,
        'input-success': !error && modelValue?.length > 0 && type === 'password'
      }"
      :placeholder="placeholder"
      :autocomplete="autocomplete"
      :required="required"
    />

    <!-- 错误提示 -->
    <div v-if="error" class="mt-1 text-error text-sm flex items-center gap-1">
      <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4 shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
      </svg>
      {{ errorMessage }}
    </div>

    <!-- 密码提示 -->
    <div v-if="hints?.length && type === 'password'" class="mt-2 space-y-1">
      <div
        v-for="(hint, index) in hints"
        :key="index"
        class="flex items-center gap-1 text-sm"
        :class="hint.valid ? 'text-success' : 'text-neutral-content/70'"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          class="w-4 h-4 shrink-0"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
          stroke-width="2"
        >
          <path v-if="hint.valid" stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" />
          <circle v-else cx="12" cy="12" r="9" stroke-dasharray="24" stroke-dashoffset="0" />
        </svg>
        <span>{{ hint.text }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps({
  id: String,
  label: String,
  type: { type: String, default: 'text' },
  modelValue: String,
  placeholder: String,
  autocomplete: String,
  required: Boolean,
  error: Boolean,
  errorMessage: String,
  hints: Array as () => Array<{ text: string; valid: boolean }>,
})

defineEmits(['update:modelValue'])
</script>
