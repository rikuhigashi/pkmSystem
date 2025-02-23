import type { DirectiveBinding } from 'vue';

// 这是一个自定义指令，用于实现拖拽效果


const drag = {
  mounted(el: HTMLElement, binding: DirectiveBinding) {
    el.style.position = 'absolute';
    el.style.cursor = 'move';

    el.onmousedown = (e) => {
      const disX = e.clientX - el.offsetLeft;
      const disY = e.clientY - el.offsetTop;

      document.onmousemove = (e) => {
        el.style.left = `${e.clientX - disX}px`;
        el.style.top = `${e.clientY - disY}px`;
      };

      document.onmouseup = () => {
        document.onmousemove = null;
        document.onmouseup = null;
      };
    };
  },
};

export default drag;