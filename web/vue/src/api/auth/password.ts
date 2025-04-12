import axios from 'axios';
import { ElMessage } from 'element-plus';
import { API_BASE_URL } from '@/config';

/**
 * @description 发送找回密码验证码
 * @param {string} userEmail - 用户邮箱
 * @returns {Promise<string>} - 返回验证码的key
 */
export async function sendResetPasswordCode(userEmail: string): Promise<string> {
  if (!userEmail) {
    ElMessage.error('请输入邮箱地址');
    return '';
  }

  const sendCodeUrl = `${API_BASE_URL}/password/sendResetCode?userEmail=${userEmail}`;
  try {
    const response = await axios.post(sendCodeUrl);
    if (response.data.code === 200) {
      ElMessage.success('验证码发送成功');
      return response.data.data.codeKey;
    } else {
      ElMessage.error(response.data.msg);
      return '';
    }
  } catch (error) {
    console.error('验证码发送失败:', error);
    ElMessage.error('验证码发送失败');
    return '';
  }
}

/**
 * @description 验证找回密码验证码
 * @param {string} codeKey - 验证码key
 * @param {string} codeValue - 验证码值
 * @returns {Promise<boolean>} - 返回验证是否成功
 */
export async function validateResetCode(codeKey: string, codeValue: string): Promise<boolean> {
  const validateUrl = `${API_BASE_URL}/password/validateResetCode?key=${codeKey}&codeValue=${codeValue}`;
  try {
    const response = await axios.post(validateUrl);
    if (response.data.code === 200) {
      ElMessage.success('验证码验证成功');
      return true;
    } else {
      ElMessage.error(response.data.msg);
      return false;
    }
  } catch (error) {
    console.error('验证码验证失败:', error);
    ElMessage.error('验证码验证失败');
    return false;
  }
}

/**
 * @description 重置密码
 * @param {Object} resetData - 重置密码数据
 * @param {string} resetData.userEmail - 用户邮箱
 * @param {string} resetData.newPassword - 新密码
 * @param {string} resetData.codeKey - 验证码key
 * @param {string} resetData.captcha - 验证码
 * @returns {Promise<boolean>} - 返回重置是否成功
 */
export async function resetPassword(resetData: {
  userEmail: string;
  newPassword: string;
  codeKey: string;
  captcha: string;
}): Promise<boolean> {
  const resetUrl = `${API_BASE_URL}/password/reset`;
  try {
    const response = await axios.post(resetUrl, resetData);
    if (response.data.code === 200) {
      ElMessage.success('密码重置成功');
      return true;
    } else {
      ElMessage.error(response.data.msg);
      return false;
    }
  } catch (error) {
    console.error('密码重置失败:', error);
    ElMessage.error('密码重置失败');
    return false;
  }
} 