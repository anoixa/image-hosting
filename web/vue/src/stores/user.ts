import { ref, computed } from 'vue';
import { defineStore } from 'pinia';
import axios from 'axios'; // 导入 axios 用于判断 401 错误
import { getCurrentUser } from '@/api/auth/login'; // 确保导入路径正确

// ====================================================================
// 定义用户信息接口 
// ====================================================================
export interface UserInfo {
  userId: string;
  userName: string;
  userEmail?: string | null;
}

// ====================================================================
// 定义图片接口 (与 MyImagesView.vue 中的定义一致，用于 selectedImage 状态)
// ====================================================================
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
  // isToggling is component-specific, no need in store
}
// ====================================================================


interface UserState {
  token: string | null;
  userInfo: UserInfo | null;
  userInfoLoaded: boolean; // 是否已成功加载过用户信息
  isLoadingUserInfo: boolean; // 是否正在加载用户信息
  // === 新增图片详情状态 ===
  selectedImage: Image | null;
  // ======================
}

export const useUserStore = defineStore('user', () => {
  // === State ===
  // 用户令牌，手动从 localStorage 初始化
  const token = ref<string | null>(localStorage.getItem('token'));

  // 用户信息
  const userInfo = ref<UserInfo | null>(null);

  // 是否已成功加载用户信息
  const userInfoLoaded = ref(false);

  // 是否正在加载用户信息
  const isLoadingUserInfo = ref(false);

  // === 新增图片详情状态 ===
  const selectedImage = ref<Image | null>(null);
  // ======================


  // === Getters ===
  // 计算属性：是否已登录 (基于 token 是否存在)
  const isLoggedIn = computed(() => !!token.value);

  // 计算属性：用户信息是否已加载且可用 (加载成功且 userInfo 对象存在且有 userId)
  const hasUserInfo = computed(() => userInfoLoaded.value && !!userInfo.value?.userId);

  // === 新增 Getter：根据 imageId 从 selectedImage 状态中查找图片 ===
  // 注意：这个 Getter 只检查当前的 selectedImage 是否匹配 ID
  // 如果你的图片列表在 Store 中管理 (比如 state 中有 imageList: Image[]),
  // 这个 Getter 可以增强为先检查 selectedImage，再遍历 imageList 查找。
  const findImageById = computed(() => (imageId: string): Image | null => {
      // 检查 selectedImage 是否存在且其 imageId 是否与传入的 imageId 匹配
      return selectedImage.value && selectedImage.value.imageId === imageId ? selectedImage.value : null;
  });
  // ==============================================================


  // === Actions ===
  // 设置登录信息 (登录成功时调用，并手动保存 Token 到 localStorage)
  function setLoginInfo({ token: newToken, userInfo: newUserInfo }: { token: string; userInfo?: UserInfo | null }) {
    token.value = newToken;
    userInfo.value = newUserInfo || null;
    userInfoLoaded.value = !!newUserInfo;

    if (newToken) {
       localStorage.setItem('token', newToken);
    } else {
       localStorage.removeItem('token');
    }

    isLoadingUserInfo.value = false;
  }

  // 加载用户信息 (通常由路由守卫或应用初始化时调用)
  async function loadUserInfo(): Promise<boolean> {
    if (isLoadingUserInfo.value || hasUserInfo.value) {
        console.log('用户信息已在加载中或已加载，跳过重复加载。');
        return hasUserInfo.value;
    }

    if (!token.value) {
        console.log('无 Token，无法加载用户信息 API。');
        clearLoginState();
        return false;
    }

    isLoadingUserInfo.value = true;
    console.log('开始加载用户信息...');

    try {
      const info = await getCurrentUser(); // 调用获取用户信息的 API

      userInfo.value = info;
      userInfoLoaded.value = true;
      isLoadingUserInfo.value = false;
      console.log('用户信息加载成功。');
      return true;
    } catch (error) {
      console.error('加载用户信息失败:', error);
      userInfo.value = null;
      userInfoLoaded.value = false;
      isLoadingUserInfo.value = false;

      if (axios.isAxiosError(error) && error.response?.status === 401) {
          console.warn('检测到 401 错误，Token 可能失效，清除登录状态。');
          clearLoginState();
      }
      return false;
    }
  }

  // 清除登录状态 (登出时调用，或遇到无效 Token 时调用)
  function clearLoginState() {
    token.value = null;
    userInfo.value = null;
    userInfoLoaded.value = false;
    isLoadingUserInfo.value = false;
    // === 清除选中的图片状态 ===
    selectedImage.value = null;
    // ======================
    localStorage.removeItem('token');
    console.log('用户登录状态已清除。');
  }

  // === 新增 Action：设置选中的图片详情 ===
  function setSelectedImage(image: Image | null) {
      selectedImage.value = image;
      console.log('Store 中设置选中的图片:', image?.imageId || 'null');
  }
  // ====================================


  return {
    token,
    userInfo,
    userInfoLoaded,
    isLoadingUserInfo,
    isLoggedIn,
    hasUserInfo,
    // === 导出新增的状态和方法 ===
    selectedImage, // 导出状态本身
    findImageById, // 导出 Getter
    setSelectedImage, // 导出 Action
    // =========================
    setLoginInfo,
    loadUserInfo,
    clearLoginState
  }
});
