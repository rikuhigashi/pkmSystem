<script lang="ts" setup>
import { ref, watch, onUnmounted, defineProps, defineEmits } from 'vue'
import { CheckCircleIcon } from '@heroicons/vue/24/solid'
import type { ShareFormData } from '@/types/knowledgeTypes'

const props = defineProps<{
  show: boolean;
  form: ShareFormData;
}>()

const emit = defineEmits<{
  (e: 'close'): void;
  (e: 'submit', formData: ShareFormData): void;
}>()

// 创建本地表单副本
const localForm = ref<ShareFormData>({
  title: '',
  isEncrypted: false,
  price: 0,
  tags: []
})

// 标签输入
const tagsInput = ref('')

// 监听父组件传入的 form 变化，更新本地副本
watch(() => props.form, (newForm) => {
  if (newForm) {
    localForm.value = { ...newForm }
    tagsInput.value = newForm.tags.join(', ') // 将标签数组转换为逗号分隔字符串
  }
}, { immediate: true })

const isSubmitting = ref(false)
const submitSuccess = ref(false)
let resetTimeout: ReturnType<typeof setTimeout> | null = null

// 重置状态
const resetState = () => {
  isSubmitting.value = false
  submitSuccess.value = false
  tagsInput.value = props.form.tags.join(', ') // 使用当前表单值初始化

  if (resetTimeout) {
    clearTimeout(resetTimeout)
    resetTimeout = null
  }
}

// 监听显示状态变化
watch(() => props.show, (newVal) => {
  if (newVal) {
    resetState()
  }
})

const submitForm = async () => {
  isSubmitting.value = true
  // 模拟API请求
  await new Promise(resolve => setTimeout(resolve, 1500))

  submitSuccess.value = true
  isSubmitting.value = false

  // 2秒后关闭模态框
  resetTimeout = setTimeout(() => {
    // 处理标签为数组
    const tagsArray = tagsInput.value
      .split(',')
      .map(tag => tag.trim())
      .filter(tag => tag.length > 0)

    // 提交数据
    emit('submit', {
      ...localForm.value,
      tags: tagsArray
    })
    emit('close')
  }, 2000)
}

// 组件卸载时清理
onUnmounted(() => {
  if (resetTimeout) {
    clearTimeout(resetTimeout)
  }
})
</script>

<template>
  <div v-if="show" class="modal modal-open">
    <div class="modal-box max-w-2xl">
      <div class="mb-6">
        <h1 class="text-2xl font-bold text-gray-900">分享到知识广场</h1>
        <p class="text-gray-500 mt-1">将你的知识分享给社区</p>
      </div>

      <!-- 修复成功提示样式：添加 flex 布局和间距 -->
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
          </label>
          <input
            v-model="localForm.title"
            type="text"
            placeholder="输入标题"
            class="input input-bordered"
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
              v-model="localForm.isEncrypted"
              type="checkbox"
              class="checkbox checkbox-primary mr-3"
            />
            <span class="label-text">加密知识（需要购买才能查看）</span>
          </label>
        </div>

        <div v-if="localForm.isEncrypted" class="form-control">
          <label class="label">
            <span class="label-text">设置价格（元）</span>
          </label>
          <input
            v-model.number="localForm.price"
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
