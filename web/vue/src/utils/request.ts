import axios from 'axios';
import type { AxiosInstance, InternalAxiosRequestConfig, AxiosResponse } from 'axios';
import { ElMessage } from 'element-plus';
import { API_BASE_URL } from '@/config';
import router from '@/router';

// 创建axios实例
const service: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 15000,
  headers: {
    // 'Content-Type': 'application/json;charset=utf-8',
  }
});

// 请求拦截器
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 从localStorage获取token
    const token = localStorage.getItem('token');
    if (token) {
      // 为请求头添加token
      config.headers = config.headers || {};
      config.headers['Authorization'] = token; // 确保 Authorization 是标准写法
    }
    return config;
  },
  (error) => {
    console.error('请求拦截器错误:', error);
    return Promise.reject(error);
  }
);

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse) => {
    // --- 响应拦截器成功回调 ---
    // HTTP 状态码是 2xx
    // response.data 是 Axios 解析后的响应体

    // 检查 response.data 是否存在且有 code 属性 (假设后端返回的数据总是有 code 字段)
    if (response.data === undefined || response.data === null || response.data.code === undefined) {
        // 如果后端在 HTTP 2xx 状态码时返回空 body 或缺少 code 字段，我们在这里判断并抛出错误
        const err = new Error('后端返回数据格式不正确或缺少业务码');
        (err as any).response = response; // 附带原始响应信息
        return Promise.reject(err);
    }

    // 尝试安全地获取后端业务状态码和消息
    const resCode = response.data.code;
    const resMsg = response.data.msg;
    // const requestUrl = response.config.url; // 不需要根据 URL 判断了

    // 如果后端业务码不是 200 (业务错误)
    if (resCode !== 200) {
        // 抛出错误，让 response 拦截器的错误回调统一处理提示和 401 跳转
        const err = new Error(resMsg || `业务错误(${resCode})`);
        (err as any).code = resCode; // 附加业务码
        (err as any).response = response; // 附带原始响应信息
        return Promise.reject(err);
    }

    // 后端业务码是 200 (业务成功)
    // === 统一返回完整的 response.data 对象 ===
    // 无论是否是登录接口，都返回 response.data
    // 调用的地方需要自行检查 response.data 的结构 (code, msg, data 或 code, token 等)
    return response.data;
    // ==================================
  },
  (error) => {
    // --- 响应拦截器错误回调 ---
    console.error('响应拦截器错误:', error); // 使用 console.error

    // 使用 AxiosError type guard for better type inference
    if (axios.isAxiosError(error)) {
         if (error.response) {
            // 请求已发出，但服务器响应的状态码不在 2xx 范围内 (HTTP 错误，如 401, 404, 500)
            const status = error.response.status;
            const errorMsg = error.response.data?.msg?.toString() || `请求错误(${status})`; // Safely access msg

            // 重点：在这里统一处理 HTTP 错误状态码
            switch (status) {
                case 401: // Unauthorized - Token expired or invalid (HTTP 401)
                    ElMessage.error('登录状态已过期，请重新登录');
                    // 清除令牌和Store状态，并重定向到登录页
                    localStorage.removeItem('token');
                    router.push('/auth/login'); // 直接在拦截器中重定向
                    break;
                case 403: // Forbidden - No permission
                    ElMessage.error('无权访问，请联系管理员');
                    break;
                case 404: // Not Found
                         ElMessage.error('请求资源不存在');
                         break;
                case 500: // Internal Server Error
                         ElMessage.error('服务器发生错误，请稍后再试');
                         break;
                default: // 其他 HTTP 状态码错误
                     ElMessage.error(errorMsg);
            }
         } else if (error.request) {
             // 请求已发出但没有收到响应 (例如，服务器宕机，无网络连接)
             ElMessage.error('服务器无响应或网络连接中断');
         } else {
             // 发送请求时出了问题，例如请求配置错误
             ElMessage.error('请求配置错误: ' + error.message);
         }
    } else {
        // 非 Axios 错误 (例如，我们在成功回调中手动抛出的 Error)
        console.error('捕获到非 Axios 错误:', error);
        // 避免重复弹窗，只记录错误。如果需要在 Store/组件中处理，这里不再弹窗。
        // ElMessage.error(error.message || '发生未知错误');
    }

    // 始终返回 reject 状态的 Promise，将错误向下传递
    return Promise.reject(error);
  }
);

export default service;
