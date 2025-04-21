import { ref, computed } from 'vue';
import { defineStore } from 'pinia';
// 确保 UserInfo 类型和 getCurrentUser 方法的导入路径正确
// 例如：import { getCurrentUser, type UserInfo } from '@/api/auth/login';
import axios from 'axios'; // 导入 axios 用于判断 401 错误
import { getCurrentUser } from '@/api/auth/login';

// ====================================================================
// 定义用户信息接口 (请根据你后端实际 /auth/currentUser 接口返回的数据结构定义)
// 你可能需要根据你的实际情况修改或取消注释这里的定义
// ====================================================================
export interface UserInfo {
  userId: string;
  userName: string;
  userEmail?: string | null;
}
// ====================================================================

// ====================================================================
// 假设你的 API 调用方法 getCurrentUser 在 @/api/auth/login 中定义
// 并且它返回 Promise<UserInfo> 类型的数据
// ====================================================================
// async function getCurrentUser(): Promise<UserInfo> {
//   // 确保这里使用的是你配置了拦截器的 axios 实例，例如 service
//   // import service from '@/utils/request';
//   // const response = await service.get('/auth/currentUser'); // 假设后端接口路径
//   // return response.data.data; // 假设后端返回结构是 { code: 200, data: UserInfo }
//   // 如果后端返回结构不同，请相应调整
//   throw new Error("getCurrentUser API 方法未实现或导入路径错误"); // 仅为示例
// }
// ====================================================================


interface UserState {
  token: string | null;
  userInfo: UserInfo | null;
  userInfoLoaded: boolean; // 是否已成功加载过用户信息
  isLoadingUserInfo: boolean; // 是否正在加载用户信息
}

export const useUserStore = defineStore('user', () => {
  // === State ===
  // 用户令牌，手动从 localStorage 初始化
  const token = ref<string | null>(localStorage.getItem('token'));

  // 用户信息
  const userInfo = ref<UserInfo | null>(null); // 使用 UserInfo 类型

  // 是否已成功加载用户信息
  const userInfoLoaded = ref(false);

  // 是否正在加载用户信息
  const isLoadingUserInfo = ref(false);


  // === Getters ===
  // 计算属性：是否已登录 (基于 token 是否存在)
  const isLoggedIn = computed(() => !!token.value);

  // 计算属性：用户信息是否已加载且可用 (加载成功且 userInfo 对象存在且有 userId)
  const hasUserInfo = computed(() => userInfoLoaded.value && !!userInfo.value?.userId);


  // === Actions ===
  // 设置登录信息 (登录成功时调用，并手动保存 Token 到 localStorage)
  function setLoginInfo({ token: newToken, userInfo: newUserInfo }: { token: string; userInfo?: UserInfo | null }) {
    token.value = newToken;
    // 登录接口可能直接返回用户信息，优先使用登录接口返回的
    userInfo.value = newUserInfo || null;
    userInfoLoaded.value = !!newUserInfo; // 如果登录接口返回了用户信息，则标记为已加载

    // === 手动保存 Token 到 localStorage ===
    if (newToken) {
       localStorage.setItem('token', newToken);
    } else {
       // 如果 newToken 为 null 或 undefined，也需要从 localStorage 移除
       localStorage.removeItem('token');
    }
    // 如果登录接口返回了用户信息，也可以选择性保存到 localStorage (如非敏感信息)
    // if (newUserInfo) {
    //     localStorage.setItem('user_info', JSON.stringify(newInfoUser)); // 注意这里 userIfo 改为 newUserInfo
    // } else {
    //     localStorage.removeItem('user_info');
    // }
    // ===================================

    // 确保加载状态为 false
    isLoadingUserInfo.value = false;
  }

  // 加载用户信息 (通常由路由守卫或应用初始化时调用)
  // 这个 Action 负责通过 API 获取用户详情，不直接操作 localStorage 的 token
  async function loadUserInfo(): Promise<boolean> {
    // 如果已经在加载中，或者已经成功加载了用户信息，则不再重复加载
    if (isLoadingUserInfo.value || hasUserInfo.value) {
        console.log('用户信息已在加载中或已加载，跳过重复加载。');
        return hasUserInfo.value; // 返回当前用户信息是否可用状态
    }

    // 如果没有 Token，则肯定未登录，不进行 API 调用，清空状态并返回 false
    if (!token.value) {
        console.log('无 Token，无法加载用户信息 API。');
        clearLoginState(); // 确保状态是清空的
        return false;
    }

    // 开始加载用户信息 API 调用
    isLoadingUserInfo.value = true;
    console.log('开始加载用户信息...');

    try {
      const info = await getCurrentUser(); // 调用获取用户信息的 API (假设返回 UserInfo 类型)

      userInfo.value = info; // 更新用户信息状态
      userInfoLoaded.value = true; // 标记为已成功加载
      isLoadingUserInfo.value = false; // 停止加载状态
      console.log('用户信息加载成功。');
      return true; // 返回加载成功
    } catch (error) {
      console.error('加载用户信息失败:', error);
      userInfo.value = null; // 清空用户信息
      userInfoLoaded.value = false; // 标记为未成功加载
      isLoadingUserInfo.value = false; // 停止加载状态

      // === 添加 401 错误处理 ===
      // 如果是 Axios 错误且状态码是 401，则可能是 Token 失效，清空登录状态
      // 注意：这里调用 clearLoginState 会再次移除 localStorage 的 token，是预期的行为
      if (axios.isAxiosError(error) && error.response?.status === 401) {
          console.warn('检测到 401 错误，Token 可能失效，清除登录状态。');
          clearLoginState(); // 调用清除状态的方法
      }
      // ======================

      // 可以选择在这里抛出错误，如果调用者（如路由守卫）需要捕获并做进一步处理
      // throw error;
      return false; // 返回加载失败
    }
  }

  // 清除登录状态 (登出时调用，或遇到无效 Token 时调用)
  // 手动移除 localStorage 的 Token
  function clearLoginState() {
    token.value = null;
    userInfo.value = null;
    userInfoLoaded.value = false;
    isLoadingUserInfo.value = false; // 确保加载状态也重置
    // === 手动移除 Token 从 localStorage ===
    localStorage.removeItem('token');
    // localStorage.removeItem('user_info'); // 如果保存了用户信息也清除
    // ===================================
    console.log('用户登录状态已清除。');
  }

  return {
    token,
    userInfo,
    userInfoLoaded,
    isLoadingUserInfo,
    isLoggedIn,
    hasUserInfo,
    setLoginInfo,
    loadUserInfo,
    clearLoginState
  }
}
// === 移除这里的 persist 配置块 ===
/*
{
  persist: {
    key: 'userStore',
    storage: localStorage,
    paths: ['token']
  }
}
*/
// ==============================
); // <--- defineStore 函数结束，没有第二个参数了
