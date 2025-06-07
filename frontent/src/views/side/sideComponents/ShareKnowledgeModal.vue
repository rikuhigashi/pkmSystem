<script lang="ts" setup>
import { ref } from 'vue'
import { CheckCircleIcon } from '@heroicons/vue/24/solid'
import { createKnowledge } from '@/API/knowledge/knowledgeAPI'
import { useEditorStore } from '@/stores/main/editorStore'
import { userAuthStore } from '@/stores/auth'
import { generateHTML } from '@tiptap/html'
import StarterKit from '@tiptap/starter-kit'
import Heading from '@tiptap/extension-heading'
import Blockquote from '@tiptap/extension-blockquote'
import CodeBlock from '@tiptap/extension-code-block'
import HorizontalRule from '@tiptap/extension-horizontal-rule'
import Underline from '@tiptap/extension-underline'
import Link from '@tiptap/extension-link'

const editorStore = useEditorStore()
const authStore = userAuthStore()

defineProps<{
  show: boolean
}>()

const emit = defineEmits<{
  (e: 'close'): void
}>()

const form = ref({
  title: '',
  isEncrypted: false,
  price: 0,
  tags: [] as string[],
})

const tagsInput = ref('')
const isSubmitting = ref(false)
const submitSuccess = ref(false)

// Tiptap 扩展配置
const extensions = [
  StarterKit,
  Heading.configure({ levels: [1, 2, 3] }),
  Blockquote,
  CodeBlock,
  HorizontalRule,
  Underline,
  Link.configure({
    openOnClick: true,
    HTMLAttributes: { class: 'text-blue-600 hover:underline' },
  }),
]

// 提交分享表单
const submitForm = async () => {
  if (!form.value.title.trim()) {
    alert('请填写知识标题')
    return
  }

  try {
    isSubmitting.value = true

    // 处理标签为数组
    const tagsArray = tagsInput.value
      .split(',')
      .map(tag => tag.trim())
      .filter(tag => tag.length > 0)

    // 将 Tiptap JSON 内容转换为 HTML
    const contentHtml = generateHTML(editorStore.editorContent, extensions)

    // 准备请求数据
    const requestData = {
      title: form.value.title,
      content: contentHtml,
      isEncrypted: form.value.isEncrypted,
      price: form.value.price,
      tags: tagsArray,
      authorId: authStore.userInfo?.id
    }

    // 调用创建知识API
    await createKnowledge(requestData)

    submitSuccess.value = true

    // 2秒后关闭模态框
    setTimeout(() => {
      emit('close')
      // 重置表单
      form.value = {
        title: '',
        isEncrypted: false,
        price: 0,
        tags: [],
      }
      tagsInput.value = ''
      submitSuccess.value = false
    }, 2000)
  } catch (error) {
    console.error('分享知识失败:', error)
    alert('分享知识失败，请稍后再试')
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
              v-model="form.isEncrypted"
              type="checkbox"
              class="checkbox checkbox-primary mr-3"
            />
            <span class="label-text">加密知识（需要购买才能查看）</span>
          </label>
        </div>

        <div v-if="form.isEncrypted" class="form-control">
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
