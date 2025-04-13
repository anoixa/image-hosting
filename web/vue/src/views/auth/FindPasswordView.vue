<template>
  <div class="min-h-screen w-full bg-gradient-to-br from-indigo-500/10 to-purple-500/10 flex flex-col">
    <!-- 顶部导航栏 -->
    <div class="w-full h-16 flex items-center justify-between px-8 bg-white/70 shadow-sm backdrop-blur-sm">
      <div class="flex items-center">
        <img src="@/assets/logo.png" alt="Logo" class="h-8 w-auto">
        <h1 class="ml-4 text-2xl font-bold text-gray-800">image-hosting</h1>
      </div>
      <div class="flex items-center space-x-6">
        <router-link to="/auth/login" class="text-gray-600 hover:text-indigo-600 transition-colors">
          登录
        </router-link>
        <router-link to="/register" class="px-4 py-2 bg-indigo-500 text-white rounded-lg hover:bg-indigo-600 transition-all">
          注册
        </router-link>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="container mx-auto px-8 py-12 flex-grow flex items-center justify-center">
      <div class="w-3/4 bg-white bg-opacity-70 rounded-xl shadow-xl p-8 py-12">
        <h2 class="text-3xl font-extrabold text-gray-800 mb-4">找回密码</h2>
        <p class="text-gray-600 mb-6">别担心，找回密码很容易。只需填写以下信息，我们将向您发送一封包含重置链接的电子邮件。</p>
        <FindPasswordComponent />
      </div>
    </div>

    <!-- 底部信息 -->
    <div class="mt-8 text-center text-sm text-gray-500 fixed bottom-0 w-full">
      <p>© 2024 Image-Hosting. All rights reserved.</p>
      <div class="mt-2 space-x-6">
        <a href="#" class="hover:text-indigo-600 transition-colors">关于我们</a>
        <a href="#" class="hover:text-indigo-600 transition-colors">服务条款</a>
        <a href="#" class="hover:text-indigo-600 transition-colors">隐私政策</a>
        <a href="#" class="hover:text-indigo-600 transition-colors">帮助中心</a>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import FindPasswordComponent from '@/components/FindPasswordComponent.vue';
</script>

<style scoped>
.container {
  max-width: 1200px;
}
</style>
