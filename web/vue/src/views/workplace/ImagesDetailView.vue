<template>
  <div class="container mx-auto py-8 px-6 pt-20">
    <!-- <h1 class="text-3xl font-bold text-gray-800 mb-6">图片详情</h1> -->
    <div v-if="loading" class="text-center text-gray-500">加载中...</div>
    <div v-else-if="error" class="text-center text-red-500">加载图片详情失败: {{ error.message }}</div>
    <div v-else-if="imageDetail">

      <div class="bg-white rounded-lg shadow-md overflow-hidden p-6 flex flex-col md:flex-row">
        <div class="md:w-1/2 md:pr-6 mb-6 md:mb-0">
          <img :src="'http://' + imageDetail.minioUrl" :alt="imageDetail.fileName"
            class="w-full object-cover rounded-md" />
        </div>
        <div class="md:w-1/2">
          <h2 class="text-2xl font-semibold text-gray-800 mb-4">{{ imageDetail.fileName }}</h2>
          <p class="text-gray-600 mb-4">{{ imageDetail.description || '无描述' }}</p>
          <div class="text-sm text-gray-500">
            <p><strong>图片ID:</strong> {{ imageDetail.imageId }}</p>
            <p><strong>文件类型:</strong> {{ imageDetail.contentType }}</p>
            <p><strong>大小:</strong> {{ formatBytes(imageDetail.size) }}</p>
            <p><strong>上传用户 ID:</strong> {{ imageDetail.userId }}</p>
            <p><strong>是否公开:</strong> {{ imageDetail.isPublic ? '是' : '否' }}</p>
            <p v-if="imageDetail.uploadTime"><strong>上传时间:</strong> {{ formatTimestamp(imageDetail.uploadTime) }}</p>
          </div>
          {/* 可选：添加下载、删除等操作按钮 */}
        </div>
      </div>

      <div class="mt-8 bg-white rounded-lg shadow-md p-6">
        <h3 class="text-xl font-semibold text-gray-800 mb-4">使用链接</h3>

        <div class="mb-6">
          <label class="block text-sm font-medium text-gray-700 mb-2">直链:</label>
          <div class="flex items-center">
            <input type="text" :value="'http://' + imageDetail.minioUrl" readonly
              class="flex-grow border border-gray-300 rounded-md px-3 py-2 text-gray-700 bg-gray-100 text-sm truncate" />
            <button @click="copyToClipboard('http://' + imageDetail.minioUrl)"
              class="ml-2 px-4 py-2 bg-indigo-500 text-white rounded-md hover:bg-indigo-600 focus:outline-none focus:ring focus:ring-indigo-200 text-sm">
              复制
            </button>
          </div>
        </div>

        <div class="mb-6">
          <label class="block text-sm font-medium text-gray-700 mb-2">Markdown:</label>
          <div class="flex items-center">
            <input type="text" :value="`![${imageDetail.fileName || 'image'}](${'http://' + imageDetail.minioUrl})`"
              readonly
              class="flex-grow border border-gray-300 rounded-md px-3 py-2 text-gray-700 bg-gray-100 text-sm truncate" />
            <button
              @click="copyToClipboard(`![${imageDetail.fileName || 'image'}](${'http://' + imageDetail.minioUrl})`)"
              class="ml-2 px-4 py-2 bg-indigo-500 text-white rounded-md hover:bg-indigo-600 focus:outline-none focus:ring focus:ring-indigo-200 text-sm">
              复制
            </button>
          </div>
        </div>

        <div class="mb-6">
          <label class="block text-sm font-medium text-gray-700 mb-2">HTML:</label>
          <div class="flex items-center">
            <input type="text"
              :value="`<img src=&quot;${'http://' + imageDetail.minioUrl}&quot; alt=&quot;${imageDetail.fileName || 'image'}&quot;>`"
              readonly
              class="flex-grow border border-gray-300 rounded-md px-3 py-2 text-gray-700 bg-gray-100 text-sm truncate" />
            <button
              @click="copyToClipboard(`<img src=&quot;${'http://' + imageDetail.minioUrl}&quot; alt=&quot;${imageDetail.fileName || 'image'}&quot;>`)"
              class="ml-2 px-4 py-2 bg-indigo-500 text-white rounded-md hover:bg-indigo-600 focus:outline-none focus:ring focus:ring-indigo-200 text-sm">
              复制
            </button>
          </div>
        </div>

        <div class="mb-6">
          <label class="block text-sm font-medium text-gray-700 mb-2">BBCode:</label>
          <div class="flex items-center">
            <input type="text" :value="`[img]${'http://' + imageDetail.minioUrl}[/img]`" readonly
              class="flex-grow border border-gray-300 rounded-md px-3 py-2 text-gray-700 bg-gray-100 text-sm truncate" />
            <button @click="copyToClipboard(`[img]${'http://' + imageDetail.minioUrl}[/img]`)"
              class="ml-2 px-4 py-2 bg-indigo-500 text-white rounded-md hover:bg-indigo-600 focus:outline-none focus:ring focus:ring-indigo-200 text-sm">
              复制
            </button>
          </div>
        </div>

        <div class="mb-3">
          <label class="block text-sm font-medium text-gray-700 mb-2">CSS 背景图:</label>
          <div class="flex items-center">
            <input type="text" :value="`background-image: url('${'http://' + imageDetail.minioUrl}');`" readonly
              class="flex-grow border border-gray-300 rounded-md px-3 py-2 text-gray-700 bg-gray-100 text-sm truncate" />
            <button @click="copyToClipboard(`background-image: url('${'http://' + imageDetail.minioUrl}');`)"
              class="ml-2 px-4 py-2 bg-indigo-500 text-white rounded-md hover:bg-indigo-600 focus:outline-none focus:ring focus:ring-indigo-200 text-sm">
              复制
            </button>
          </div>
        </div>
      </div>

    </div>

    <div v-else class="text-center text-gray-500">未找到图片详情。</div>

  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { useRoute } from 'vue-router';
import service from '@/utils/request'; // 确保导入你的 service 实例
import { API_BASE_URL } from '@/config';
import { ElMessage } from 'element-plus';
// === 导入你的 Store ===
import { useUserStore } from '@/stores/user'; // Adjust store path as needed
// =====================

// 复用 Image 接口定义 (确保与 Store 中的定义一致)
interface Image {
  imageId: string;
  minioUrl: string;
  fileName: string;
  userId: string;
  contentType: string;
  size: number;
  isPublic: boolean;
  description: string | null;
  uploadTime?: string;
}

const route = useRoute();
const userStore = useUserStore(); // 获取 Store 实例
const imageDetail = ref<Image | null>(null);
const loading = ref(true);
const error = ref<Error | null>(null);

// Helper functions (formatBytes, formatTimestamp) - define locally or import
const formatBytes = (bytes: number | undefined, decimals = 2): string => {
  if (bytes === undefined || bytes === null || bytes === 0) return '0 Bytes';
  const k = 1024;
  const dm = decimals < 0 ? 0 : decimals;
  const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
};

const formatTimestamp = (timestamp: string | undefined): string => {
  if (!timestamp) return '未知时间';
  try {
    const date = new Date(timestamp);
    if (isNaN(date.getTime())) {
      return '无效时间';
    }
    const year = date.getFullYear();
    const month = ('0' + (date.getMonth() + 1)).slice(-2);
    const day = ('0' + date.getDate()).slice(-2);
    const hours = ('0' + date.getHours()).slice(-2);
    const minutes = ('0' + date.getMinutes()).slice(-2);

    return `${year}-${month}-${day} ${hours}:${minutes}`;

  } catch (error) {
    console.error('Error formatting timestamp:', timestamp, error);
    return '格式错误';
  }
};

// === 复制到剪贴板的方法 ===
const copyToClipboard = async (text: string) => {
  try {
    await navigator.clipboard.writeText(text);
    ElMessage.success('已复制到剪贴板');
  } catch (err) {
    console.error('复制失败:', err);
    ElMessage.error('复制失败，请手动选择复制');
  }
};
// ==========================


// Function to fetch image details (from Store or API)
const fetchImageDetail = async (imageId: string) => {
  loading.value = true;
  error.value = null;
  imageDetail.value = null;

  // === 1. 优先尝试从 Store 中查找 ===
  // 调用 Store 的 getter 查找图片。确保你的 Store 中实现了 findImageById getter
  const storedImage = userStore.findImageById(imageId);

  if (storedImage) {
    console.log('图片详情从 Store 加载:', storedImage);
    imageDetail.value = storedImage;
    loading.value = false;
  } else {
    // === 2. Store 中未找到，从 API 获取 (兜底) ===
    console.log('图片详情 Store 中未找到，从 API 加载:', imageId);
    // 假设你有一个后端接口来获取单个图片的详情，例如 GET /api/images/:imageId
    const apiUrl = `${API_BASE_URL}/api/images/${imageId}`; // 调整为你实际的接口 URL

    try {
      // service 实例的拦截器应该已经处理了 HTTP 错误和业务码非 200 的情况
      const responseData = await service.get(apiUrl); // 期望返回 { code, msg, data: Image }

      if (responseData.code === 200 && responseData.data) {
        imageDetail.value = responseData.data as Image; // 假设 responseData.data 就是 Image 对象
        console.log('图片详情从 API 加载成功:', imageDetail.value);
      } else {
        // 后端返回 200 但数据异常 (data 缺失或格式不对)
        console.error('获取图片详情失败: 后端返回数据异常', responseData);
        error.value = new Error(responseData.msg || '获取图片详情失败: 服务器返回数据异常');
        ElMessage.error(error.value.message);
      }
    } catch (err: any) { // 捕获网络错误、HTTP 错误、或拦截器抛出的业务错误
      console.error('获取图片详情请求失败:', err);
      error.value = err; // 存储错误对象
      // 错误消息通常已经在拦截器中弹窗了，这里避免重复弹窗
      // ElMessage.error(err.message || '获取图片详情请求失败');
    } finally {
      loading.value = false;
    }
  }
};

// 在组件加载时或路由参数 imageId 变化时获取数据
watch(() => route.params.imageId, (newImageId) => {
  if (typeof newImageId === 'string' && newImageId) {
    fetchImageDetail(newImageId);
  } else {
    // 路由中缺少 imageId
    imageDetail.value = null;
    error.value = new Error('缺少图片 ID');
    loading.value = false;
    ElMessage.error(error.value.message);
  }
}, { immediate: true }); // immediate: true 确保在组件初次挂载时也执行一次


// 可选：在组件卸载前清除 Store 中的选中图片状态 (如果你希望离开详情页就清除)
// import { onUnmounted } from 'vue';
// onUnmounted(() => {
//   userStore.setSelectedImage(null); // 调用 Store 的方法清除状态
// });

</script>

<style scoped>
/* 你可以在这里添加组件的 scoped 样式 */

/* Element Plus 组件的深度选择器样式如果需要 */
/* :deep(.some-el-plus-class) {
    /* style *\/
  } */
</style>
