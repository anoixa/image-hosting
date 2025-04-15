import axios from 'axios';
import { ElMessage } from 'element-plus';
import { API_BASE_URL } from '@/config';

/**
 * @description 用户注册接口
 * @param {Object} registerData - 注册数据
 * @param {string} registerData.userName - 用户名
 * @param {string} registerData.userEmail - 用户邮箱
 * @param {string} registerData.password - 密码
 * @param {string} registerData.captcha - 验证码
 * @returns {Promise<boolean>} - 返回注册是否成功
 */
export async function register(registerData: {
  userName: string;
  userEmail: string;
  password: string;
  captcha: string;
}): Promise<boolean> {
  const registerUrl = `${API_BASE_URL}/register/register`;
  try {
    const response = await axios.post(registerUrl, registerData);
    if (response.data.code === 200) {
      ElMessage.success('注册成功');
      return true;
    } else {
      // 显示后端返回的具体错误信息
      ElMessage.error(response.data.msg || '注册失败');
      return false;
    }
  } catch (error: any) {
    console.error('注册失败:', error);
    // 显示后端返回的具体错误信息
    if (error.response?.data?.msg) {
      ElMessage.error(error.response.data.msg);
    } else {
      ElMessage.error('注册失败，请稍后重试');
    }
    return false;
  }
}

/**
 * @description 获取验证码接口
 * @param {string} userEmail - 用户邮箱
 * @returns {Promise<string>} - 返回验证码的key
 */
export async function getEmailCaptcha(userEmail: string): Promise<string> {
  if (!userEmail) {
    ElMessage.error('请输入邮箱地址');
    return '';
  }

  const sendVerificationCodeUrl = `${API_BASE_URL}/register/sendVerificationCode?userEmail=${userEmail}`;
  
  try {
    const response = await axios.post(sendVerificationCodeUrl);
    if (response.data.code === 200) {
      ElMessage.success('获取验证码成功');
      return response.data.data.codeKey;
    } else {
      ElMessage.error(response.data.msg || '获取验证码失败');
      return '';
    }
  } catch (error: any) {
    console.error('获取验证码失败:', error);
    if (error.response?.data?.msg) {
      ElMessage.error(error.response.data.msg);
    } else {
      ElMessage.error('获取验证码失败，请稍后重试');
    }
    return '';
  }
}

/**
 * @description 验证验证码接口
 * @param {string} codeKey - 验证码key
 * @param {string} codeValue - 验证码值
 * @returns {Promise<boolean>} - 返回验证是否成功
 */
export async function validateVerificationCode(codeKey: string, codeValue: string): Promise<boolean> {
  const validateVerificationCodeUrl = `${API_BASE_URL}/register/validateVerificationCode?key=${codeKey}&codeValue=${codeValue}`;
  
  try {
    const response = await axios.post(validateVerificationCodeUrl);
    if (response.data.code === 200) {
      ElMessage.success('验证码验证成功');
      return true;
    } else {
      ElMessage.error(response.data.msg || '验证码验证失败');
      return false;
    }
  } catch (error: any) {
    console.error('验证码验证失败:', error);
    if (error.response?.data?.msg) {
      ElMessage.error(error.response.data.msg);
    } else {
      ElMessage.error('验证码验证失败，请稍后重试');
    }
    return false;
  }
} 