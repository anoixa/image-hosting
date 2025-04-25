<template>
  <div class="container mx-auto py-8 px-6 pt-24">
    <h1 class="text-3xl font-bold text-gray-800 mb-6">上传图片</h1>

    <div class="bg-white rounded-lg shadow-md p-6">
      <div class="mb-6">
        <label class="block text-sm font-medium text-gray-700 mb-2">选择文件:</label>
        <el-upload
          ref="uploadRef"
          multiple
          :auto-upload="false"
          :show-file-list="false"
          :on-change="handleFileChange"
          :on-remove="handleRemoveFileFromUpload"
          :file-list="selectedFiles"
          drag
        >
          <div class="el-upload__text">
            将文件拖放到此处，或 <em>点击选择文件</em>
          </div>
           <template #tip>
             <div class="el-upload__tip">支持批量上传</div>
           </template>
        </el-upload>

        <div v-if="selectedFiles.length > 0" class="mt-4">
          <p class="text-sm font-medium text-gray-700 mb-2">已选文件:</p>
          <ul>
            <!-- 使用 file.uid 作为 key，ElUpload 提供 -->
            <li v-for="(file, index) in selectedFiles" :key="file.uid"
                class="flex items-center justify-between text-sm text-gray-700 bg-gray-100 rounded-md px-3 py-2 mb-2">
              <span class="truncate mr-2">{{ file.name }}</span>
              <button @click="handleRemoveFile(file, index)"
                      class="text-red-500 hover:text-red-700 focus:outline-none">
                <el-icon><Close /></el-icon>
              </button>
            </li>
          </ul>
        </div>
      </div>

      <div class="mb-6">
        <label class="block text-sm font-medium text-gray-700 mb-2" for="description">描述 (可选):</label>
        <el-input
          id="description"
          v-model="description"
          placeholder="为图片添加描述"
          type="textarea"
          :rows="3"
        />
      </div>

      <div class="mb-6 flex items-center">
        <label class="block text-sm font-medium text-gray-700 mr-3">是否公开:</label>
        <el-switch v-model="isPublic" active-text="公开" inactive-text="私有" />
      </div>

      <div class="mb-6">
        <el-button
          type="primary"
          size="large"
          @click="uploadBatch"
          :disabled="selectedFiles.length === 0 || isUploading"
          :loading="isUploading"
        >
          {{ isUploading ? '上传中...' : `上传 (${selectedFiles.length} 个文件)` }}
        </el-button>
      </div>

      <div v-if="uploadResult" class="mt-6 p-4 rounded-md"
           :class="{ 'bg-green-100 border border-green-400 text-green-700': uploadResult.code === 200 && !uploadResult.failedFiles?.length,
                     'bg-yellow-100 border border-yellow-400 text-yellow-700': uploadResult.code === 200 && uploadResult.failedFiles?.length > 0,
                     'bg-red-100 border border-red-400 text-red-700': uploadResult.code !== 200 }">
        <p class="font-semibold mb-2">{{ uploadResult.msg || (uploadResult.code === 200 ? '上传完成' : '上传失败') }}</p>

        <div v-if="uploadResult.code === 200 && uploadResult.data?.length > 0">
          <p class="text-sm font-medium">成功上传:</p>
          <ul class="text-sm mt-1">
            <li v-for="img in uploadResult.data" :key="img.imageId">
              {{ img.fileName }} (ID: {{ img.imageId }})
            </li>
          </ul>
        </div>

        <div v-if="uploadResult.failedFiles?.length > 0">
          <p class="text-sm font-medium mt-2">上传失败:</p>
          <ul class="text-sm mt-1">
            <li v-for="fileName in uploadResult.failedFiles" :key="fileName">
              {{ fileName }}
            </li>
          </ul>
        </div>
      </div>

       <div v-else-if="uploadError" class="mt-6 p-4 bg-red-100 border border-red-400 text-red-700 rounded-md">
          <p class="font-semibold mb-2">上传请求失败:</p>
           <p class="text-sm">{{ uploadError.message || '未知错误' }}</p>
       </div>

    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElUpload, ElInput, ElSwitch, ElButton, ElIcon } from 'element-plus'; // 导入 Element Plus 组件
import { Close } from '@element-plus/icons-vue';

import service from '@/utils/request';
import { API_BASE_URL } from '@/config';

// === Reactive State ===
const selectedFiles = ref<any[]>([]); // 用于存储用户选择的文件列表 (ElUpload 文件对象)
const description = ref(''); // 用于存储描述
const isPublic = ref(true); // 用于存储公开状态
const isUploading = ref(false); // 新增：上传操作的加载状态
const uploadResult = ref<any>(null); // 新增：存储后端返回的成功响应数据 (AjaxResult)
const uploadError = ref<any>(null); // 新增：存储请求失败的 Error 对象

// === Refs for Component Instances ===
const uploadRef = ref<InstanceType<typeof ElUpload> | null>(null); // 用于访问 ElUpload 组件方法
// ====================================

// 处理文件选择改变 (通过 ElUpload 的 @on-change 触发)
// file: 当前操作的文件 (添加/移除), fileList: 当前的文件列表
const handleFileChange = (file: any, fileList: any[]) => {
  // 直接使用 ElUpload 维护的 fileList 作为我们的已选文件列表
  selectedFiles.value = fileList;

  // 当选择新文件时，清空之前的上传结果和错误信息
  uploadResult.value = null;
  uploadError.value = null;
};

// 处理从手动展示的列表中移除文件 (通过点击自定义移除按钮触发)
const handleRemoveFile = (file: any, index: number) => {
  // 调用 ElUpload 实例的 handleRemove 方法来移除文件
  // 这会触发 ElUpload 内部的 on-remove 钩子 (如果配置了 handleRemoveFileFromUpload)
  // 并且更新 ElUpload 内部维护的 fileList
  if (uploadRef.value) {
    uploadRef.value.handleRemove(file);
  }

  // 从我们的 selectedFiles 数组中移除文件 (handleRemove 也会更新 ElUpload 的 fileList，
  // on-change -> handleFileChange 会更新 selectedFiles，所以这行可能不是严格必需，
  // 但作为双重保障或如果 on-change 逻辑不同时有用)
  selectedFiles.value.splice(index, 1);

  // 如果文件全部移除，清空结果展示
  if (selectedFiles.value.length === 0) {
      uploadResult.value = null;
      uploadError.value = null;
  }
};

// ElUpload 内部文件移除时的钩子 (当调用 handleRemove 或点击默认列表的移除按钮时触发)
// 我们隐藏了默认列表，所以主要由 handleRemoveFile 触发 ElUpload.handleRemove 进而触发这里
const handleRemoveFileFromUpload = (file: any, fileList: any[]) => {
    console.log('文件从 ElUpload 内部列表移除:', file.name);
    // selectedFiles.value 在 handleFileChange 和 handleRemoveFile 中管理
};


// 上传按钮点击处理函数
const uploadBatch = async () => {
  if (selectedFiles.value.length === 0) {
    ElMessage.warning('请先选择文件');
    return;
  }

  isUploading.value = true; // 开始上传，激活加载状态
  uploadResult.value = null; // 清空之前的上传结果
  uploadError.value = null; // 清空之前的错误信息

  const formData = new FormData();

  // 将所有选择的文件添加到 FormData 中
  // selectedFiles 数组中的项是 ElUpload 提供的 File 对象，它们有一个 .raw 属性指向原始 File 对象
  selectedFiles.value.forEach((fileItem: any) => {
    formData.append('files', fileItem.raw); // 使用 .raw 属性获取原始文件对象
  });

  // 将元数据添加到 FormData 中
  formData.append('description', description.value);
  // FormData 最好使用字符串类型，将布尔值转换为字符串 "true" 或 "false"
  formData.append('isPublic', String(isPublic.value));

  try {
    // 使用你的 service 实例发起 POST 请求到批量上传接口
    // service 拦截器应该会自动添加 Header
    const responseData = await service.post(`${API_BASE_URL}/api/images/batch-upload`, formData, {
       // 如果你的批量上传成功响应格式特殊，需要跳过拦截器处理，可以在这里添加标记：
       // skipResponseInterceptor: true,
    });

    // service 拦截器通常会处理业务成功 (code 200) 和失败 (非 200) 的消息弹窗
    // 在这里，我们将整个后端返回的数据存储到 uploadResult 中，用于详细展示结果
    uploadResult.value = responseData; // 存储后端返回的 AjaxResult 对象

    if (responseData.code === 200) {
      // --- 处理业务成功 (所有或部分成功) ---
      console.log('批量上传成功响应:', responseData);
      // 清空已选文件列表并重置 ElUpload 组件，以便进行新的上传
      selectedFiles.value = []; // 清空我们的数组
      if (uploadRef.value) {
         uploadRef.value.clearFiles(); // 清空 ElUpload 内部的文件列表和状态
      }
      // 可选：上传成功后清空描述和公开状态
      // description.value = '';
      // isPublic.value = true;
      // 成功消息已由拦截器处理，或根据 uploadResult 显示
      // --------------------------------------------
    } else {
      // --- 处理业务逻辑失败 (例如后端验证未通过) ---
      console.error('批量上传业务失败响应:', responseData);
      // 错误消息已由拦截器处理，或根据 uploadResult 显示
      // 文件列表通常保留，让用户可以修改后重试
    }

  } catch (error: any) {
    // --- 处理请求失败 (网络错误，HTTP 状态码非 2xx) ---
    console.error('批量上传请求失败:', error);
    uploadError.value = error; // 存储错误对象，用于在 UI 中展示
    // 错误消息已由拦截器处理，或根据 uploadError 显示
    // 文件列表通常保留
  } finally {
    isUploading.value = false; // 上传结束，停止加载状态
  }
};

// === Lifecycle Hooks ===
// 可选：在组件挂载时清空可能残留的结果或状态
onMounted(() => {
  // 确保 ElUpload 初始状态是空的
   if (uploadRef.value) {
     uploadRef.value.clearFiles();
   }
   selectedFiles.value = []; // 确保我们的数组也是空的
   uploadResult.value = null;
   uploadError.value = null;
   isUploading.value = false;
});

// ========================

</script>

<style lang="scss" scoped>
/* 你可以在这里添加组件的 scoped 样式 */

/* 可以通过深度选择器修改 Element Plus 内部组件样式 */
/* 修改 ElUpload 拖拽区域的样式 */
:deep(.el-upload-dragger) {
  padding: 20px;
  height: auto; /* 根据内容自适应高度 */
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

/* 修改 ElUpload 提示文字样式 */
:deep(.el-upload__text) {
  font-size: 16px;
}

:deep(.el-upload__tip) {
  margin-top: 10px;
  font-size: 14px;
  color: #606266;
}

/* 修改 ElInput 文本域的样式 */
:deep(.el-textarea__inner) {
    box-shadow: none !important; /* 移除 Element Plus 默认的阴影或边框样式 */
    /* 根据需要添加自定义样式 */
}

/* 修改 ElSwitch 的样式 */
:deep(.el-switch__label) {
    margin: 0 5px; /* 调整文字与开关的间距 */
}

</style>
