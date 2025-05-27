<template>
  <node-view-wrapper
    as="div"
    class="relative inline-block min-w-[400px] max-w-full my-4 mx-auto p-3 bg-base-100 rounded-box border border-base-300"
    :class="{ 'ring ring-primary/50': selected }"
    :style="{ width: containerWidth }"
    style="will-change: width; backface-visibility: hidden;"
  >
    <iframe
      :src="src"
      :width="currentWidth"
      :height="parsedHeight"
      allowfullscreen
      class="w-[calc(100%-24px)] h-[calc(400px-24px)] mx-3 my-3 rounded-box bg-base-content aspect-video"
    />
    <div
      v-if="selected"
      class="absolute bottom-3 right-3 w-4 h-4 bg-primary border-2 border-base-100 rounded-full cursor-nwse-resize translate-x-1/2 translate-y-1/2 z-10"
      @mousedown="startResize"
    />
  </node-view-wrapper>
</template>

<!--<template>-->
<!--  <node-view-wrapper-->
<!--    as="div"-->
<!--    class="video-container"-->
<!--    :class="{ selected }"-->
<!--    :style="{ width: containerWidth }"-->
<!--  >-->
<!--    <iframe-->
<!--      :src="src"-->
<!--      :width="currentWidth"-->
<!--      :height="parsedHeight"-->
<!--      allowfullscreen-->
<!--      class="video-iframe"-->
<!--    />-->
<!--    <div v-if="selected" class="resize-handle" @mousedown="startResize" />-->
<!--  </node-view-wrapper>-->
<!--</template>-->


<script lang="ts" setup>
import {ref, computed, watch, onMounted, type PropType} from 'vue'
import {NodeViewWrapper, nodeViewProps} from '@tiptap/vue-3'

// 接收传入的 props
const props = defineProps({
  ...nodeViewProps,
  selected: {
    type: Boolean,
    required: true,
  },
  updateAttributes: {
    type:  Function as PropType<(attributes: Record<string, unknown>) => void>,
    required: true,
  },
})

// 响应式数据
const isResizing = ref(false)
const startX = ref(0)
const startWidth = ref(0)
const currentWidth = ref(0)
const tempWidth = ref(0)


onMounted(() => {
  const editorWidth = document.querySelector('.editor-container')?.clientWidth || 800
  const initialWidth = Math.min(800, editorWidth * 0.8)
  startWidth.value = initialWidth
  currentWidth.value = initialWidth
  tempWidth.value = initialWidth
})

// 计算属性
const src = computed(() => props.node.attrs.src as string)

const containerWidth = computed(() => `${tempWidth.value}px`)

const parsedHeight = computed(() => {
  const height = parseInt((props.node.attrs.height as string)?.replace('px', '') || '400')
  return `${height}px`
})


const startResize = (event: MouseEvent) => {
  if (isResizing.value) return
  isResizing.value = true
  startX.value = event.clientX
  startWidth.value = tempWidth.value
  document.addEventListener('mousemove', handleResize)
  document.addEventListener('mouseup', stopResize)
}

const handleResize = (event: MouseEvent) => {
  if (!isResizing.value) return

  const editorContainer = document.querySelector('.editor-container') || document.body
  const editorWidth = editorContainer.clientWidth

  const dynamicMaxWidth = Math.min(1200, editorWidth * 0.9)
  const delta = event.clientX - startX.value
  let newWidth = startWidth.value + delta

  newWidth = Math.min(Math.max(300, newWidth), dynamicMaxWidth)

  if (Math.abs(newWidth - currentWidth.value) > 0) {
    tempWidth.value = newWidth
  }
}

const stopResize = () => {
  if (isResizing.value) {
    isResizing.value = false
    props.updateAttributes({width: `${tempWidth.value}px`})
    currentWidth.value = tempWidth.value
    document.removeEventListener('mousemove', handleResize)
    document.removeEventListener('mouseup', stopResize)
  }
}


watch(
  () => props.node.attrs.width,
  (newWidth: string) => {
    const parsedWidth = parseInt(newWidth.replace('px', '')) || 600
    if (parsedWidth !== currentWidth.value) {
      currentWidth.value = parsedWidth
      tempWidth.value = parsedWidth
      startWidth.value = parsedWidth
    }
  },
  {immediate: true}
)

watch(
  () => props.selected,
  (newVal) => {
    if (!newVal) {
      stopResize()
    }
  }
)
</script>


<!--<script lang="ts">-->
<!--import { NodeViewWrapper, nodeViewProps } from '@tiptap/vue-3'-->
<!--import { defineComponent, type PropType } from 'vue'-->

<!--export default defineComponent({-->
<!--  name: 'VideoComponent',-->
<!--  components: { NodeViewWrapper },-->

<!--  props: {-->
<!--    ...nodeViewProps,-->
<!--    selected: {-->
<!--      type: Boolean as PropType<boolean>,-->
<!--      required: true,-->
<!--    },-->
<!--    updateAttributes: {-->
<!--      type: Function as PropType<(attrs: Record<string, unknown>) => void>,-->
<!--      required: true,-->
<!--    },-->
<!--  },-->

<!--  data() {-->
<!--    const editorWidth = this.$el?.closest('.editor-container')?.clientWidth || 800-->
<!--    const initialWidth = Math.min(800, editorWidth * 0.8)-->
<!--    return {-->
<!--      isResizing: false,-->
<!--      startX: 0,-->
<!--      startWidth: initialWidth,-->
<!--      currentWidth: initialWidth,-->
<!--      tempWidth: initialWidth,-->
<!--    }-->
<!--  },-->

<!--  computed: {-->
<!--    src(): string {-->
<!--      return this.node.attrs.src as string-->
<!--    },-->
<!--    containerWidth(): string {-->
<!--      return `${this.tempWidth}px` // 使用临时宽度-->
<!--    },-->
<!--    // containerWidth(): string {-->
<!--    //   return `${this.currentWidth}px`-->
<!--    // },-->
<!--    parsedHeight(): string {-->
<!--      const height = parseInt((this.node.attrs.height as string)?.replace('px', '') || '400')-->
<!--      return `${height}px`-->
<!--    },-->
<!--  },-->

<!--  methods: {-->
<!--    startResize(event: MouseEvent) {-->
<!--      if (this.isResizing) return-->
<!--      this.isResizing = true-->
<!--      this.startX = event.clientX-->
<!--      // this.startWidth = this.currentWidth-->
<!--      this.startWidth = this.tempWidth-->
<!--      document.addEventListener('mousemove', this.handleResize)-->
<!--      document.addEventListener('mouseup', this.stopResize)-->
<!--    },-->

<!--    handleResize(event: MouseEvent) {-->
<!--      if (!this.isResizing) return-->

<!--      // 获取编辑器内容区域宽度-->
<!--      const editorContainer = this.$el.closest('.editor-container') || document.body-->
<!--      const editorWidth = editorContainer.clientWidth-->

<!--      // 计算动态最大宽度-->
<!--      const dynamicMaxWidth = Math.min(1200, editorWidth * 0.9)-->

<!--      const delta = event.clientX - this.startX-->
<!--      let newWidth = this.startWidth + delta-->

<!--      // 限制最大宽度为编辑器宽度的90%-->
<!--      newWidth = Math.min(Math.max(300, newWidth), dynamicMaxWidth)-->

<!--      // 边界检测-->
<!--      if (Math.abs(newWidth - this.currentWidth) > 0) {-->
<!--        // this.currentWidth = newWidth-->
<!--        // this.updateAttributes({ width: `${newWidth}px` })-->
<!--        this.tempWidth = newWidth-->
<!--      }-->
<!--    },-->

<!--    stopResize() {-->
<!--      if (this.isResizing) {-->
<!--        this.isResizing = false-->
<!--        this.updateAttributes({ width: `${this.tempWidth}px` }) // 调整结束后更新属性-->
<!--        this.currentWidth = this.tempWidth // 同步当前宽度-->
<!--        document.removeEventListener('mousemove', this.handleResize)-->
<!--        document.removeEventListener('mouseup', this.stopResize)-->
<!--      }-->
<!--    },-->


<!--  },-->

<!--  watch: {-->
<!--    'node.attrs.width': {-->
<!--      handler(newWidth: string) {-->
<!--        const parsedWidth = parseInt(newWidth.replace('px', '')) || 600-->
<!--        if (parsedWidth !== this.currentWidth) {-->
<!--          this.currentWidth = parsedWidth-->
<!--          this.tempWidth = parsedWidth-->
<!--          this.startWidth = parsedWidth // 同步基准值-->
<!--        }-->
<!--      },-->
<!--      immediate: true,-->
<!--    },-->

<!--    selected(newVal) {-->
<!--      if (!newVal) {-->
<!--        // 取消选中时强制结束调整状态-->
<!--        this.stopResize()-->
<!--      }-->
<!--    },-->
<!--  },-->
<!--})-->
<!--</script>-->

