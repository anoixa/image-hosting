<template>
  <div class="bg-gray-50 h-full flex flex-col justify-between p-4 pr-0 rounded-md shadow-md w-full">
    <div>
      <router-link to="/workplace/recommended"
        class="flex py-2 text-gray-700 hover:bg-indigo-50 rounded-md transition-colors items-center" :class="[
          { 'bg-indigo-50 text-indigo-600 border-l-4 border-indigo-600 font-semibold': $route.path === '/workplace/recommended' },
          'hover:text-gray-900 p-3 pr-10'
        ]">
        <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24"
          xmlns="http://www.w3.org/2000/svg">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8">
          </path>
        </svg>
        <span class="truncate">推荐</span>
      </router-link>
      <router-link to="/workplace/my-images"
        class="flex py-2 mt-2 text-gray-700 hover:bg-indigo-50 rounded-md transition-colors items-center" :class="[
          { 'bg-indigo-50 text-indigo-600 border-l-4 border-indigo-600 font-semibold': $route.path === '/workplace/my-images' },
          'hover:text-gray-900 p-3 pr-10'
        ]">
        <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24"
          xmlns="http://www.w3.org/2000/svg">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z">
          </path>
        </svg>
        <span class="truncate">我的图片</span>
      </router-link>
      <router-link to="/workplace/upload-image"
        class="flex py-2 mt-2 text-gray-700 hover:bg-indigo-50 rounded-md transition-colors items-center" :class="[
          { 'bg-indigo-50 text-indigo-600 border-l-4 border-indigo-600 font-semibold': $route.path === '/workplace/upload-image' },
          'hover:text-gray-900 p-3 pr-10'
        ]">
        <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24"
          xmlns="http://www.w3.org/2000/svg">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12"></path>
        </svg>
        <span class="truncate">图片上传</span>
      </router-link>
      <router-link to="/workplace/my-files"
        class="flex py-2 mt-2 text-gray-700 hover:bg-indigo-50 rounded-md transition-colors items-center" :class="[
          { 'bg-indigo-50 text-indigo-600 border-l-4 border-indigo-600 font-semibold': $route.path === '/workplace/my-files' },
          'hover:text-gray-900 p-3 pr-10'
        ]">
        <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24"
          xmlns="http://www.w3.org/2000/svg">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M15.172 7l-6.586 6.586a2 2 0 102.828 2.828l6.414-6.586a4 4 0 00-5.656-5.656l-6.415 6.585a6 6 0 108.486 8.486L20.5 13">
          </path>
        </svg>
        <span class="truncate">我的文件</span>
      </router-link>
      <router-link to="/workplace/upload-file"
        class="flex py-2 mt-2 text-gray-700 hover:bg-indigo-50 rounded-md transition-colors items-center" :class="[
          { 'bg-indigo-50 text-indigo-600 border-l-4 border-indigo-600 font-semibold': $route.path === '/workplace/upload-file' },
          'hover:text-gray-900 p-3 pr-10'
        ]">
        <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24"
          xmlns="http://www.w3.org/2000/svg">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12"></path>
        </svg>
        <span class="truncate">文件上传</span>
      </router-link>
    </div>
    <div>
      <router-link to="/workplace/account"
        class="flex py-2 text-gray-700 hover:bg-indigo-50 rounded-md transition-colors items-center" :class="[
          { 'bg-indigo-50 text-indigo-600 border-l-4 border-indigo-600 font-semibold': $route.path === '/workplace/account' },
          'hover:text-gray-900 p-3 pr-10'
        ]">
        <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24"
          xmlns="http://www.w3.org/2000/svg">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
        </svg>
        <span class="truncate">账号管理</span>
      </router-link>
      <router-link to="/workplace/settings"
        class="flex py-2 mt-2 text-gray-700 hover:bg-indigo-50 rounded-md transition-colors items-center" :class="[
          { 'bg-indigo-50 text-indigo-600 border-l-4 border-indigo-600 font-semibold': $route.path === '/workplace/settings' },
          'hover:text-gray-900 p-3 pr-10'
        ]">

        <svg class="w-5 h-5 mr-2" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round"
            d="M9.594 3.94c.09-.542.56-.94 1.11-.94h2.593c.55 0 1.02.398 1.11.94l.213 1.281c.063.374.313.686.645.87.074.04.147.083.22.127.325.196.72.257 1.075.124l1.217-.456a1.125 1.125 0 0 1 1.37.49l1.296 2.247a1.125 1.125 0 0 1-.26 1.431l-1.003.827c-.293.241-.438.613-.43.992a7.723 7.723 0 0 1 0 .255c-.008.378.137.75.43.991l1.004.827c.424.35.534.955.26 1.43l-1.298 2.247a1.125 1.125 0 0 1-1.369.491l-1.217-.456c-.355-.133-.75-.072-1.076.124a6.47 6.47 0 0 1-.22.128c-.331.183-.581.495-.644.869l-.213 1.281c-.09.543-.56.94-1.11.94h-2.594c-.55 0-1.019-.398-1.11-.94l-.213-1.281c-.062-.374-.312-.686-.644-.87a6.52 6.52 0 0 1-.22-.127c-.325-.196-.72-.257-1.076-.124l-1.217.456a1.125 1.125 0 0 1-1.369-.49l-1.297-2.247a1.125 1.125 0 0 1 .26-1.431l1.004-.827c.292-.24.437-.613.43-.991a6.932 6.932 0 0 1 0-.255c.007-.38-.138-.751-.43-.992l-1.004-.827a1.125 1.125 0 0 1-.26-1.43l1.297-2.247a1.125 1.125 0 0 1 1.37-.491l1.216.456c.356.133.751.072 1.076-.124.072-.044.146-.086.22-.128.332-.183.582-.495.644-.869l.214-1.28Z" />
          <path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z" />
        </svg>
        <span class="truncate">设置</span>
      </router-link>
      <router-link to="/workplace/about"
        class="flex py-2 mt-2 text-gray-700 hover:bg-indigo-50 rounded-md transition-colors items-center" :class="[
          { 'bg-indigo-50 text-indigo-600 border-l-4 border-indigo-600 font-semibold': $route.path === '/workplace/about' },
          'hover:text-gray-900 p-3 pr-10'
        ]">
        <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24"
          xmlns="http://www.w3.org/2000/svg">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
        </svg>
        <span class="truncate">关于</span>
      </router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router';

const route = useRoute();
</script>

<style scoped>
/* 可以根据需要添加其他组件特定的样式 */
.truncate {
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
</style>
