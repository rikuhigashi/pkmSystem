<!--知识上传页面-->
<script lang="ts" setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { CheckCircleIcon } from '@heroicons/vue/24/solid'

const router = useRouter()

const form = ref({
  title: '',
  content: '',
  isEncrypted: false,
  price: 0,
  tags: []
})

const tags = ref('')
const isSubmitting = ref(false)
const submitSuccess = ref(false)

const submitForm = async () => {
  isSubmitting.value = true
  // 模拟API请求
  await new Promise(resolve => setTimeout(resolve, 1500))

  submitSuccess.value = true
  isSubmitting.value = false

  // 2秒后跳转回知识广场
  setTimeout(() => {
    router.push({ name: 'knowledgeSquare' })
  }, 2000)
}
</script>

<template>
  <div class="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <div class="mb-8">
      <h1 class="text-3xl font-bold text-gray-900">上传知识</h1>
      <p class="text-gray-500 mt-2">分享你的专业知识，帮助他人成长</p>
    </div>

    <div v-if="submitSuccess" class="alert alert-success shadow-lg mb-6">
      <div>
        <CheckCircleIcon class="h-6 w-6" />
        <span>知识上传成功！正在跳转回知识广场...</span>
      </div>
    </div>

    <div class="card bg-base-100 shadow-lg">
      <div class="card-body">
        <div class="form-control">
          <label class="label">
            <span class="label-text">知识标题</span>
          </label>
          <input
            v-model="form.title"
            type="text"
            placeholder="输入标题"
            class="input input-bordered"
          />
        </div>

        <div class="form-control mt-4">
          <label class="label">
            <span class="label-text">知识内容</span>
          </label>
          <textarea
            v-model="form.content"
            class="textarea textarea-bordered h-48"
            placeholder="详细描述你的知识..."
          ></textarea>
        </div>

        <div class="form-control mt-4">
          <label class="label">
            <span class="label-text">标签（逗号分隔）</span>
          </label>
          <input
            v-model="tags"
            type="text"
            placeholder="例如: Vue, 前端, 设计模式"
            class="input input-bordered"
          />
        </div>

        <div class="form-control mt-6">
          <label class="cursor-pointer label justify-start">
            <input
              v-model="form.isEncrypted"
              type="checkbox"
              class="checkbox checkbox-primary mr-3"
            />
            <span class="label-text">加密知识（需要购买才能查看）</span>
          </label>
        </div>

        <div v-if="form.isEncrypted" class="form-control mt-4">
          <label class="label">
            <span class="label-text">设置价格（元）</span>
          </label>
          <input
            v-model="form.price"
            type="number"
            min="0"
            step="0.1"
            class="input input-bordered w-32"
          />
        </div>

        <div class="card-actions justify-end mt-8">
          <button
            @click="router.back()"
            class="btn btn-ghost"
          >
            取消
          </button>
          <button
            @click="submitForm"
            :disabled="isSubmitting"
            class="btn btn-primary"
          >
            <span v-if="isSubmitting" class="loading loading-spinner"></span>
            {{ isSubmitting ? '上传中...' : '确认上传' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
