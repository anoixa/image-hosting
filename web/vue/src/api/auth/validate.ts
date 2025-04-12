import axios from 'axios';
import { ElMessage } from 'element-plus';
import { API_BASE_URL } from '@/config';

/**
 * @description 表单验证接口
 * @param {Object} formData - 表单数据
 * @param {string} formData.userName - 用户名
 * @param {string} formData.userEmail - 用户邮箱
 * @param {string} formData.password - 密码
 * @param {string} formData.captcha - 验证码
 * @returns {Promise<boolean>} - 返回验证是否通过
 */
export async function validateForm(formData: {
  userName: string;
  userEmail: string;
  password: string;
  captcha: string;
}): Promise<boolean> {
  if (!formData.userName || !formData.userEmail || !formData.password || !formData.captcha) {
    ElMessage.error('请填写完整表单');
    return false;
  }

  const checkFormUrl = `${API_BASE_URL}/register/validateTable`;
  
  try {
    const response = await axios.post(checkFormUrl, formData);
    if (response.data.code === 200) {
      ElMessage.success('表单验证成功');
      return true;
    } else {
      ElMessage.error(response.data.msg);
      return false;
    }
  } catch (error) {
    console.error('表单验证失败:', error);
    ElMessage.error('表单验证失败');
    return false;
  }
} 