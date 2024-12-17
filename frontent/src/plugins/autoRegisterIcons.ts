import type { App } from "vue";


export default function registerIcons(app: App) {
  // 自动导入 `src/icons` 文件夹及其子文件夹中的所有 .vue 文件
  const iconModules = import.meta.glob('../components/icons/**/*.vue', { eager: true });

  for (const [path, component] of Object.entries(iconModules)) {
    // 获取组件名称，例如 iconLeftArrow、iconVipAct
    const componentName = path.split('/').pop()?.replace('.vue', '');
    if (componentName) {
      app.component(componentName, (component as any).default);
    }
  }
}
