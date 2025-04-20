import axios from 'axios';
import type { AxiosResponse } from 'axios'; // 使用type-only import
import { ElMessage } from 'element-plus';
import { API_BASE_URL } from '@/config';

// 定义成功登录时的响应数据类型
interface LoginSuccessResponse {
  code: number; // 期望是 200
  msg?: string; // 后端可能返回的成功消息
  token: string;
}

// 定义后端通用响应结构
interface ApiResponse<T> {
  code: number;
  msg?: string; // 错误或成功消息
  data?: T; // 成功时的数据
}

// 定义用户信息结构
export interface UserInfo {
  userId: string;
  userName: string;
  userEmail: string;
}

/**
 * @description 用户登录接口
 * @param {Object} loginData - 登录数据
 * @param {string} loginData.userName - 用户名
 * @param {string} loginData.password - 密码
 * @param {string} loginData.captcha - 验证码
 * @param {string} loginData.codeKey - 验证码key
 * @returns {Promise<LoginSuccessResponse>} - 成功时返回包含token等信息的对象，失败时Promise会reject
 */
export async function login(loginData: {
  userName: string;
  password: string;
  captcha: string;
  codeKey: string;
}): Promise<LoginSuccessResponse> {
  const loginUrl = `${API_BASE_URL}/auth/login`;

  // 客户端基本校验：直接抛出错误，让Promise进入reject状态
  if (!loginData.userName || !loginData.password || !loginData.captcha) {
    const errorMsg = '请填写完整表单';
    ElMessage.error(errorMsg);
    return Promise.reject(new Error(errorMsg)); // 返回reject状态的Promise
  }

  try {
    // 明确axios响应数据的类型
    const response: AxiosResponse<LoginSuccessResponse> = await axios.post(loginUrl, loginData);

    // 检查后端返回的业务状态码
    if (response.data.code === 200) {
      // 保存token到localStorage
      localStorage.setItem('token', response.data.token);

      // 设置默认Authorization请求头
      axios.defaults.headers.common['Authorization'] = response.data.token;

      ElMessage.success('登录成功');

      return response.data; // <-- 成功时返回整个 response.data，它符合 LoginSuccessResponse 结构
    } else {
      // 后端返回非200的业务错误码时
      const errorMsg = response.data.msg || '登录失败';
      ElMessage.error(errorMsg);
      // 抛出错误，让Promise进入reject状态
      return Promise.reject(new Error(errorMsg));
      // 或者直接 throw new Error(errorMsg);
    }
  } catch (error: any) { // 捕获 axios 或其他网络错误
    console.error('登录失败:', error);
    // 尝试获取详细错误信息
    const errorMsg = error.response?.data?.msg || '登录失败，请稍后再试。';
    ElMessage.error(errorMsg);
    // 抛出捕获到的错误，或者一个新的错误，让Promise进入reject状态
    return Promise.reject(error); // 重新抛出原始错误
  }
}

/**
 * @description 获取当前用户信息
 * @returns {Promise<UserInfo>} 用户信息
 */
export async function getCurrentUser(): Promise<UserInfo> {
  try {
    // 获取token
    const token = localStorage.getItem('token');
    console.log('从 localStorage 读取到的 token:', token);
    if (!token) {
      console.log('localStorage 中没有 token');
      return Promise.reject(new Error('未登录'));
    }
    console.log('发送请求时使用的 Authorization 头值:', token);
    const response = await axios.get(`${API_BASE_URL}/auth/currentUser`, {
      headers: {
        'Authorization': token
      }
    });

    if (response.data.code === 200) {
      return response.data.data;
    } else {
      return Promise.reject(new Error(response.data.msg || '获取用户信息失败'));
    }
  } catch (error: any) {
    console.error('获取用户信息失败:', error);
    return Promise.reject(error);
  }
}

/**
 * @description 退出登录
 * @returns {Promise<boolean>} 是否成功退出
 */
export async function logout(): Promise<boolean> {
  try {
    const token = localStorage.getItem('token');
    if (!token) {
      return true; // 已经未登录状态
    }

    const response = await axios.delete(`${API_BASE_URL}/auth/logout`, {
      headers: {
        'Authorization': token
      }
    });

    // 无论后端响应如何，前端都要清理
    localStorage.removeItem('token');
    delete axios.defaults.headers.common['Authorization'];

    if (response.data.code === 200) {
      ElMessage.success('退出成功');
      return true;
    } else {
      ElMessage.warning('退出处理出现问题，但已在本地清除登录状态');
      return true;
    }
  } catch (error) {
    console.error('退出登录失败:', error);
    // 即使API调用失败，也要清理本地存储
    localStorage.removeItem('token');
    delete axios.defaults.headers.common['Authorization'];
    ElMessage.warning('退出处理出现问题，但已在本地清除登录状态');
    return true;
  }
}
