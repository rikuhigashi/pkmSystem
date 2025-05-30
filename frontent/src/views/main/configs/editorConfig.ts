import {type EditorOptions } from '@tiptap/core'
import { Doc } from 'yjs'
import { WebsocketProvider } from 'y-websocket'
import {type Ref } from 'vue'
import StarterKit from '@tiptap/starter-kit'
import Underline from '@tiptap/extension-underline'
import TextStyle from '@tiptap/extension-text-style'
import Color from '@tiptap/extension-color'
import Link from '@tiptap/extension-link'
import Highlight from '@tiptap/extension-highlight'
import Subscript from '@tiptap/extension-subscript'
import Superscript from '@tiptap/extension-superscript'
import TextAlign from '@tiptap/extension-text-align'
import FontFamily from '@tiptap/extension-font-family'
import ListItem from '@tiptap/extension-list-item'
import OrderedList from '@tiptap/extension-ordered-list'
import Placeholder from '@tiptap/extension-placeholder'
import { Collaboration } from '@tiptap/extension-collaboration'
import { CollaborationCursor } from '@tiptap/extension-collaboration-cursor'
import CodeBlockLowlight from '@tiptap/extension-code-block-lowlight'
import { createLowlight } from 'lowlight'
import css from 'highlight.js/lib/languages/css'
import js from 'highlight.js/lib/languages/javascript'
import ts from 'highlight.js/lib/languages/typescript'
import html from 'highlight.js/lib/languages/xml'
import { CustomImage } from '@/extensions/customImage'
import { VideoExtension } from '@/extensions/video'
import {useEditorStore} from "@/stores/main/editorStore";

const lowlight = createLowlight()

lowlight.register('html', html)
lowlight.register('css', css)
lowlight.register('javascript', js)
lowlight.register('typescript', ts)

export const useEditorConfig = (
  ydoc: Doc,
  websocketProvider: Ref<WebsocketProvider | null>,
  currentUser: Ref<{ name: string; color: string }>
): Partial<EditorOptions> => {
  return {
    onUpdate: ({ editor }) => {
      // 获取到编辑器内部内容
      useEditorStore().editorContent = editor.getJSON()
    },
    extensions: [
      ...(websocketProvider?.value ? [
        Collaboration.configure({ document: ydoc }),
        CollaborationCursor.configure({
          provider: websocketProvider.value,
          user: currentUser.value,
        })
      ] : []),
      StarterKit.configure({
        codeBlock: false,
        listItem: false,
        orderedList: false,
        heading: {
          levels: [1, 2, 3, 4],
          HTMLAttributes: { class: 'my-heading' },
        },
        paragraph: {
          HTMLAttributes: {
            class: 'prose-paragraph',
            contenteditable: 'true',
          },
        },
      }),
      Placeholder.configure({
        placeholder: ({ node }) => node.type.name === 'paragraph' ? '开始输入内容...' : '',
        showOnlyWhenEditable: true,
        showOnlyCurrent: true,
        emptyNodeClass: 'is-empty',
      }),
      TextStyle,
      Color,
      Underline,
      Link.configure({ openOnClick: true, autolink: true }),
      CodeBlockLowlight.configure({ lowlight, defaultLanguage: 'typescript' }),
      Highlight.configure({ multicolor: true }),
      Subscript,
      Superscript,
      TextAlign.configure({
        types: ['heading', 'paragraph'],
        defaultAlignment: 'left',
      }),
      FontFamily.configure({ types: ['textStyle'] }),
      ListItem,
      OrderedList,
      CustomImage,
      VideoExtension,
    ],
  }
}
