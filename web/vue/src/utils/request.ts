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
    'Content-Type': 'application/json;charset=utf-8',
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
      config.headers['Authorization'] = token;
    }
    return config;
  },
  (error) => {
    console.log('请求拦截器错误:', error);
    return Promise.reject(error);
  }
);

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data;
    // 如果响应码不是200，则判断为错误
    if (res.code !== 200) {
      ElMessage.error(res.msg || '系统错误');
      
      // 处理401错误（未授权/令牌过期）
      if (res.code === 401) {
        // 清除令牌
        localStorage.removeItem('token');
        // 重定向到登录页
        router.push('/auth/login');
      }
      return Promise.reject(new Error(res.msg || '系统错误'));
    }
    return res;
  },
  (error) => {
    console.log('响应拦截器错误:', error);
    // 处理HTTP错误
    if (error.response) {
      // 处理401错误（未授权/令牌过期）
      if (error.response.status === 401) {
        // 清除令牌
        localStorage.removeItem('token');
        // 重定向到登录页
        router.push('/auth/login');
        ElMessage.error('登录状态已过期，请重新登录');
      } else {
        // 其他HTTP错误
        ElMessage.error(error.response.data?.msg || `请求错误(${error.response.status})`);
      }
    } else if (error.request) {
      // 请求发出但没有收到响应
      ElMessage.error('服务器无响应，请稍后再试');
    } else {
      // 请求配置错误
      ElMessage.error('请求配置错误: ' + error.message);
    }
    return Promise.reject(error);
  }
);

export default service; 