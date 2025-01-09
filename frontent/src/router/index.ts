import { createRouter, createWebHistory } from 'vue-router'

import HomeView from '@/views/homeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
      beforeEnter: (to, from, next) => {
        console.log('Before entering Home route');
        next(); // 允许导航
      },
    },
    {
      path: '/sideList',
      name: 'sideList',
      component: () => import('../components/side/sideComponents/sideList.vue'),
      meta: { standalone: true },
    },
  ],
})

export default router
