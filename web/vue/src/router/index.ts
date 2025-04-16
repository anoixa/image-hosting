import { createRouter, createWebHistory } from 'vue-router'
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

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterView,
    },
    {
      path: '/auth/login',
      name: 'login',
      component: LoginView,
    },
    {
      path: '/auth/find-password',
      name: 'find-password',
      component: FindPasswordView,
    },
    {
      path: '/workplace',
      name: 'workplace',
      component: WorkplaceView,
      children: [
        {
          path: 'recommended',
          name: 'Recommended',
          component: RecommendedView,
        },
        {
          path: 'my-images',
          name: 'MyImages',
          component: MyImagesView,
        },
        {
          path: 'upload-image',
          name: 'UploadImage',
          component: UploadImageView,
        },
        {
          path: 'upload-file',
          name: 'UploadFile',
          component: UploadFileView,
        },
        {
          path: 'account',
          name: 'AccountSettings',
          component: AccountSettingsView,
        },
        {
          path: 'settings',
          name: 'Settings',
          component: SettingsView,
        },
        {
          path: 'about',
          name: 'About',
          component: AboutView,
        },
      ],
    },
  ],
})

export default router
