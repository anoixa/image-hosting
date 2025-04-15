<template>
  <div class="w-full">
    <div class="p-6 justify-center flex flex-col items-center">
      <form @submit.prevent="handleResetPassword" class="w-4/5">
        <div class="mb-4">
          <label for="userEmail" class="block text-sm font-medium text-gray-700 mb-1">邮箱</label>
          <div class="relative">
            <input
              type="text"
              v-model="resetData.userEmail"
              :class="[
                'rounded-xl w-full h-10 p-4 border text-gray-500/90 text-sm transition-all ease-in-out outline-none pr-28',
                emailError
                  ? 'border-red-300 focus:ring-2 focus:ring-red-500 focus:border-transparent'
                  : 'border-gray-300 focus:ring-indigo-500/70 focus:ring-2 focus:border-transparent duration-300',
              ]"
              placeholder="请输入邮箱"
              required
            />
            <button
              type="button"
              :disabled="isSending"
              @click="handleSendCode"
              class="absolute right-0 top-0 bottom-0 w-1/3 h-10 text-white rounded-r-xl transition-all ease-in-out focus:ring-2 duration-300 text-sm"
              :class="[isSending ? 'bg-gray-400 cursor-not-allowed' : 'bg-indigo-500 hover:bg-indigo-600']"
            >
              {{ isSending ? `重新发送(${remainingTime}s)` : '获取验证码' }}

            </button>
          </div>
          <p v-if="emailError" class="text-red-500 text-sm mt-1">{{ emailError }}</p>
        </div>
        <div class="mb-4">
          <label for="captcha" class="block text-sm font-medium text-gray-700 mb-1">验证码</label>
          <input
            type="text"
            v-model="resetData.captcha"
            class="rounded-xl w-full h-10 p-4 border border-gray-300 text-gray-500/90 text-sm transition-all ease-in-out outline-none focus:ring-indigo-500/70 focus:ring-2 duration-300"
            :class="[
              'rounded-xl w-full h-10 p-4 border text-gray-500/90 text-sm transition-all ease-in-out outline-none',
              captchaError
                ? 'border-red-300 focus:ring-2 focus:ring-red-500 focus:border-transparent'
                : 'border-gray-300 focus:ring-indigo-500/70 focus:ring-2 focus:border-transparent duration-300',
            ]"
            placeholder="请输入验证码"
            required
          />
          <p v-if="captchaError" class="text-red-500 text-sm mt-1">{{ captchaError }}</p>
        </div>
        <div class="mb-6">
          <label for="newPassword" class="block text-sm font-medium text-gray-700 mb-1">设置新密码</label>
          <input
            type="password"
            v-model="resetData.newPassword"
            class="rounded-xl w-full h-10 p-4 border border-gray-300 text-gray-500/90 text-sm transition-all ease-in-out outline-none focus:ring-indigo-500/70 focus:ring-2 duration-300"
            placeholder="请输入新密码"
            required
            :class="[
              'rounded-xl w-full h-10 p-4 border text-gray-500/90 text-sm transition-all ease-in-out outline-none',
              passwordError
                ? 'border-red-300 focus:ring-2 focus:ring-red-500 focus:border-transparent'
                : 'border-gray-300 focus:ring-indigo-500/70 focus:ring-2 focus:border-transparent duration-300',
            ]"
          />
          <p v-if="passwordError" class="text-red-500 text-sm mt-1">{{ passwordError }}</p>
        </div>
        <button type="submit" class="mt-4 w-full h-10 bg-indigo-500 text-white rounded-xl transition-all ease-in-out hover:bg-indigo-600 focus:ring-2 duration-300 text-sm">重置密码</button>
        <div v-if="message" class="mt-4 p-2 rounded-md" :class="{'bg-green-100 text-green-700': success, 'bg-red-100 text-red-700': !success}">{{ message }}</div>
      </form>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import {
  sendResetPasswordCode,
  validateResetCode,
  resetPassword,
  isUserRegistered
} from '@/api/auth/password';

// 路由
const router = useRouter();

// 反馈消息
const message = ref('');
const success = ref(false);

// 发送验证码按钮状态
const isSending = ref(false);
const remainingTime = ref(0);

// input 错误信息
const emailError = ref('');
const captchaError = ref('');
const passwordError = ref('');

// 找回密码数据
const resetData = reactive({
  userEmail: '',
  newPassword: '',
  captcha: '',
  codeKey: '',
});

// 处理发送验证码
const startCountdown = () => {
  isSending.value = true;
  remainingTime.value = 60;

  const interval = setInterval(() => {
    remainingTime.value--;
    if (remainingTime.value <= 0) {
      clearInterval(interval);
      isSending.value = false;
    }
  }, 1000);
};


async function handleSendCode() {
  // 重置错误信息
  emailError.value = '';
  message.value = '';

  if (isSending.value) {
    return;
  }

  // 验证邮箱格式
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!resetData.userEmail) {
    emailError.value = '请输入邮箱地址';
    return;
  }
  if (!emailRegex.test(resetData.userEmail)) {
    emailError.value = '请输入有效的邮箱地址';
    return;
  }

  // 先检查邮箱是否已注册
  try {
    const isRegistered = await isUserRegistered(resetData.userEmail);
    if (!isRegistered) {
      emailError.value = '该邮箱尚未注册，请先注册账号';
      return;
    }

    // 邮箱已注册，开始倒计时
    startCountdown();
    
    // 发送验证码
    const codeKey = await sendResetPasswordCode(resetData.userEmail);
    if (codeKey) {
      resetData.codeKey = codeKey;
      message.value = '验证码已发送，请查收';
      success.value = true;
    } else {
      message.value = '验证码发送失败，请重试';
      success.value = false;
    }
  } catch (error: any) {
    message.value = error.response?.data?.msg || '验证码发送失败，请重试';
    success.value = false;
  }
}

// 处理重置密码
async function handleResetPassword() {
  // 重置错误信息
  message.value = '';
  emailError.value = '';
  captchaError.value = '';
  passwordError.value = '';

  // 表单验证
  if (!resetData.userEmail) {
    emailError.value = '请输入邮箱';
    return;
  }
  if (!resetData.captcha) {
    captchaError.value = '请输入验证码';
    return;
  }
  if (!resetData.newPassword) {
    passwordError.value = '请输入新密码';
    return;
  }
  // 验证验证码
  try {
    const isCodeValid = await validateResetCode(resetData.codeKey, resetData.captcha);
    if (!isCodeValid) {
      message.value = '验证码错误';
      success.value = false;
      return;
    }

    // 执行密码重置
    const isSuccess = await resetPassword(resetData);
    if (isSuccess) {
      message.value = '密码重置成功，请重新登录';
      success.value = true;
      setTimeout(() => {
        router.push('/auth/login');
      }, 2000); // Redirect after 2 seconds for message visibility
    } else {
      message.value = '密码重置失败，请重试';
      success.value = false;
    }
  } catch (error: any) {
    message.value = error.response?.data?.msg || '密码重置失败，请重试';
    success.value = false;
  }
}
</script>
