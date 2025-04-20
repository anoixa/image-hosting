import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate';
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { useUserStore } from './stores/user'

import App from './App.vue'
import router from './router'
import './utils/request' // 导入请求拦截器

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(ElementPlus)

const pinia = createPinia();
pinia.use(piniaPluginPersistedstate);

// 注册Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 全局错误处理
app.config.errorHandler = (err, instance, info) => {
  console.error('全局错误:', err)
  console.log('错误信息:', info)
}

// 初始化令牌
const token = localStorage.getItem('token')
if (token) {
  // 设置全局请求头
  import('axios').then(module => {
    module.default.defaults.headers.common['Authorization'] = token
  })
}

// --- 手动恢复状态逻辑 ---
const userStore = useUserStore();
const savedUserInfo = localStorage.getItem('user'); // 或你保存 user info 的键名
const savedToken = localStorage.getItem('token');     // 或你保存 token 的键名

if (savedUserInfo && savedToken) {
  try {
    const userInfo = JSON.parse(savedUserInfo);
    // 确保你的 store 中有一个方法来设置全部登录状态
    userStore.setLoginInfo(userInfo, savedToken);
  } catch (e) {
    console.error("从 localStorage 解析用户信息失败", e);
    userStore.logout(); // 清除无效状态
  }
}

app.mount('#app')
