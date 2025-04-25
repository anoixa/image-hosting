import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import HomeView from '../views/HomeView.vue'
import RegisterView from '../views/register/RegisterView.vue';
import LoginView from '../views/auth/LoginView.vue';
import FindPasswordView from '../views/auth/FindPasswordView.vue';
import WorkplaceView from '@/views/workplace/WorkplaceView.vue';
import RecommendedView from '@/views/workplace/RecommendedView.vue';
import AboutView from '../views/workplace/AboutView.vue';
import SettingsView from '@/views/workplace/SettingsView.vue';
import AccountSettingsView from '@/views/workplace/AccountSettingsView.vue';
import UploadFileView from '@/views/workplace/UploadFileView.vue';
import UploadImageView from '@/views/workplace/UploadImageView.vue';
import MyImagesView from '@/views/workplace/MyImagesView.vue';
import MyFilesView from '@/views/workplace/MyFilesView.vue';
import ImagesDetailView from '@/views/workplace/ImagesDetailView.vue';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/workplace'
    },
    {
      path: '/auth',
      children: [
        {
          path: 'login',
          name: 'login',
          component: LoginView,
          meta: { requiresAuth: false }
        },
        {
          path: 'find-password',
          name: 'findPassword',
          component: FindPasswordView,
          meta: { requiresAuth: false }
        }
      ]
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterView,
      meta: { requiresAuth: false }
    },
    {
      path: '/workplace',
      name: 'workplace',
      component: WorkplaceView,
      meta: { requiresAuth: true },
      children: [
        {
          path: 'recommended',
          name: 'Recommended',
          component: RecommendedView,
          meta: { requiresAuth: true }
        },
        {
          path: 'my-images',
          name: 'MyImages',
          component: MyImagesView,
          meta: { requiresAuth: true, activeMenu: 'MyImages'}
        },
        {
          path: 'my-images/:imageId', // Child path
          name: 'ImageDetail', // Route name (can keep same if unique within children)
          component: ImagesDetailView, // Image detail component
          meta: { requiresAuth: true, activeMenu: 'MyImages'} // Ensure requires auth if needed
        },
        {
          path: 'upload-image',
          name: 'UploadImage',
          component: UploadImageView,
          meta: { requiresAuth: true }
        },
        {
          path: 'upload-file',
          name: 'UploadFile',
          component: UploadFileView,
          meta: { requiresAuth: true }
        },
        {
          path: 'account',
          name: 'AccountSettings',
          component: AccountSettingsView,
          meta: { requiresAuth: true }
        },
        {
          path: 'settings',
          name: 'Settings',
          component: SettingsView,
          meta: { requiresAuth: true }
        },
        {
          path: 'about',
          name: 'About',
          component: AboutView,
        },
        {
          path: 'my-files',
          name: 'MyFiles',
          component: MyFilesView,
          meta: { requiresAuth: true }
        }
      ],
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: {
        template: `
          <div class="flex flex-col items-center justify-center min-h-screen">
            <h1 class="text-6xl font-bold text-indigo-500 mb-4">404</h1>
            <p class="text-xl text-gray-600 mb-8">页面不存在</p>
            <router-link to="/" class="px-4 py-2 bg-indigo-500 text-white rounded-lg hover:bg-indigo-600 transition-colors">
              返回首页
            </router-link>
          </div>
        `
      }
    }
  ]
})

// 全局前置守卫
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)

  // 如果路由需要认证
  if (requiresAuth) {
    // 检查是否已登录
    if (!userStore.isLoggedIn) {
      ElMessage.warning('请先登录')
      next({ name: 'login' })
      return
    }

    // 如果已登录但未加载用户信息，则加载用户信息
    if (!userStore.userInfoLoaded) {
      try {
        const success = await userStore.loadUserInfo()
        if (!success) {
          ElMessage.error('获取用户信息失败，请重新登录')
          // 清除登录状态
          userStore.clearLoginState()
          next({ name: 'login' })
          return
        }
      } catch (error) {
        console.error('加载用户信息出错:', error)
        // 清除登录状态
        userStore.clearLoginState()
        next({ name: 'login' })
        return
      }
    }

    // 已登录并且加载了用户信息，继续访问
    next()
  }
  // 如果是登录页，且已登录，则重定向到工作区
  else if ((to.name === 'login' || to.name === 'register') && userStore.isLoggedIn) {
    next({ path: '/workplace' })
  }
  // 其他情况，允许访问
  else {
    next()
  }
})

export default router
