<script lang="ts" setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { CheckCircleIcon } from '@heroicons/vue/24/solid'
import { createKnowledge } from '@/API/knowledge/knowledgeAPI'
import { useAlertStore } from '@/stores/alert'
import {EditorContent, useEditor} from "@tiptap/vue-3"
import StarterKit from "@tiptap/starter-kit"
import type { JSONContent } from '@tiptap/core'

const router = useRouter()
const alertStore = useAlertStore()

// 表单数据结构
const form = ref({
  title: '',
  content: null as JSONContent | null, // 使用Tiptap的JSONContent类型
  encrypted: false,
  price: 0,
  tags: [] as string[]
})

const tagsInput = ref('')
const isSubmitting = ref(false)
const submitSuccess = ref(false)

// Tiptap编辑器初始化
const editor = useEditor({
  content: {
    type: 'doc',
    content: [{
      type: 'paragraph',
      content: [{ type: 'text', text: '' }]
    }]
  },
  extensions: [StarterKit],
  onUpdate: () => {
    // 更新表单中的内容
    form.value.content = editor.value?.getJSON() || null
  },
})

const submitForm = async () => {
  if (!form.value.title.trim()) {
    alertStore.showAlert('请填写知识标题', 'error')
    return
  }

  if (!form.value.content) {
    alertStore.showAlert('请填写知识内容', 'error')
    return
  }

  try {
    isSubmitting.value = true

    // 处理标签
    const tagsArray = tagsInput.value
      .split(',')
      .map(tag => tag.trim())
      .filter(tag => tag.length > 0)

    // 创建知识 - 传递JSON内容
    await createKnowledge({
      title: form.value.title,
      content: JSON.stringify(form.value.content),
      encrypted: form.value.encrypted,
      price: form.value.price,
      tags: tagsArray
    })

    submitSuccess.value = true
    alertStore.showAlert('知识上传成功！', 'success')

    setTimeout(() => {
      router.push({ name: 'knowledgeSquare' })
    }, 2000)
  } catch (error) {
    console.error('上传知识失败:', error)
    alertStore.showAlert('上传知识失败，请稍后再试', 'error', 5000)
  } finally {
    isSubmitting.value = false
  }
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

        <div class="form-control mt-4">
          <label class="label">
            <span class="label-text">知识内容</span>
            <span class="label-text-alt text-error">* 必填</span>
          </label>
          <div class="border rounded-md p-2 min-h-[200px] bg-white">
            <editor-content :editor="editor" />
          </div>
        </div>

        <div class="form-control mt-4">
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

        <div class="form-control mt-6">
          <label class="cursor-pointer label justify-start">
            <input
              v-model="form.encrypted"
              type="checkbox"
              class="checkbox checkbox-primary mr-3"
            />
            <span class="label-text">加密知识（需要购买才能查看）</span>
          </label>
        </div>

        <div v-if="form.encrypted" class="form-control mt-4">
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
