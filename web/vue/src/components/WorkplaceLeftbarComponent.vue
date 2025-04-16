<template>
  <div class="bg-gray-100 h-full flex flex-col justify-between p-4">
    <div>
      <router-link
        to="/workplace"
        class="block py-2 text-gray-800 hover:bg-indigo-200/50 rounded transition-colors"
        :class="[
          { 'bg-indigo-200/50 text-indigo-500 ring-2 ring-indigo-500': $route.path === '/workplace' },
          'hover:text-gray-800' // Keep text dark on hover if not active
        ]"
      >
        推荐
      </router-link>
      <router-link
        to="/workplace/my-images"
        class="block py-2 mt-2 text-gray-800 hover:bg-indigo-200/50 rounded transition-colors"
        :class="[
          { 'bg-indigo-200/50 text-indigo-500 ring-2 ring-indigo-500': $route.path === '/workplace/my-images' },
          'hover:text-gray-800'
        ]"
      >
        我的图片
      </router-link>
      <router-link
        to="/workplace/upload-image"
        class="block py-2 mt-2 text-gray-800 hover:bg-indigo-200/50 rounded transition-colors"
        :class="[
          { 'bg-indigo-200/50 text-indigo-500 ring-2 ring-indigo-500': $route.path === '/workplace/upload-image' },
          'hover:text-gray-800'
        ]"
      >
        图片上传
      </router-link>
      <router-link
        to="/workplace/upload-file"
        class="block py-2 mt-2 text-gray-800 hover:bg-indigo-200/50 rounded transition-colors"
        :class="[
          { 'bg-indigo-200/50 text-indigo-500 ring-2 ring-indigo-500': $route.path === '/workplace/upload-file' },
          'hover:text-gray-800'
        ]"
      >
        文件上传
      </router-link>
    </div>
    <div>
      <router-link
        to="/workplace/account"
        class="block py-2 text-gray-800 hover:bg-indigo-200/50 rounded transition-colors"
        :class="[
          { 'bg-indigo-200/50 text-indigo-500 ring-2 ring-indigo-500': $route.path === '/workplace/account' },
          'hover:text-gray-800'
        ]"
      >
        账号管理
      </router-link>
      <router-link
        to="/workplace/settings"
        class="block py-2 mt-2 text-gray-800 hover:bg-indigo-200/50 rounded transition-colors"
        :class="[
          { 'bg-indigo-200/50 text-indigo-500 ring-2 ring-indigo-500': $route.path === '/workplace/settings' },
          'hover:text-gray-800'
        ]"
      >
        设置
      </router-link>
      <router-link
        to="/workplace/about"
        class="block py-2 mt-2 text-gray-800 hover:bg-indigo-200/50 rounded transition-colors"
        :class="[
          { 'bg-indigo-200/50 text-indigo-500 ring-2 ring-indigo-500': $route.path === '/workplace/about' },
          'hover:text-gray-800'
        ]"
      >
        关于
      </router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router';

const route = useRoute();
</script>

<style scoped>
/* You can add any specific styles for this component here */
</style>
