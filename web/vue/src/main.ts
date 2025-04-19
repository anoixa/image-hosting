import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'
import router from './router'
import './utils/request' // 导入请求拦截器

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(ElementPlus)

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

app.mount('#app')
