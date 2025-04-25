<template>
  <div class="w-full bg-white bg-opacity-70 backdrop-blur-sm shadow-md py-3 px-6 flex justify-between items-center">
    <!-- 左侧应用标题 -->
    <div class="flex items-center">
      <div class="text-xl font-bold text-indigo-700">image-hosting</div>
    </div>

    <!-- 右侧用户信息和下拉菜单 -->
    <div class="relative" ref="userDropdownRef">
      <div class="flex items-center gap-2 cursor-pointer" @click="toggleDropdown">
        <!-- 用户头像 - 使用首字母作为默认头像 -->
        <div class="h-10 w-10 rounded-full bg-indigo-500 text-white flex items-center justify-center text-lg font-medium overflow-hidden">
          {{ userStore.userInfo?.userName ? userStore.userInfo.userName.charAt(0).toUpperCase() : '?' }}
        </div>

        <!-- 用户名和下拉图标 -->
        <div class="flex items-center gap-1">
          <span class="font-medium text-gray-800">{{ userStore.userInfo?.userName }}</span>
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none"
            stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
            class="h-4 w-4 text-gray-500" :class="{'transform rotate-180': isDropdownOpen}">
            <path d="m6 9 6 6 6-6"/>
          </svg>
        </div>
      </div>

      <!-- 下拉菜单 -->
      <div v-if="isDropdownOpen"
        class="absolute right-0 mt-2 w-64 bg-white rounded-lg shadow-xl border border-gray-100 z-10
        transition-all duration-200 ease-in-out transform origin-top">
        <!-- 用户信息区域 -->
        <div class="p-4 border-b border-gray-100">
          <div class="font-semibold text-indigo-600 mb-1">{{ userStore.userInfo?.userName }}</div>
          <div class="text-sm text-gray-500 mb-1">{{ userStore.userInfo?.userEmail }}</div>
          <div class="text-xs text-gray-400">ID: {{ userStore.userInfo?.userId }}</div>
        </div>

        <!-- 按钮区域 -->
        <div class="p-3">
          <button @click="handleLogout"
            class="flex items-center w-full gap-2 px-3 py-2 text-left text-sm text-red-600 hover:bg-red-50 rounded-md transition-colors duration-200">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none"
              stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="h-4 w-4">
              <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
              <polyline points="16 17 21 12 16 7"/>
              <line x1="21" y1="12" x2="9" y2="12"/>
            </svg>
            退出登录
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';
import { logout } from '@/api/auth/login';
import { ElMessage } from 'element-plus';

const router = useRouter();
const userStore = useUserStore();
const isDropdownOpen = ref(false);
const userDropdownRef = ref<HTMLElement | null>(null);

// 切换下拉菜单显示状态
const toggleDropdown = () => {
  isDropdownOpen.value = !isDropdownOpen.value;
};

// 处理退出登录
const handleLogout = async () => {
  try {
    // 调用登出API
    await logout();

    // 清除用户状态
    userStore.clearLoginState();

    // 跳转到登录页
    router.push('/auth/login');
  } catch (error) {
    console.error('退出登录失败:', error);
    ElMessage.error('退出登录失败');
  }
};

// 点击外部关闭下拉菜单
const handleClickOutside = (event: MouseEvent) => {
  const target = event.target as Node;
  if (userDropdownRef.value && !userDropdownRef.value.contains(target)) {
    isDropdownOpen.value = false;
  }
};

// 组件挂载时获取用户信息
onMounted(async () => {
  // 如果用户信息未加载，则加载用户信息
  if (!userStore.userInfoLoaded) {
    const success = await userStore.loadUserInfo();
    if (!success) {
      // 如果加载失败，跳转到登录页
      router.push('/auth/login');
    }
  }

  // 添加全局点击事件监听器
  document.addEventListener('click', handleClickOutside);
});

// 组件卸载前移除事件监听器
onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside);
});
</script>

<style lang="scss" scoped>
/* 添加转场动画 */
.dropdown-enter-active,
.dropdown-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
