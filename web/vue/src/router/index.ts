import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import RegisterView from '../views/register/RegisterView.vue';
import LoginView from '../views/auth/LoginView.vue';
import FindPasswordView from '../views/auth/FindPasswordView.vue';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/about',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/AboutView.vue'),
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
  ],
})

export default router
