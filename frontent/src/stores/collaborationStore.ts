// stores/collaborationStore.ts
import { defineStore } from 'pinia';

export const useCollaborationStore = defineStore('collaboration', {
  state: () => ({
    connected: false,
    username: '匿名用户',
    color: getRandomColor(),
  }),
  actions: {
    setConnected(status: boolean) {
      this.connected = status;
    },
    setUsername(name: string) {
      this.username = name;
    },
    setColor(color: string) {
      this.color = color;
    },
  },
});

// 生成随机颜色
function getRandomColor() {
  const colors = [
    '#FF6B6B', '#4ECDC4', '#FFD166', '#6A0572', '#AB83A1',
    '#1A535C', '#4A7C59', '#F75C03', '#2274A5', '#F1C40F'
  ];
  return colors[Math.floor(Math.random() * colors.length)];
}
