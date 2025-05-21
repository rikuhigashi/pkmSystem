<template>
  <node-view-wrapper
    as="div"
    class="video-container"
    :class="{ selected }"
    :style="{ width: containerWidth }"
  >
    <iframe
      :src="src"
      :width="currentWidth"
      :height="parsedHeight"
      allowfullscreen
      class="video-iframe"
    />
    <div v-if="selected" class="resize-handle" @mousedown="startResize" />
  </node-view-wrapper>
</template>

<script lang="ts">
import { NodeViewWrapper, nodeViewProps } from '@tiptap/vue-3'
import { defineComponent, type PropType } from 'vue'

export default defineComponent({
  name: 'VideoComponent',
  components: { NodeViewWrapper },

  props: {
    ...nodeViewProps,
    selected: {
      type: Boolean as PropType<boolean>,
      required: true,
    },
    updateAttributes: {
      type: Function as PropType<(attrs: Record<string, unknown>) => void>,
      required: true,
    },
  },

  data() {
    const editorWidth = this.$el?.closest('.editor-container')?.clientWidth || 800
    const initialWidth = Math.min(800, editorWidth * 0.8)
    return {
      isResizing: false,
      startX: 0,
      startWidth: initialWidth,
      currentWidth: initialWidth,
      tempWidth: initialWidth,
    }
  },

  computed: {
    src(): string {
      return this.node.attrs.src as string
    },
    containerWidth(): string {
      return `${this.tempWidth}px` // 使用临时宽度
    },
    // containerWidth(): string {
    //   return `${this.currentWidth}px`
    // },
    parsedHeight(): string {
      const height = parseInt((this.node.attrs.height as string)?.replace('px', '') || '400')
      return `${height}px`
    },
  },

  methods: {
    startResize(event: MouseEvent) {
      if (this.isResizing) return
      this.isResizing = true
      this.startX = event.clientX
      // this.startWidth = this.currentWidth
      this.startWidth = this.tempWidth
      document.addEventListener('mousemove', this.handleResize)
      document.addEventListener('mouseup', this.stopResize)
    },

    handleResize(event: MouseEvent) {
      if (!this.isResizing) return

      // 获取编辑器内容区域宽度
      const editorContainer = this.$el.closest('.editor-container') || document.body
      const editorWidth = editorContainer.clientWidth

      // 计算动态最大宽度
      const dynamicMaxWidth = Math.min(1200, editorWidth * 0.9)

      const delta = event.clientX - this.startX
      let newWidth = this.startWidth + delta

      // 限制最大宽度为编辑器宽度的90%
      newWidth = Math.min(Math.max(300, newWidth), dynamicMaxWidth)

      // 边界检测
      if (Math.abs(newWidth - this.currentWidth) > 0) {
        // this.currentWidth = newWidth
        // this.updateAttributes({ width: `${newWidth}px` })
        this.tempWidth = newWidth
      }
    },

    stopResize() {
      if (this.isResizing) {
        this.isResizing = false
        this.updateAttributes({ width: `${this.tempWidth}px` }) // 调整结束后更新属性
        this.currentWidth = this.tempWidth // 同步当前宽度
        document.removeEventListener('mousemove', this.handleResize)
        document.removeEventListener('mouseup', this.stopResize)
      }
    },


  },

  watch: {
    'node.attrs.width': {
      handler(newWidth: string) {
        const parsedWidth = parseInt(newWidth.replace('px', '')) || 600
        if (parsedWidth !== this.currentWidth) {
          this.currentWidth = parsedWidth
          this.tempWidth = parsedWidth
          this.startWidth = parsedWidth // 同步基准值
        }
      },
      immediate: true,
    },

    selected(newVal) {
      if (!newVal) {
        // 取消选中时强制结束调整状态
        this.stopResize()
      }
    },
  },
})
</script>

<style scoped>
.video-container {
  position: relative;
  display: inline-block;
  min-width: 400px;
  max-width: 100%;
  margin: 1rem auto;
  padding: 12px;
  background: #f5f5f5;
  border-radius: 12px;
  will-change: width;
  backface-visibility: hidden;
}

.video-iframe {
  width: calc(100% - 24px);
  height: calc(400px - 24px);
  margin: 12px;
  aspect-ratio: 16/9;
  border-radius: 8px;
  background: #000;
  transition: inherit;
}

.resize-handle {
  position: absolute;
  right: 12px;
  bottom: 12px;
  width: 16px;
  height: 16px;
  background: #4f46e5;
  border: 2px solid white;
  border-radius: 50%;
  cursor: nwse-resize;
  transform: translate(50%, 50%);
  z-index: 10;
}

.selected {
  outline: 3px solid rgba(99, 102, 241, 0.5);
  background: rgba(99, 102, 241, 0.05);
  outline-offset: 2px;
}
</style>
