import { Editor } from '@tiptap/vue-3'
import {getFullData} from '@/API/side/sideAPI'

export const loadMainData = async ( id: number,editor?: Editor) => {
  try {

    const res = await getFullData(id)
    // console.log(res)
    const jsonData = res.tiptapJson

    if (editor) {
      editor.commands.setContent(jsonData || createDefaultContent())
    }

    return true
  } catch (error) {
    console.error('加载数据失败:', error)
    return false
  }
}


const createDefaultContent = () => ({
  type: 'doc',
  content: [
    {
      type: 'heading',
      attrs: { level: 2 },
      content: [{ type: 'text', text: '欢迎使用' }]
    },
    {
      type: 'paragraph',
      content: [{
        type: 'text',
        text: '这是一个默认文档，右键菜单可以创建更多内容'
      }]
    }
  ]
})
