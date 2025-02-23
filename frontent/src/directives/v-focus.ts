import type { DirectiveBinding } from 'vue';

// 这是一个自定义指令，用于自定义input自动获取焦点
// 使用方法    v-focus="true"



export default {
  mounted(el: HTMLElement, binding: DirectiveBinding) {
    if (binding.value) {
      el.focus();
    }
  },
  updated(el: HTMLElement, binding: DirectiveBinding) {
    if (binding.value) {
      el.focus();
    }
  }
};