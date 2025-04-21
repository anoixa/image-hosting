<template>
  <div class="min-h-screen w-full bg-gradient-to-br from-indigo-500/10 to-purple-500/10 pt-16 px-4">
    <div class="container mx-auto py-8">

      <h1 class="text-3xl font-bold text-gray-800 mb-6">我的图片</h1>

      <div class="flex justify-end mb-4">
        <button @click="setLayout('grid')"
          :class="{ 'bg-indigo-500 text-white': currentLayout === 'grid', 'bg-gray-200 text-gray-700': currentLayout !== 'grid' }"
          class="px-4 py-2 rounded-l-md transition-colors duration-200 focus:outline-none">
          网格布局
        </button>
        <button @click="setLayout('list')"
          :class="{ 'bg-indigo-500 text-white': currentLayout === 'list', 'bg-gray-200 text-gray-700': currentLayout !== 'list' }"
          class="px-4 py-2 rounded-r-md transition-colors duration-200 focus:outline-none">
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
              <div class="mt-auto text-xs text-gray-500">
                <span>大小: {{ formatBytes(image.size) }}</span>
                <span class="ml-4">
                  <span class="mr-1">公开:</span>
                  <el-switch
                    v-model="image.isPublic"
                    :loading="image.isToggling"
                    @change="handleTogglePublicStatus(image, $event)"
                    active-text="是"
                    inactive-text="否"
                    :active-value="true"
                    :inactive-value="false"
                  />
                </span>
                <span v-if="image.uploadTime" class="ml-4">上传时间: {{ formatTimestamp(image.uploadTime) }}</span>
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
            <el-table-column label="文件名" prop="fileName" width="200" show-overflow-tooltip></el-table-column>
            <el-table-column label="类型" prop="contentType" show-overflow-tooltip></el-table-column>
            <el-table-column label="描述" prop="description" show-overflow-tooltip></el-table-column>
            <el-table-column label="大小" width="120">
              <template #default="{ row }">
                {{ formatBytes(row.size) }}
              </template>
            </el-table-column>
            <el-table-column label="公开" width="120">
              <template #default="{ row }">
                 <el-switch
                    v-model="row.isPublic"
                    :loading="row.isToggling"
                    @change="handleTogglePublicStatus(row, $event)"
                    active-text="是"
                    inactive-text="否"
                    :active-value="true"
                    :inactive-value="false"
                  />
              </template>
            </el-table-column>
            <el-table-column v-if="imageList.length > 0 && imageList[0].uploadTime !== undefined" label="上传时间" width="180">
                <template #default="{ row }">
                    {{ formatTimestamp(row.uploadTime) }}
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
// === 修改这里的导入，导入你的自定义 service 实例 ===
import service from '@/utils/request';
// ================================================
import { useUserStore } from '@/stores/user';
import { API_BASE_URL } from '@/config';
import { ElMessage } from 'element-plus';

// Import Element Plus components and styles
import { ElTable, ElTableColumn, ElLoading, ElSwitch } from 'element-plus'; // 确保导入 ElSwitch
import 'element-plus/dist/index.css';

// 定义图片接口 (根据你的后端返回结构)
interface Image {
  imageId: string;
  minioUrl: string;
  fileName: string;
  userId: string;
  contentType: string;
  size: number;
  isPublic: boolean;
  description: string | null;
  uploadTime?: string; // 假设这个字段可能存在
  isToggling?: boolean; // 滑块加载状态
}

// State
const imageList = ref<Image[]>([]);
const currentLayout = ref('grid');
const loading = ref(false); // Loading state for the whole table

// Get user store instance
const userStore = useUserStore();

// Fetch data on component mount
onMounted(async () => {
  loading.value = true;

  // 确保用户信息已加载且 userId 可用
  if (userStore.hasUserInfo && userStore.userInfo?.userId) {
    const userId = userStore.userInfo.userId;
    const apiUrl = `${API_BASE_URL}/api/images/minio/url/user/${userId}`;

    try {
      // === 使用 service 实例发送 GET 请求 ===
        // === 现在 await service.get(apiUrl) 将返回完整的 response.data 对象 { code, msg, data } ===
      const responseData = await service.get(apiUrl); // <-- 直接接收 response.data 对象
      // ==================================

      // === 检查响应数据结构和业务码 ===
      // 拦截器确保 responseData 存在且 code 属性存在，并在业务码非 200 时抛错
      // 这里只需要检查业务码是否是 200 以及 data 字段是否符合预期
      if (responseData.code === 200) { // 检查业务码是否是 200
          // 业务码 200 成功，现在检查 data 字段是否存在且是数组
          if (Array.isArray(responseData.data)) {
             // 成功：业务码 200 且 data 是数组
            imageList.value = responseData.data.map((item: any) => ({
              imageId: item.imageId,
              minioUrl: item.minioUrl,
              fileName: item.fileName,
              userId: item.userId,
              contentType: item.contentType,
              description:item.description,
              size: item.size,
              isPublic: item.isPublic,
              uploadTime: responseData.data.uploadTime, // 确保后端返回这个字段
              isToggling: false,
            }));
            console.log('图片列表加载成功', imageList.value); // 添加成功日志
          } else {
             // 后端业务码 200，但 data 字段缺失或不是数组 (格式不正确)
             console.error('获取图片列表失败: 后端数据格式不正确 (data 不是数组)', responseData);
             ElMessage.error(responseData.msg || '获取图片列表失败: 数据格式不正确'); // 使用后端 msg 如果存在
          }
      } else {
          // 这个 else 块理论上不应该被命中，因为拦截器在业务码非 200 时会抛错
          // 但作为安全网，如果命中了，说明可能拦截器处理异常
          console.error('获取图片列表失败: 拦截器返回数据或业务码异常', responseData);
          ElMessage.error(responseData?.msg || '获取图片列表失败: 拦截器返回异常');
      }
    } catch (error: any) { // 捕获 service 拦截器抛出的所有错误 (HTTP 错误, 业务错误, 数据空等)
      console.error('获取图片列表请求失败:', error);
      // 错误消息通常已经在拦截器中弹窗了，这里避免重复
      // ElMessage.error(error.message || '获取图片列表请求失败，请稍后再试。');
    } finally {
      loading.value = false;
    }
  } else {
      // 用户 ID 不可用，无法加载图片列表。路由守卫应该处理了未登录的重定向
      console.warn('用户 ID 不可用，无法加载图片列表。');
      loading.value = false;
  }
});

// Set layout mode
const setLayout = (layout: 'grid' | 'list') => {
  currentLayout.value = layout;
};

// Compute classes for the list container (flex or grid)
const layoutClasses = computed(() => {
  if (currentLayout.value === 'grid') {
    return 'grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3';
  } else {
    return 'flex flex-col';
  }
});

// Format file size
const formatBytes = (bytes: number | undefined, decimals = 2): string => {
  if (bytes === undefined || bytes === null || bytes === 0) return '0 Bytes';
  const k = 1024;
  const dm = decimals < 0 ? 0 : decimals;
  const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
};

// Helper function to format timestamp
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

// === Function to handle toggling public status ===
const handleTogglePublicStatus = async (image: Image, newStatus: boolean) => {
    const originalStatus = !newStatus;
    image.isPublic = newStatus; // 乐观更新 UI
    image.isToggling = true;

    const apiUrl = `${API_BASE_URL}/api/images/switchPublicStatus`;

    try {
        // === 使用 service 实例发送 POST 请求 ===
        // === 拦截器在业务码非 200 时会抛错，成功时返回完整的 response.data ===
        // === 对于 switchPublicStatus，后端可能返回 { code: 200, msg: "操作成功" } ===
        // === 如果是这样，这里的 await service.post 将返回 { code: 200, msg: "操作成功" } ===
        const responseData = await service.post(apiUrl, null, { // 期望返回 { code, msg? } 结构
            params: {
                imageId: image.imageId
            }
        });
        // ======================================

        // === 拦截器没有抛错，并且返回了数据，检查业务码 ===
        // 理论上拦截器已经检查了业务码非 200 并抛错
        // 但这里可以再加一个最终检查，并确认 msg
        if (responseData.code === 200) {
            ElMessage.success(responseData.msg || '状态切换成功'); // 使用后端 msg 如果存在
        } else {
            // 极端情况，拦截器通过但业务码非 200
            console.error('切换状态失败: 拦截器返回业务码异常', responseData);
            ElMessage.error(responseData.msg || '状态切换失败'); // 使用后端 msg 如果存在
            // 回滚 UI 状态
            image.isPublic = originalStatus;
        }

    } catch (error: any) { // 捕获拦截器抛出的错误
        console.error('切换状态请求失败:', error);
        // 错误消息通常已经在拦截器中弹窗了，这里避免重复
        // ElMessage.error(error.message || '切换状态请求失败，请稍后再试。');
        // 回滚 UI 状态
        image.isPublic = originalStatus;
    } finally {
        image.isToggling = false;
    }
};
</script>

<style scoped>
/* Scoped styles if needed, Element Plus provides its own styles */

/* Adjust table cell padding if necessary to match design */
:deep(.el-table .el-table__cell) {
  padding: 12px 0 !important;
}

/* Style for the image in the list view */
.el-table img {
  display: block;
  margin: 0 auto;
}

/* Style for the column headers if needed */
/* :deep(.el-table th.el-table__cell) { ... } */

/* Grid layout styles for list view if used */
/* .list-grid-columns { ... } */

</style>
