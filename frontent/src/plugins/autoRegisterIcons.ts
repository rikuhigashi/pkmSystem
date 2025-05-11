import type { App, Component } from 'vue'

// export default function registerIcons(app: App) {
//   // 自动导入 `src/icons` 文件夹及其子文件夹中的所有 .vue 文件
//   const iconModules = import.meta.glob('../assets/icons/**/*.vue', { eager: true });
//
//   for (const [path, component] of Object.entries(iconModules)) {
//     // 获取组件名称，例如 iconLeftArrow、iconVipAct
//     const componentName = path.split('/').pop()?.replace('.vue', '');
//     if (componentName) {
//       app.component(componentName, (component as any).default);
//     }
//   }
// }


// 全局引入assets目录下的所有icon图标

export default function registerIcons(app: App) {
  const iconModules = import.meta.glob<{ default: Component }>('@/assets/icons/**/*.vue', {
    eager: true
  });

  Object.entries(iconModules).forEach(([path, module]) => {

    const componentName = path.split('/').pop()?.replace(/\.\w+$/, '');
    if (componentName) {
      app.component(componentName, module.default);
    }
  });
}
