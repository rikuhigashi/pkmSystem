import { defineStore } from 'pinia';

export const useCollaborationStore = defineStore('collaboration', {
  state: () => ({
    isActive: false,
    username: '',
    color: '#f783ac',
  }),
  actions: {
    setCollaborationUser(name: string) {
      this.username = name;
      this.isActive = true;

      // 生成用户颜色
      let hash = 0;
      for (let i = 0; i < name.length; i++) {
        hash = name.charCodeAt(i) + ((hash << 5) - hash);
      }
      const color = Math.floor(Math.abs(((Math.sin(hash) * 10000) % 1) * 16777215)).toString(16);

      this.color = `#${'000000'.slice(0, 6 - color.length)}${color}`;
    },
  },
});
