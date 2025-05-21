import { Node } from '@tiptap/core'
import { VueNodeViewRenderer } from '@tiptap/vue-3'
import VideoComponent from '@/components/mainComponent/VideoComponent.vue'

declare module '@tiptap/core' {
  interface Commands<ReturnType> {
    video: {
      setVideo: (options: { src: string }) => ReturnType
    }
  }
}

export const VideoExtension = Node.create({
  name: 'video',
  group: 'block',
  draggable: true,

  addAttributes() {
    return {
      src: {
        default: null,
        parseHTML: element => element.getAttribute('src'),
        renderHTML: attributes => ({ src: attributes.src })
      },
      width: {
        default: '800px',
        parseHTML: element => element.getAttribute('width') || '800px',
        renderHTML: attributes => ({ width: attributes.width })
      },
      height: {
        default: '400',
        parseHTML: element => element.getAttribute('height') || '400',
        renderHTML: attributes => ({ height: attributes.height })
      }
    }
  },

  parseHTML() {
    return [{
      tag: 'div[data-video]',
    }]
  },

  renderHTML({ HTMLAttributes }) {
    return ['div', { 'data-video': '' }, [
      'iframe',
      {
        ...HTMLAttributes,
        frameborder: '0',
        allowfullscreen: 'true',
        loading: 'lazy'
      }
    ]]
  },

  addNodeView() {
    return VueNodeViewRenderer(VideoComponent)
  },

  addCommands() {
    const parseVideoUrl = (url: string): string | null => {
      const bilibiliRegex = /(?:https?:\/\/)?(?:www\.)?bilibili\.com\/video\/((BV|av)[a-zA-Z0-9]+)/i
      const youtubeRegex = /(?:https?:\/\/)?(?:www\.)?(?:youtube\.com\/watch\?v=|youtu\.be\/)([a-zA-Z0-9_-]{11})/i

      const bilibiliMatch = url.match(bilibiliRegex)
      const youtubeMatch = url.match(youtubeRegex)

      if (bilibiliMatch) {
        return `https://player.bilibili.com/player.html?bvid=${bilibiliMatch[1]}`
      }

      if (youtubeMatch) {
        return `https://www.youtube.com/embed/${youtubeMatch[1]}`
      }

      return null
    }

    return {
      setVideo: ({ src }) => ({ commands }) => {
        const embedUrl = parseVideoUrl(src)
        if (!embedUrl) return false

        return commands.insertContent({
          type: this.name,
          attrs: {
            src: embedUrl,
            width: '800px',
            height: '450'
          }
        })
      }
    }
  },


})
