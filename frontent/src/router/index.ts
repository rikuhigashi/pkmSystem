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
        // console.log('Before entering Home route');
        next() // 允许导航
      },
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
      meta: { requiresAuth: true },
    },
    {
      path: '/AdminPendingReview',
      name: 'AdminPendingReview',

      component: () => import('@/views/admin/AdminPendingReview.vue'),
      meta: { requiresAuth: true },
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
      props: (route) => ({ orderNo: route.query.orderNo })
    },
  ],
})

// router.beforeEach(async (to, from, next) => {
//   const authStore = userAuthStore()
//   // const isAuthenticated = authStore.isLoggedIn
//
//   await authStore.checkSession();
//
//
//
//   if (to.name === 'login' && authStore.isLoggedIn) {
//     next({ name: 'home' })
//   } else if (to.meta.requiresAuth && !authStore.isLoggedIn) {
//     next({ name: 'login' })
//   } else {
//     next()
//   }
// })

router.beforeEach(async (to, from, next) => {
  const authStore = userAuthStore()
  await authStore.checkSession()

  if (to.meta.shouldRedirectIfLoggedIn && authStore.isLoggedIn) {
    return next({ name: 'home' })
  }

  // 已登录用户访问登录/注册/忘记密码页面时重定向到首页
  if (
    (to.name === 'login' ||
      to.name === 'registerView' ||
      to.name === 'forgotPassword' ||
      to.name === 'resetPassword') &&
    authStore.isLoggedIn
  ) {
    next({ name: 'home' })
  }
  // 需要登录的页面未登录时跳转登录页
  else if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    next({ name: 'login' })
  } else {
    next()
  }
})

export default router
