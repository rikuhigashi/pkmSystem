<template>
  <div>
    <!-- 加载状态 -->
    <div v-if="loading" class="text-center py-8 text-gray-500">
      正在加载内容...
    </div>

    <!-- 内容渲染 -->
    <div v-else-if="content">
      <!-- 标题 -->
      <h1 v-if="content.content?.[0]?.type === 'heading'" class="text-3xl font-bold mb-6">
        {{ getTextContent(content.content[0]) }}
      </h1>

      <!-- 循环渲染内容 -->
      <template v-for="(node, index) in content.content" :key="index">
        <!-- 段落 -->
        <p
          v-if="node.type === 'paragraph'"
          class="mb-4 text-gray-700 leading-relaxed"
        >
          <template v-for="(textNode, textIndex) in node.content" :key="textIndex">
            <span
              :class="getTextStyles(textNode.marks)"
              class="whitespace-pre-wrap"
            >
              {{ textNode.text }}
            </span>
          </template>
        </p>

        <!-- 有序列表 -->
        <ol v-if="node.type === 'orderedList'" class="list-decimal pl-6 mb-4">
          <li
            v-for="(item, itemIndex) in node.content"
            :key="itemIndex"
            class="mb-2"
          >
            {{ getTextContent(item) }}
          </li>
        </ol>

        <!-- 图片 -->
        <img
          v-if="node.type === 'image'"
          :src="node.attrs.src"
          :alt="node.attrs.alt || ''"
          class="my-4 rounded-lg shadow-md max-w-full h-auto"
        />

        <!-- 更多类型支持... -->
      </template>
    </div>

    <!-- 空状态 -->
    <div v-else class="text-gray-400 text-center py-8">
      暂无可用内容
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  content: Record<string, any> | null
  loading?: boolean
}>()

// 获取文字样式
const getTextStyles = (marks: any[]) => {
  if (!marks) return ''
  return marks.map(mark => {
    switch (mark.type) {
      case 'bold': return 'font-bold'
      case 'italic': return 'italic'
      case 'underline': return 'underline'
      default: return ''
    }
  }).join(' ')
}

// 获取节点文字内容
const getTextContent = (node: any): string => {
  if (node.content) {
    return node.content.map((textNode: any) => textNode.text).join('')
  }
  return node.text || ''
}
</script>

<style scoped>
.prose {
  max-width: none !important;
}
</style>
