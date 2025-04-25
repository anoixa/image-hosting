import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
// 导入 pinia-plugin-persistedstate
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate';

import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
// 不需要在这里useUserStore来手动恢复状态

import App from './App.vue'
import router from './router'
import './utils/request' // 导入请求拦截器

const app = createApp(App)

const pinia = createPinia();

// === 修正 Pinia 插件注册顺序 ===
// 在将 Pinia 挂载到 App 之前，先在 Pinia 实例上使用插件
pinia.use(piniaPluginPersistedstate);
// =============================

// 注册Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 将配置好的 Pinia 实例挂载到 App
app.use(pinia)

// 挂载路由器
app.use(router)

// 挂载 Element Plus
app.use(ElementPlus)


// 全局错误处理
app.config.errorHandler = (err, instance, info) => {
  console.error('全局错误:', err)
  console.log('错误信息:', info)
}

// === 移除手动恢复状态的逻辑 ===
// Pinia plugin persistedstate 会自动从 localStorage 读取 token
// loadUserInfo action 应该由路由守卫或需要用户信息的组件按需触发
/*
const token = localStorage.getItem('token')
if (token) {
  // 设置全局请求头 (这部分可以在 Axios 拦截器中处理，当 Store 中的 token 存在时)
  import('axios').then(module => {
    module.default.defaults.headers.common['Authorization'] = token
  })
}
*/

/*
const userStore = useUserStore(); // 这里创建 Store 实例的时机可能过早
const savedUserInfo = localStorage.getItem('user');
const savedToken = localStorage.getItem('token');

if (savedUserInfo && savedToken) {
  try {
    const userInfo = JSON.parse(savedUserInfo);
    userStore.setLoginInfo(userInfo, savedToken); // 这里的调用方式不正确，且逻辑多余
  } catch (e) {
    console.error("从 localStorage 解析用户信息失败", e);
    userStore.logout(); // 方法名应为 clearLoginState
  }
}
*/
// =============================


app.mount('#app')
