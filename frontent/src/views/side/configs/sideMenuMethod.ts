import { nextTick } from 'vue'
import sideUtils, { iconMap, inputForm, navigation } from '@/views/side/side'
import { userInputStore } from '@/stores/inputState'
import {
  addSideData,
  apiCopySideData,
  deleteSideData,
  getSideAllData,
  upDataMainData,
  upDataSideData,
} from '@/API/side/sideAPI'
import type { sideListItem } from '@/views/side/types/sideTypes'
import { useAlertStore } from '@/stores/alert'
import { useEditorStore } from '@/stores/main/editorStore'
import { loadMainData } from '@/views/main/configs/mainConfigs'
import { AxiosError } from 'axios'

// 这是一个右键菜单方法的ts

let inputStore: ReturnType<typeof userInputStore> | null = null // 初始化为 null
let alertStore: ReturnType<typeof useAlertStore> | null = null
let editorStore: ReturnType<typeof useEditorStore> | null = null
// 使用 nextTick 确保代码在 Vue 应用挂载之后执行
nextTick(() => {
  inputStore = userInputStore() // 在这里初始化 InputStore
  alertStore = useAlertStore()
  editorStore = useEditorStore()
})

// 创建时的默认值
const createDefaultContent = (): Record<string, unknown> => ({
  type: 'doc',
  content: [
    {
      type: 'heading',
      attrs: { level: 2 },
      content: [{ type: 'text', text: '新文档' }],
    },
  ],
})

// 设置高亮，并切换
const highlightItem = async (newSideItem: sideListItem) => {
  if (editorStore) {
    editorStore.setCurrentDocId(newSideItem.id)
  }

  sideUtils.navigation.value = sideUtils.navigation.value.map((item) => ({
    ...item,
    current: item.id === newSideItem.id,
  }))
}

// 加载side的全部数据
export const loadSideData = async () => {
  try {
    const res = await getSideAllData()

    const currentId = sideUtils.navigation.value.find((item) => item.current)?.id
    navigation.value = res.map((item: sideListItem) => ({
      ...item,
      iconComponent: iconMap[item.icon],
      current: item.id === currentId,
    }))
  } catch (error) {
    console.log('side后端请求失败!', error)
  }
}

// 添加方法
export const saveInputDate = async () => {
  try {
    // 设置默认名称逻辑
    const userName = inputForm.value.name.trim()
    const defaultName = '未命名文档'

    const payload = {
      ...inputForm.value,
      name: userName || defaultName,
      tiptapJson: createDefaultContent(), // 初始化编辑器内容
    }

    const addedData = await addSideData(payload)

    // 关闭输入框
    inputStore?.closeInput()
    sideUtils.inputForm.value = { name: '', herf: '#', icon: 'HomeIcon' }

    await loadSideData()

    await loadMainData(addedData.id)

    // 设置高亮，并切换
    await highlightItem(addedData)
  } catch (error) {
    console.log(error)
  }
}

// 编辑方法
export const saveEditData = async (id: number, editInputValue: sideListItem) => {
  try {
    if (editInputValue.name != '') {
      inputStore?.closeInputError()

      const newSideItem = await upDataSideData(id, editInputValue)

      // 关闭编辑输入框
      inputStore?.closeEditingInput()

      // 加载主数据
      await loadSideData()

      // 设置高亮，并切换
      await highlightItem(newSideItem)
    } else {
      inputStore?.activationInputError()
    }
  } catch (error) {
    console.log(error)
  }
}

export const saveMainData = async () => {
  try {
    if (editorStore && alertStore) {
      if (!editorStore.currentDocId || !editorStore.editorContent) {
        alertStore.showAlert('没有可保存的内容', 'warning')
        return
      }
    }

    // console.log('editorStore?.currentDocId:', editorStore?.currentDocId)
    // console.log('editorStore?.editorContent:', editorStore?.editorContent)

     await upDataMainData(editorStore?.currentDocId, editorStore?.editorContent)

    if (alertStore){
      alertStore.showAlert('保存成功', 'success')
    }

  } catch (error) {
    console.error(error)
  }
}

// 复制方法
export const copySideData = async (copiedItem: sideListItem) => {
  try {
    const newSideItem = await apiCopySideData(copiedItem.id)

    await loadSideData()

    // 设置高亮，并切换
    await highlightItem(newSideItem)

    // 加载主数据
    await loadMainData(newSideItem.id)

    return newSideItem
  } catch (error) {
    console.error(error)
  }
}

// 删除方法
export const savaDeleteSideData = async (id: number) => {
  try {
    await deleteSideData(id)

    if (sideUtils.navigation.value.length <= 1) {
      alertStore?.showAlert('无法删除最后一个文档', 'warning')
      return
    }

    if (alertStore) {
      alertStore.showAlert('删除成功！', 'success')
    }
    await loadSideData()

    // 如果删除的是当前选中项，自动选择第一个
    if (sideUtils.navigation.value.length > 0) {
      sideUtils.navigation.value = sideUtils.navigation.value.map((item, index) => ({
        ...item,
        current: index === 0,
      }))
    }

    const newSelectedItem = sideUtils.navigation.value.find((item) => item.current)

    if (newSelectedItem && editorStore) {
      // 更新当前文档 ID
      editorStore.setCurrentDocId(newSelectedItem.id)
      // 加载主数据
      await loadMainData(newSelectedItem.id)
    }
  } catch (error: unknown) {
    if (error instanceof AxiosError) {
      if (error.response?.data?.includes('必须保留至少一个文档')) {
        alertStore?.showAlert('必须保留至少一个文档', 'warning')
      } else {
        alertStore?.showAlert('删除失败,必须保留至少一个文档', 'error')
      }
      console.log(error)
    }
  }
}

export default {
  loadSideData,
  saveInputDate,
  saveEditData,
  copySideData,
  saveMainData,
  savaDeleteSideData,
}
