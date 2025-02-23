import { nextTick, ref } from 'vue'
import sideUtils from '../components/side/side'
import { inputValue } from '../components/side/side'
import { userInputStore } from '@/stores/inputState'

// 这是一个右键菜单方法的ts


let inputStore: ReturnType<typeof userInputStore> | null = null  // 初始化为 null

// 使用 nextTick 确保代码在 Vue 应用挂载之后执行
nextTick(() => {
  inputStore = userInputStore()  // 在这里初始化 InputStore
})



// 当输入框上有blur与keyup.enter事件时
// 因为bulr是失去焦点激活，enter事件点击时也会失去焦点
// 导致了点击回车时两个事件会一起激活
// 所以需要一个标志符来判断是否是激活了回车事件
export const isEnterPressed = ref(false) //标志符

// 键盘按下时
export const handleKeyup = () => {
  isEnterPressed.value = true
}

// 失去焦点时
export const handleBlur = () => {
  isEnterPressed.value = false
}




// 保存方法
export const saveInputDate = () => {
  // 确保 rightMenuStore 已经初始化
  if (inputStore) {
    inputStore.closeInput()  // 关闭输入框
    sideUtils.addItem()  // 调用 sideUtils 的保存方法
    inputValue.value = ''  // 清空 inputValue

    // console.log("sava success");

  } else {
    console.log('addSave fail')
  }
}


// 编辑保存方法
export const saveEditData = (currentValue: any, editInputValue: any) => {
  if (inputStore) {
    currentValue.name = editInputValue
    inputStore.isEditingActive = false
  } else {
    console.log('editSave fail')
  }
}


export default {
  isEnterPressed,
  saveInputDate, saveEditData, handleBlur, handleKeyup

}
