<template>
  <div>

  </div>
</template>

<script lang="ts" setup>

const selectedFiles = ref([]); // 用于存储用户选择的文件列表
const description = ref(''); // 用于存储描述
const isPublic = ref(true); // 用于存储公开状态

// 当文件输入框选择文件后调用
const handleFileChange = (event) => {
  selectedFiles.value = Array.from(event.target.files); // 将 FileList 转换为数组
};

// 上传按钮点击事件
const uploadBatch = async () => {
  if (selectedFiles.value.length === 0) {
    ElMessage.warning('请先选择文件');
    return;
  }

  const formData = new FormData();

  // 将所有选择的文件添加到 FormData 中，字段名为 'files'
  selectedFiles.value.forEach(file => {
    formData.append('files', file);
  });

  // 将元数据添加到 FormData 中，字段名需要与后端 ImageData 的属性名对应
  formData.append('description', description.value);
  formData.append('isPublic', isPublic.value); // 注意布尔值在 FormData 中的处理

  // TODO: 添加加载状态控制

  try {
    // 使用你的 service 实例发起 POST 请求
    // service 拦截器会自动添加 Header
    const responseData = await service.post(`${API_BASE_URL}/api/images/batch-upload`, formData, {
      // 对于文件上传，通常不需要设置 Content-Type，浏览器会自动设置 multipart/form-data
      // 但如果需要跳过拦截器处理响应，可以在这里添加标记：
      // skipResponseInterceptor: true, // 如果批量上传的响应格式也特殊
    });

    // service 拦截器应该会处理 code 200 和非 200 的响应并弹窗
    // 如果需要更详细的成功处理（例如处理部分上传成功的情况），可以在这里根据 responseData 进一步判断
    if (responseData.code === 200) {
        console.log('批量上传结果:', responseData.data); // responseData.data 应该是一个 Image 对象的数组
        // TODO: 清空文件列表，刷新图片列表等 UI 操作
        selectedFiles.value = [];
        // 提示成功消息已由拦截器处理
    } else {
        // 业务错误消息已由拦截器处理
    }


  } catch (error) {
    console.error('批量上传请求失败:', error);
    // 请求失败的错误通常由 service 拦截器处理并弹窗
  } finally {
    // TODO: 停止加载状态
  }
};
</script>

<style lang="scss" scoped>

</style>
