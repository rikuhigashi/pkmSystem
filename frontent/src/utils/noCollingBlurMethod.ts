// 当输入框上有blur与keyup.enter事件时
// 因为bulr是失去焦点激活，enter事件点击时也会失去焦点
// 导致了点击回车时两个事件会一起激活
// 所以需要一个标志符来判断是否是激活了回车事件
import {ref} from "vue";

export const isEnterPressed = ref(false) //标志符

// 键盘按下时
export const handleKeyup = () => {
  isEnterPressed.value = true
}

// 失去焦点时
export const handleBlur = () => {
  isEnterPressed.value = false
}

export  default {
  isEnterPressed,
  handleKeyup,
  handleBlur
}
