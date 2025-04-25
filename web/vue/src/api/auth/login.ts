// === 导入你的自定义 service 实例 ===
import service from '@/utils/request';
// ==================================
import type { AxiosResponse } from 'axios';
import { ElMessage } from 'element-plus';
import { API_BASE_URL } from '@/config';

// ====================================================================
// 定义后端通用响应结构 - 这个用于除了登录接口之外的其他接口
// ====================================================================
interface ApiResponse<T> {
  code: number;
  msg?: string;
  data?: T;
}
// ====================================================================

// ====================================================================
// 定义登录接口成功时的响应数据类型 - 精确匹配后端 `/auth/login` 成功格式
// ====================================================================
interface LoginSuccessResponseData { // 任然使用这个名字表示后端返回的 data payload
  code: number; // 成功时应为 200
  token: string; // 成功时应包含 token 字段
  msg?: string; // 后端可能在成功时也包含 msg
}
// ====================================================================


// 定义用户信息结构 (确保与后端 /auth/currentUser 返回的 data 部分一致)
export interface UserInfo {
  userId: string;
  userName: string;
  userEmail: string;
  // ... 其他用户信息字段
}


/**
 * @description 用户登录接口
 * @param {Object} loginData - 登录数据
 * ... (JSDoc looks okay) ...
 * @returns {Promise<LoginSuccessResponseData>} - 成功时返回包含 code 和 token 的对象，失败时Promise会reject
 */
export async function login(loginData: {
  userName: string;
  password: string;
  captcha: string;
  codeKey: string;
}): Promise<LoginSuccessResponseData> {
  const loginUrl = `${API_BASE_URL}/auth/login`;

  // 客户端基本校验
  if (!loginData.userName || !loginData.password || !loginData.captcha) {
    const errorMsg = '请填写完整表单';
    ElMessage.error(errorMsg);
    return Promise.reject(new Error(errorMsg));
  }
  console.log('登录请求发送前:', loginData);

  try {
    // === 使用 service 实例发送 POST 请求 ===
    // === await service.post 将返回完整的 response.data 对象 { code, token, msg? } 或 { code, msg } ===
    const responseData = await service.post(loginUrl, loginData); // <-- 直接接收 response.data
    // ====================================

    // === 拦截器确保 responseData 存在且 code 属性存在，并在业务码非 200 时抛错 ===
    // 这里只需检查业务码是否是 200 并检查 token 字段
    if (responseData.code === 200) {
      // 业务码 200 成功，检查 token 字段是否存在 (对于登录接口特别重要)
      if (responseData.token !== undefined) {
          ElMessage.success(responseData.msg || '登录成功'); // 使用后端 msg
          return responseData as LoginSuccessResponseData; // 返回完整对象
      } else {
          // 业务码 200 但缺少 token (异常情况)
          console.error('登录失败: 后端返回数据缺少token字段', responseData);
          const errorMsg = responseData.msg || '登录失败，后端未返回Token';
          ElMessage.error(errorMsg);
          return Promise.reject(new Error(errorMsg));
      }
    } else {
      // 理论上不应该命中，因为拦截器在业务码非 200 时会抛错
      // 如果命中，说明拦截器处理异常
       const errorMsg = responseData.msg || `登录返回业务码异常 (${responseData.code})`;
       console.error('登录返回业务码异常:', responseData);
       ElMessage.error(errorMsg);
       return Promise.reject(new Error(errorMsg));
    }
  } catch (error: any) { // 捕获拦截器抛出的错误
    console.error('登录请求最终处理失败:', error);
    // 错误消息通常已经在拦截器中弹窗了，这里避免重复
    // ElMessage.error(error.message || '登录失败，请稍后再试。');
    return Promise.reject(error);
  }
}

/**
 * @description 获取当前用户信息
 * @returns {Promise<UserInfo>} 用户信息 (从 response.data.data 中提取)
 */
export async function getCurrentUser(): Promise<UserInfo> {
  try {
    // === 使用 service 实例发送 GET 请求 ===
    // === await service.get 将返回完整的 response.data 对象 { code, msg, data } ===
    const responseData = await service.get(`${API_BASE_URL}/auth/currentUser`); // <-- 直接接收 response.data
    // ==================================

    // === 拦截器确保 responseData 存在且 code 属性存在，并在业务码非 200 时抛错 ===
    // 这里检查业务码是否是 200 并提取 data 字段
    if (responseData.code === 200) {
        // 业务码 200 成功，检查 data 字段是否存在且不是 null/undefined
        if (responseData.data !== undefined && responseData.data !== null) {
             return responseData.data as UserInfo; // 返回 data 字段内容
        } else {
            // 业务码 200 但 data 字段缺失 (异常情况)
            console.error('获取用户信息失败: 后端返回数据缺少data字段', responseData);
            const errorMsg = responseData.msg || '获取用户信息失败，后端未返回数据';
            ElMessage.error(errorMsg);
            return Promise.reject(new Error(errorMsg));
        }
    } else {
        // 理论上不应该命中，因为拦截器在业务码非 200 时会抛错
        // 如果命中，说明拦截器处理异常
        const errorMsg = responseData.msg || `获取用户信息返回业务码异常 (${responseData.code})`;
        console.error('获取用户信息返回业务码异常:', responseData);
        ElMessage.error(errorMsg);
        return Promise.reject(new Error(errorMsg));
    }
  } catch (error: any) { // 捕获拦截器抛出的错误
    console.error('获取用户信息请求最终处理失败:', error);
    // 错误消息通常已经在拦截器中弹窗了，这里避免重复
    // ElMessage.error(error.message || '获取用户信息失败，请稍后再试。');
    return Promise.reject(error);
  }
}

/**
 * @description 退出登录
 * @returns {Promise<void>} 表示异步操作完成
 */
export async function logout(): Promise<void> {
  try {
    // === 使用 service 实例发送 DELETE 请求 ===
    // === await service.delete 将返回完整的 response.data 对象 { code, msg?, data? } ===
    const responseData = await service.delete(`${API_BASE_URL}/auth/logout`); // <-- 直接接收 response.data
    // ======================================

    // === 拦截器确保 responseData 存在且 code 属性存在，并在业务码非 200 时抛错 ===
    // 这里检查业务码是否是 200
    if (responseData.code === 200) {
        // 业务码 200 成功
        // ElMessage.success(responseData.msg || '退出成功'); // 消息提示在 Store 或组件处理
        return; // 返回 void
    } else {
        // 理论上不应该命中，因为拦截器在业务码非 200 时会抛错
        // 如果命中，说明拦截器处理异常
        const errorMsg = responseData.msg || `退出返回业务码异常 (${responseData.code})`;
        console.error('退出返回业务码异常:', responseData);
        throw new Error(errorMsg); // 抛出错误
    }
  } catch (error: any) { // 捕获拦截器抛出的错误
    console.error('退出登录请求最终处理失败:', error);
    // 错误消息通常已经在拦截器中弹窗了，这里避免重复
    // ElMessage.warning(error.message || '退出处理失败');
    throw error; // 重新抛出捕获到的错误
  }
}
