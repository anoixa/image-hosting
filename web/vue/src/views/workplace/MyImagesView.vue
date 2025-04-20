<template>
  <div class="min-h-screen w-full bg-gradient-to-br from-indigo-500/10 to-purple-500/10 pt-16 px-4">
    <div class="container mx-auto py-8">

      <h1 class="text-3xl font-bold text-gray-800 mb-6">我的图片</h1>

      <div class="flex justify-end mb-4">
        <button @click="setLayout('grid')"
          :class="{ 'bg-indigo-500 text-white': currentLayout === 'grid', 'bg-gray-200 text-gray-700': currentLayout !== 'grid' }"
          class="px-4 py-2 rounded-l-md transition-colors duration-200">
          网格布局
        </button>
        <button @click="setLayout('list')"
          :class="{ 'bg-indigo-500 text-white': currentLayout === 'list', 'bg-gray-200 text-gray-700': currentLayout !== 'list' }"
          class="px-4 py-2 rounded-r-md transition-colors duration-200">
          列表布局
        </button>
      </div>

      <div class="gap-6" :class="{ 'grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3': currentLayout === 'grid' }">

        <template v-if="currentLayout === 'grid'">
          <div v-for="image in imageList" :key="image.imageId"
            class="bg-white rounded-lg shadow-md overflow-hidden transition-transform hover:scale-[1.02] duration-200 flex flex-col">
            <img :src="'http://' + image.minioUrl" :alt="image.fileName" class="w-full object-cover h-48" />
            <div class="p-4 flex-grow flex flex-col">
              <div class="font-semibold text-gray-800 mb-2">{{ image.fileName }}</div>
              <p class="text-sm text-gray-600 truncate mb-2">{{ image.description || '无描述' }}</p>
              <div class="mt-auto text-xs text-gray-500"> <span>大小: {{ formatBytes(image.size) }}</span>
                <span class="ml-4">公开: {{ image.isPublic ? '是' : '否' }}</span>
              </div>
            </div>
          </div>
        </template>

        <template v-else-if="currentLayout === 'list'">
          <el-table :data="imageList" style="width: 100%" class="shadow-md rounded-lg overflow-hidden"
            v-loading="loading" element-loading-text="加载中..." empty-text="暂无图片数据。">
            <el-table-column label="图片" width="100">
              <template #default="{ row }">
                <img :src="'http://' + row.minioUrl" :alt="row.fileName" class="h-16 w-16 object-cover rounded-md" />
              </template>
            </el-table-column>
            <el-table-column label="文件名" prop="fileName" width="180"></el-table-column>
            <el-table-column label="描述" prop="description" show-overflow-tooltip></el-table-column>
            <el-table-column label="大小" width="120">
              <template #default="{ row }">
                {{ formatBytes(row.size) }}
              </template>
            </el-table-column>
            <el-table-column label="公开" width="80">
              <template #default="{ row }">
                {{ row.isPublic ? '是' : '否' }}
              </template>
            </el-table-column>
          </el-table>
        </template>

      </div>

      <div v-if="currentLayout === 'grid' && imageList.length === 0 && !loading" class="text-center text-gray-500 mt-8">
        暂无图片数据。
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import axios from 'axios';
import { useUserStore } from '@/stores/user';
import { API_BASE_URL } from '@/config';

// Import Element Plus components
import { ElTable, ElTableColumn, ElLoading } from 'element-plus';
import 'element-plus/dist/index.css'; // Import Element Plus styles

// Optional: Register Element Plus loading directive globally if desired
// You might need to do this in main.ts or locally here if preferred
// app.use(ElLoading); // If you uncomment this, do it in your main.ts

// State
const imageList = ref<any[]>([]); // Use a more specific type if possible (interface for image object)
const currentLayout = ref('grid'); // Default layout
const loading = ref(false); // Loading state

// Get user store instance
const userStore = useUserStore();

// Fetch data on component mount
onMounted(async () => {
  loading.value = true; // Set loading to true

  // *** Read user data from Pinia store first ***
  const userId = userStore.userInfo?.userId;

  if (userStore.isLoggedIn && userId) {
    const userInfoLoadSuccess = await userStore.loadUserInfo();

    if (userInfoLoadSuccess && userStore.isLoggedIn && userStore.userInfo?.userId) {
      const userId = userStore.userInfo.userId;
      // 构建 API URL
      // 假设你的 API_BASE_URL 是 "http://localhost:8080"
      const apiUrl = `${API_BASE_URL}/api/images/minio/url/user/${userId}`;

      try {
        // 发送 GET 请求获取图片列表数据 (假设你已经配置了 Axios 拦截器来自动添加 Token)
        const response = await axios.get(apiUrl);

        // 检查响应状态码和业务码
        if (response.data.code === 200 && response.data.data) {
          imageList.value = response.data.data;
        } else {
          // 处理业务错误
          console.error('获取图片列表失败:', response.data.msg);
          // 可以在这里显示一个用户友好的错误提示
          // ElMessage.error(response.data.msg || '获取图片列表失败');
        }
      } catch (error) {
        // 处理网络或请求错误
        console.error('获取图片列表请求失败:', error);
        // 可以在这里显示一个用户友好的错误提示
        // ElMessage.error('获取图片列表请求失败，请稍后再试。');

        // 如果是 401 错误，并且 loadUserInfo 或全局路由守卫没有处理未登录跳转，你可以在这里处理
        // if (axios.isAxiosError(error) && error.response?.status === 401) {
        //     router.push('/auth/login'); // 需要导入 router
        // }
      } finally {
        loading.value = false; // Set loading to false regardless of success or failure
      }
    } else {
      // 如果 userInfoLoadSuccess 为 false (loadUserInfo 失败) 或最终判断用户未登录
      console.warn('用户信息加载失败或未登录，无法加载图片列表。');
      // 如果 loadUserInfo 或全局路由守卫没有处理未登录跳转，你可以在这里跳转
      // router.push('/auth/login'); // 需要导入 router
    }
  }
});

// Set layout mode
const setLayout = (layout: 'grid' | 'list') => {
  currentLayout.value = layout;
};

// Format file size
const formatBytes = (bytes: number | undefined, decimals = 2): string => {
  if (bytes === undefined || bytes === null || bytes === 0) return '0 Bytes';
  const k = 1024;
  const dm = decimals < 0 ? 0 : decimals;
  const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
};
</script>

<style scoped>
/* Scoped styles if needed, Element Plus provides its own styles */

/* Adjust table cell padding if necessary to match design */
:deep(.el-table .el-table__cell) {
  padding: 12px 0;
  /* Adjust padding as needed */
}

/* Style for the image in the list view */
.el-table img {
  display: block;
  /* Helps with alignment in table cell */
  margin: 0 auto;
  /* Center the image */
}
</style>
