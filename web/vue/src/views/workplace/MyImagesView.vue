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
            class="bg-white rounded-lg shadow-md overflow-hidden transition-transform hover:scale-[1.02] duration-200 flex flex-col cursor-pointer"
            @click="goToImageDetail(image)">
            <img :src="'http://' + image.minioUrl" :alt="image.fileName" class="w-full object-cover h-48" />
            <div class="p-4 flex-grow flex flex-col">
              <div class="font-semibold text-gray-800 mb-2">{{ image.fileName }}</div>
              <p class="text-sm text-gray-600 truncate mb-2">{{ image.description || '无描述' }}</p>
              <div class="mt-auto text-xs text-gray-500">
                <div class="flex justify-between items-center mt-2">
                  <div>
                    <span>大小: {{ formatBytes(image.size) }}</span>
                    <span class="ml-4">
                      <span class="mr-1">公开:</span>
                      <el-switch v-model="image.isPublic" :loading="image.isToggling"
                        @change="handleTogglePublicStatus(image, $event)" active-text="是" inactive-text="否"
                        :active-value="true" :inactive-value="false" />
                    </span>
                  </div>
                  <button @click.stop="downloadImage(image)"
                    class="ml-4 px-3 py-1 bg-green-500 text-white rounded-md hover:bg-green-600 focus:outline-none focus:ring focus:ring-green-200 text-xs">
                    下载
                  </button>
                  <button @click.stop="deleteImage(image)" :disabled="image.isDeleting"
                    class="px-3 py-1 bg-red-600 text-white rounded-md hover:bg-red-700 focus:outline-none focus:ring focus:ring-red-300 text-xs disabled:opacity-50 disabled:cursor-not-allowed">
                    {{ image.isDeleting ? '删除中...' : '删除' }}
                  </button>
                </div>
                <span v-if="image.uploadTime" class="ml-4">上传时间: {{ formatTimestamp(image.uploadTime) }}</span>
              </div>
            </div>
          </div>
        </template>

        <template v-else-if="currentLayout === 'list'">
          <el-table :data="imageList" style="width: 100%" class="shadow-md rounded-lg overflow-hidden"
            v-loading="loading" element-loading-text="加载中..." empty-text="暂无图片数据。" @row-click="goToImageDetail">
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
                <el-switch v-model="row.isPublic" :loading="row.isToggling"
                  @change="handleTogglePublicStatus(row, $event)" active-text="是" inactive-text="否" :active-value="true"
                  :inactive-value="false" />
              </template>
            </el-table-column>
            <el-table-column v-if="imageList.length > 0 && imageList[0].uploadTime !== undefined" label="上传时间"
              width="180">
              <template #default="{ row }">
                {{ formatTimestamp(row.uploadTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <button @click.stop="downloadImage(row)"
                  class="px-3 py-1 bg-green-500 text-white rounded-md hover:bg-green-600 focus:outline-none focus:ring focus:ring-green-200 text-xs">
                  下载
                </button>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <button @click.stop="deleteImage(row)" :disabled="row.isDeleting"
                  class="px-3 py-1 bg-red-600 text-white rounded-md hover:bg-red-700 focus:outline-none focus:ring focus:ring-red-300 text-xs disabled:opacity-50 disabled:cursor-not-allowed">
                  {{ row.isDeleting ? '删除中...' : '删除' }}
                </button>
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
import service from '@/utils/request';
import { useUserStore } from '@/stores/user';
import { API_BASE_URL } from '@/config';
import { ElMessage } from 'element-plus';
import { ElMessageBox } from 'element-plus';
// === 导入 useRouter ===
import { useRouter } from 'vue-router';
// ====================

// Import Element Plus components and styles
import { ElTable, ElTableColumn, ElLoading, ElSwitch } from 'element-plus';
import 'element-plus/dist/index.css';

// 定义图片接口 (确保与后端返回的每个图片对象结构一致)
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
  isToggling?: boolean;
  isDeleting?: boolean;
}

// State
const imageList = ref<Image[]>([]);
const currentLayout = ref('grid');
const loading = ref(false);

// Get user store instance
const userStore = useUserStore();
// === 获取 router 实例 ===
const router = useRouter();
// ======================

// Fetch data on component mount
onMounted(async () => {
  loading.value = true;

  if (userStore.hasUserInfo && userStore.userInfo?.userId) {
    const userId = userStore.userInfo.userId;
    const apiUrl = `${API_BASE_URL}/api/images/minio/url/user/${userId}`;

    try {
      // await service.get 将返回完整的 response.data 对象 { code, msg, data }
      const responseData = await service.get(apiUrl);

      // 检查响应数据结构和业务码
      if (responseData.code === 200) {
        // 业务码 200 成功，现在检查 data 字段是否存在且是数组
        if (Array.isArray(responseData.data)) {
          // 成功：业务码 200 且 data 是数组
          imageList.value = responseData.data.map((item: any) => ({
            imageId: item.imageId,
            minioUrl: item.minioUrl,
            fileName: item.fileName,
            userId: item.userId,
            contentType: item.contentType,
            description: item.description,
            size: item.size,
            isPublic: item.isPublic,
            uploadTime: item.uploadTime, // 确保这里正确映射了 uploadTime
            isToggling: false,
            isDeleting: false,
          }));
          console.log('图片列表加载成功', imageList.value);
        } else {
          console.error('获取图片列表失败: 后端数据格式不正确 (data 不是数组)', responseData);
          ElMessage.error(responseData.msg || '获取图片列表失败: 数据格式不正确');
        }
      } else {
        console.error('获取图片列表失败: 拦截器返回数据或业务码异常', responseData);
        ElMessage.error(responseData?.msg || '获取图片列表失败: 拦截器返回异常');
      }
    } catch (error: any) {
      console.error('获取图片列表请求失败:', error);
      // ElMessage.error(error.message || '获取图片列表请求失败，请稍后再试。');
    } finally {
      loading.value = false;
    }
  } else {
    console.warn('用户 ID 不可用，无法加载图片列表。');
    loading.value = false;
  }
});

// === 添加图片详情页跳转方法 ===
const goToImageDetail = (image: Image) => {
  if (image && image.imageId) {
    // === 在跳转前，将完整的图片对象保存到 Store 中 ===
    userStore.setSelectedImage(image); // 调用 Store 的方法
    router.push({ name: 'ImageDetail', params: { imageId: image.imageId } });
    // 假设你的路由中有一个名为 'ImageDetail' 的路由，路径类似 '/image/:imageId'
  } else {
    console.error('无法跳转到图片详情，缺少 imageId');
    ElMessage.warning('图片信息不完整，无法查看详情');
  }
};

const setLayout = (layout: 'grid' | 'list') => {
  currentLayout.value = layout;
};

const layoutClasses = computed(() => {
  if (currentLayout.value === 'grid') {
    return 'grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3';
  } else {
    return 'flex flex-col';
  }
});

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

const handleTogglePublicStatus = async (image: Image, newStatus: boolean) => {
  const originalStatus = !newStatus;
  image.isPublic = newStatus; // 乐观更新 UI
  image.isToggling = true;

  const apiUrl = `${API_BASE_URL}/api/images/switchPublicStatus`;

  try {
    const responseData = await service.post(apiUrl, null, {
      params: {
        imageId: image.imageId
      }
    });

    if (responseData.code === 200) {
      ElMessage.success(responseData.msg || '状态切换成功');
    } else {
      console.error('切换状态失败: 拦截器返回业务码异常', responseData);
      ElMessage.error(responseData.msg || '状态切换失败');
      image.isPublic = originalStatus;
    }

  } catch (error: any) {
    console.error('切换状态请求失败:', error);
    // ElMessage.error(error.message || '切换状态请求失败，请稍后再试。');
    image.isPublic = originalStatus;
  } finally {
    image.isToggling = false;
  }
};

const downloadImage = (image: Image) => {
  if (!image || !image.imageId || !image.fileName) {
    ElMessage.warning('图片信息不完整，无法下载。');
    return;
  }

  // 构建直接下载的 URL，使用 imageId 作为路径变量
  // 根据后端 @GetMapping("/minio/{imageId}") 的定义
  const downloadUrl = `${API_BASE_URL}/api/images/minio/${image.imageId}`;

  // 创建一个临时的 <a> 元素
  const link = document.createElement('a');
  link.href = downloadUrl;
  // 设置 download 属性，建议浏览器下载文件，并提供一个默认文件名。
  // 最终文件名通常由后端返回的 Content-Disposition Header 决定。
  link.setAttribute('download', image.fileName);

  // 触发下载
  // 将 link 元素添加到文档体中，并模拟点击。
  // 在某些浏览器（如 Firefox）中，添加到 body 是必需的。
  document.body.appendChild(link);
  link.click();

  // 清理：移除临时创建的 <a> 元素
  document.body.removeChild(link);
};

// === 添加 deleteImage 方法 ===
const deleteImage = async (image: Image) => {
  if (!image || !image.imageId) {
    ElMessage.warning('图片信息不完整，无法删除。');
    return;
  }

  try {
    // 1. 显示确认对话框
    await ElMessageBox.confirm(`确定要删除图片 "${image.fileName}" 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    });

    // 用户确认删除，开始发起请求
    // 找到列表中对应的图片对象，激活其删除加载状态
    const imageToDelete = imageList.value.find(img => img.imageId === image.imageId);
    if (imageToDelete) {
        imageToDelete.isDeleting = true;
    }


    const deleteUrl = `${API_BASE_URL}/api/images/deleteById/${image.imageId}`;

    // 2. 发送 POST 请求到后端删除接口
    // 后端是 @PostMapping("/deleteById/{imageId}")
    const responseData = await service.post(deleteUrl); // 假设 service 拦截器处理了非 200 的错误提示

    // 3. 处理成功响应 (业务码 200)
    if (responseData.code === 200) {
      ElMessage.success(responseData.msg || '删除成功'); // 显示成功消息

      // 4. 删除成功后，从 imageList 中移除该图片，更新 UI
      const index = imageList.value.findIndex(img => img.imageId === image.imageId);
      if (index !== -1) {
        imageList.value.splice(index, 1);
      }

    } else {
       // 如果 service 拦截器没有处理业务码非 200 的情况，可以在这里添加弹窗
       console.error('图片删除业务失败:', responseData.msg);
       // ElMessage.error(responseData.msg || '删除失败');
    }

  } catch (error: any) {
    // 捕获用户点击取消或 API 请求失败的错误
    if (error !== 'cancel') { // 如果错误不是用户取消 (ElMessageBox.confirm 会抛出 'cancel')
       console.error('图片删除请求或确认框错误:', error);
       // API 请求失败的错误通常由 service 拦截器处理并弹窗
       // ElMessage.error(error.message || '删除操作失败');
    } else {
       console.log('删除操作已取消');
    }
  } finally {
    // 无论成功或失败，停止加载状态
    const imageToDelete = imageList.value.find(img => img.imageId === image.imageId);
    if (imageToDelete) {
        imageToDelete.isDeleting = false;
    }
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
