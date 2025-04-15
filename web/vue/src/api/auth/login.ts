import axios from 'axios';
import { ElMessage } from 'element-plus';
import { API_BASE_URL } from '@/config';

/**
 * @description 用户登录接口
 * @param {Object} loginData - 登录数据
 * @param {string} loginData.userName - 用户名
 * @param {string} loginData.password - 密码
 * @returns {Promise<boolean>} - 返回登录是否成功
 */
export async function login(loginData: {
  userName: string;
  password: string;
}): Promise<boolean> {
  const loginUrl = `${API_BASE_URL}/auth/login`;
  if (!loginData.userName || !loginData.password) {
    ElMessage.error('请填写完整表单');
    return false;
  }
  try {
    const response = await axios.post(loginUrl, loginData);
    if (response.data.code === 200) {
      ElMessage.success('登录成功');
      return true;
    } else {
      ElMessage.error(response.data.msg);
      return false;
    }
  } catch (error) {
    console.error('登录失败:', error);
    ElMessage.error('登录失败');
    return false;
  }
}