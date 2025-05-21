<script setup lang="ts">
import { ref } from 'vue'

const show = ref(false)
const reason = ref('')
const currentId = ref<number>()

const open = (id: number) => {
  currentId.value = id
  show.value = true
}

const emit = defineEmits<{
  (e: 'confirmed', id: number, reason: string): void
}>()

const submit = () => {
  if (reason.value.trim()) {
    emit('confirmed', currentId.value!, reason.value.trim())
    show.value = false
    reason.value = ''
  }
}

defineExpose({ open })
</script>

<template>
  <dialog class="modal" :class="{ 'modal-open': show }">
    <div class="modal-box">
      <h3 class="font-bold text-lg mb-4">请输入拒绝原因</h3>
      <textarea
        v-model="reason"
        class="textarea textarea-bordered w-full h-32"
        placeholder="请详细说明拒绝原因..."
        required
      ></textarea>
      <div class="modal-action">
        <button class="btn btn-ghost" @click="show = false">取消</button>
        <button
          class="btn btn-error"
          :disabled="!reason.trim()"
          @click="submit"
        >
          确认拒绝
        </button>
      </div>
    </div>
  </dialog>
</template>
