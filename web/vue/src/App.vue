<script setup lang="ts">
import { RouterView } from 'vue-router'
</script>

<template>
  <div class="h-screen">
    <!-- 使用 transition 组件包裹 RouterView 来添加路由切换动画 -->
    <transition name="fade" mode="out-in">
      <RouterView />
    </transition>
  </div>
</template>

<style scoped>
/* 确保背景图片覆盖整个视口 */
.h-screen {
  height: 100vh;
}

/* 进入和离开的动画效果 */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>
