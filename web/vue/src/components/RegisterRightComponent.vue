<template>
  <div class="h-800 bg-white/90 rounded-xl shadow-lg overflow-hidden relative group">
    <!-- 背景图片网格 -->
    <div class="absolute inset-0 overflow-hidden h-800">
      <div class="columns-2 gap-4 p-6 animate-scroll-continuous">
        <div v-for="(image, index) in images.slice(0, 6)" :key="index" 
             class="mb-4 break-inside-avoid overflow-hidden rounded-xl shadow-sm">
          <img :src="image.url" :alt="image.alt" 
               class="w-full h-32 object-cover" />
        </div>
        <!-- 重复图片以实现无限滚动效果 -->
        <div v-for="(image, index) in images.slice(0, 6)" :key="'dup-'+index" 
             class="mb-4 break-inside-avoid overflow-hidden rounded-xl shadow-sm">
          <img :src="image.url" :alt="image.alt" 
               class="w-full h-32 object-cover" />
        </div>
      </div>
    </div>
    
    <!-- 半透明遮罩 -->
    <div class="absolute inset-0 bg-white/70 backdrop-blur-sm"></div>
    
    <!-- 内容 -->
    <div class="relative h-full z-10 flex flex-col justify-center items-center p-8">
      <h3 class="text-2xl font-bold text-gray-800 mb-4">欢迎加入图片托管系统</h3>
      <p class="text-gray-600 text-center max-w-xs mb-8">
        在这里，您可以安全地存储、管理和分享您的图片。
      </p>
      
      <!-- 功能特点 -->
      <div class="grid grid-cols-2 gap-4 w-full">
        <div class="bg-white/80 p-4 rounded-lg shadow-sm hover:shadow transition-all">
          <div class="text-indigo-500 mb-2 flex justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
            </svg>
          </div>
          <h4 class="font-medium text-gray-800 mb-1 text-center">高速上传</h4>
          <p class="text-sm text-gray-600 text-center">批量上传，快速稳定</p>
        </div>
        <div class="bg-white/80 p-4 rounded-lg shadow-sm hover:shadow transition-all">
          <div class="text-indigo-500 mb-2 flex justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-6l-2-2H5a2 2 0 00-2 2z" />
            </svg>
          </div>
          <h4 class="font-medium text-gray-800 mb-1 text-center">智能分类</h4>
          <p class="text-sm text-gray-600 text-center">自动分类，轻松管理</p>
        </div>
        <div class="bg-white/80 p-4 rounded-lg shadow-sm hover:shadow transition-all">
          <div class="text-indigo-500 mb-2 flex justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8.684 13.342C8.886 12.938 9 12.482 9 12c0-.482-.114-.938-.316-1.342m0 2.684a3 3 0 110-2.684m0 2.684l6.632 3.316m-6.632-6l6.632-3.316m0 0a3 3 0 105.367-2.684 3 3 0 00-5.367 2.684zm0 9.316a3 3 0 105.368 2.684 3 3 0 00-5.368-2.684z" />
            </svg>
          </div>
          <h4 class="font-medium text-gray-800 mb-1 text-center">便捷分享</h4>
          <p class="text-sm text-gray-600 text-center">一键分享，多种方式</p>
        </div>
        <div class="bg-white/80 p-4 rounded-lg shadow-sm hover:shadow transition-all">
          <div class="text-indigo-500 mb-2 flex justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
            </svg>
          </div>
          <h4 class="font-medium text-gray-800 mb-1 text-center">安全存储</h4>
          <p class="text-sm text-gray-600 text-center">数据加密，安全可靠</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue';

// 图片数据接口
interface ImageData {
  url: string;
  alt: string;
}

// 模拟图片数据
const images = ref<ImageData[]>([
  { url: '/images/gallery/1.jpg', alt: 'Gallery Image 1' },
  { url: '/images/gallery/2.jpg', alt: 'Gallery Image 2' },
  { url: '/images/gallery/3.jpg', alt: 'Gallery Image 3' },
  { url: '/images/gallery/4.jpg', alt: 'Gallery Image 4' },
  { url: '/images/gallery/5.jpg', alt: 'Gallery Image 5' },
  { url: '/images/gallery/6.jpg', alt: 'Gallery Image 6' },
  { url: '/images/gallery/7.jpg', alt: 'Gallery Image 7' },
  { url: '/images/gallery/8.jpg', alt: 'Gallery Image 8' },
  { url: '/images/gallery/9.jpg', alt: 'Gallery Image 9' },
  { url: '/images/gallery/10.jpg', alt: 'Gallery Image 10' },
  { url: '/images/gallery/11.jpg', alt: 'Gallery Image 11' },
  { url: '/images/gallery/12.jpg', alt: 'Gallery Image 12' },
]);

// 组件挂载后加载图片
// onMounted(() => {
  // TODO: 这里可以添加从服务器获取图片的逻辑
  // 例如：
  // loadGalleryImages();
// });

// 加载图片的函数
// async function loadGalleryImages() {
//   try {
//     const response = await fetch('/api/gallery/random');
//     const data = await response.json();
//     images.value = data;
//   } catch (error) {
//     console.error('Failed to load gallery images:', error);
//   }
// }
</script>

<style scoped>
.h-500 {
  height: 500px;
}

@keyframes scroll {
  0% {
    transform: translateY(0);
  }
  100% {
    transform: translateY(-50%);
  }
}

.animate-scroll-continuous {
  animation: scroll 30s linear infinite;
}
</style>