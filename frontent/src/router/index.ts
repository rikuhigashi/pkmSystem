import { createRouter, createWebHistory } from 'vue-router'

import loginView from '@/views/loginAndRegistration/loginView.vue'
import { userAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'login',
      component: loginView,
      beforeEnter: (to, from, next) => {
        next() // 允许导航
      },
      meta: { shouldRedirectIfLoggedIn: true },
    },

    {
      path: '/home',
      name: 'home',
      component: () => import('@/views/HomeView.vue'),
      meta: { requiresAuth: true }, //标记需要登录
    },

    {
      path: '/sideList',
      name: 'sideList',
      component: () => import('@/components/sideComponents/sideList.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/registerView',
      name: 'registerView',
      component: () => import('@/views/loginAndRegistration/registerView.vue'),
      meta: { standalone: true, shouldRedirectIfLoggedIn: true },
    },
    {
      path: '/forgotPassword',
      name: 'forgotPassword',
      component: () => import('@/views/loginAndRegistration/forgotPassword.vue'),
      meta: { standalone: true },
    },
    {
      path: '/resetPassword',
      name: 'resetPassword',
      component: () => import('@/views/loginAndRegistration/resetPassword.vue'),
      meta: { standalone: true },
    },
    {
      path: '/adminDashboard',
      name: 'adminDashboard',
      component: () => import('@/views/admin/adminDashboard.vue'),
      meta: { requiresAuth: true, requiresAdmin: true },
    },
    {
      path: '/AdminPendingReview',
      name: 'AdminPendingReview',

      component: () => import('@/views/admin/AdminPendingReview.vue'),
      meta: { requiresAuth: true, requiresAdmin: true },
    },
    {
      path: '/sponsorshipView',
      name: 'sponsorshipView',
      component: () => import('@/views/sponsorship/sponsorshipView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/AccountDetailView',
      name: 'AccountDetailView',
      component: () => import('@/views/user/AccountDetailView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/payment/success',
      name: 'paymentSuccess',
      component: () => import('@/views/sponsorship/PaymentResultView.vue'),
      meta: { requiresAuth: true },
      props: (route) => ({ orderNo: route.query.orderNo }),
    },
    {
      path: '/collaborationView',
      name: 'collaborationView',
      component: () => import('@/views/collaboration/collaborationView.vue'),
      meta: { requiresAuth: true },
      beforeEnter: async (to, from, next) => {
        // 动态导入协作存储
        const { useCollaborationStore } = await import('@/stores/collaborationStore')
        const collaborationStore = useCollaborationStore()

        // 如果路由中有查询参数，优先使用
        if (to.query.roomId && to.query.username) {
          collaborationStore.setCollaborationInfo(
            to.query.roomId as string,
            to.query.username as string,
          )
          return next()
        }

        // 检查协作信息是否完整
        if (!collaborationStore.room || !collaborationStore.username) {
          next({ name: 'home' }) // 重定向到主页
        } else {
          next() // 继续访问
        }
      },
    },
    {
      path: '/knowledge',
      name: 'knowledgeSquare',
      component: () => import('@/views/knowledge/KnowledgeSquareView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/knowledge/upload',
      name: 'uploadKnowledge',
      component: () => import('@/views/knowledge/UploadKnowledgeView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/knowledge/my',
      name: 'myKnowledge',
      component: () => import('@/views/knowledge/MyKnowledgeView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/knowledge/:id',
      name: 'knowledgeDetail',
      component: () => import('@/views/knowledge/KnowledgeDetailView.vue'),
      meta: { requiresAuth: true },
    }


  ],
})

router.beforeEach(async (to, from, next) => {
  const authStore = userAuthStore()
  await authStore.checkSession()

  // 已登录用户访问重定向
  if (to.meta.shouldRedirectIfLoggedIn && authStore.isLoggedIn) {
    return next({ name: 'home' })
  }

  // 未登录用户访问需要认证的页面
  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    return next({ name: 'login' })
  }

  next()
})

export default router
