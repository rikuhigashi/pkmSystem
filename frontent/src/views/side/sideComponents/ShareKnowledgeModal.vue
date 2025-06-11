<!--知识分享弹窗-->
<script setup lang="ts">
import { ref } from 'vue'
import { createKnowledge } from '@/API/knowledge/knowledgeAPI'
import { useEditorStore } from '@/stores/main/editorStore'
import { CheckCircleIcon } from '@heroicons/vue/24/solid'
import { useAlertStore } from '@/stores/alert'

const editorStore = useEditorStore()
const alertStore = useAlertStore()

defineProps<{
  show: boolean
}>()

const emit = defineEmits<{
  (e: 'close'): void
}>()

// 使用强类型定义表单
const form = ref({
  title: '',
  encrypted: false,
  price: 0,
  tags: [] as string[],
})

const tagsInput = ref('')
const isSubmitting = ref(false)
const submitSuccess = ref(false)

const submitForm = async () => {
  if (!form.value.title.trim()) {
    alertStore.showAlert('请填写知识标题', 'error')
    return
  }

  try {
    isSubmitting.value = true

    // 处理标签
    const tagsArray = tagsInput.value
      .split(',')
      .map(tag => tag.trim())
      .filter(tag => tag.length > 0)

    // 确保编辑器内容存在
    if (!editorStore.editorContent) {
      throw new Error('知识内容为空')
    }

    // 创建知识 - 传递JSON内容
    await createKnowledge({
      title: form.value.title,
      content: JSON.stringify(editorStore.editorContent),
      encrypted: form.value.encrypted,
      price: form.value.price,
      tags: tagsArray
    })

    submitSuccess.value = true
    alertStore.showAlert('知识分享成功！', 'success')

    // 2秒后关闭模态框
    setTimeout(() => {
      emit('close')
      form.value = {
        title: '',
        encrypted: false,
        price: 0,
        tags: [],
      }
      tagsInput.value = ''
      submitSuccess.value = false
    }, 2000)
  } catch (error: unknown) {
    let errorMessage = '知识分享失败'
    if (error instanceof Error) {
      errorMessage = error.message
    }

    console.error('知识分享失败:', errorMessage)
    alertStore.showAlert(`知识分享失败: ${errorMessage}`, 'error', 5000)
  } finally {
    isSubmitting.value = false
  }
}
</script>

<template>
  <div v-if="show" class="modal modal-open">
    <div class="modal-box max-w-2xl">
      <div class="mb-6">
        <h1 class="text-2xl font-bold text-gray-900">分享到知识广场</h1>
        <p class="text-gray-500 mt-1">将你的知识分享给社区</p>
      </div>

      <div
        v-if="submitSuccess"
        class="alert alert-success shadow-lg mb-6 flex items-center bg-green-100 text-green-800 p-4 rounded-lg"
      >
        <CheckCircleIcon class="h-5 w-5 text-green-500 flex-shrink-0" />
        <span class="ml-2">知识分享成功！</span>
      </div>

      <div class="space-y-6">
        <div class="form-control">
          <label class="label">
            <span class="label-text">知识标题</span>
            <span class="label-text-alt text-error">* 必填</span>
          </label>
          <input
            v-model="form.title"
            type="text"
            placeholder="输入标题"
            class="input input-bordered"
            required
          />
        </div>

        <div class="form-control">
          <label class="label">
            <span class="label-text">标签（逗号分隔）</span>
          </label>
          <input
            v-model="tagsInput"
            type="text"
            placeholder="例如: Vue, 前端, 设计模式"
            class="input input-bordered"
          />
        </div>

        <div class="form-control">
          <label class="cursor-pointer label justify-start">
            <input
              v-model="form.encrypted"
              type="checkbox"
              class="checkbox checkbox-primary mr-3"
            />
            <span class="label-text">加密知识（需要购买才能查看）</span>
          </label>
        </div>

        <div v-if="form.encrypted" class="form-control">
          <label class="label">
            <span class="label-text">设置价格（元）</span>
          </label>
          <input
            v-model.number="form.price"
            type="number"
            min="0"
            step="0.1"
            class="input input-bordered w-32"
          />
        </div>
      </div>

      <div class="modal-action">
        <button @click="$emit('close')" class="btn btn-ghost">取消</button>
        <button
          @click="submitForm"
          :disabled="isSubmitting"
          class="btn btn-primary"
        >
          <span v-if="isSubmitting" class="loading loading-spinner"></span>
          {{ isSubmitting ? '分享中...' : '确认分享' }}
        </button>
      </div>
    </div>
  </div>
</template>
