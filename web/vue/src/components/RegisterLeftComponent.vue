<template>
  <div class="bg-white rounded-xl shadow-lg p-8 h-800 flex flex-col w-2/3">
    <div class="text-center mb-6">
      <h2 class="text-3xl font-bold text-gray-800 mb-2">创建新账号</h2>
      <p class="text-gray-600">加入我们，开始您的图片托管之旅</p>
    </div>

    <form @submit.prevent="handleRegister" class="space-y-5 flex-grow">
      <!-- 用户名输入框 -->
      <div>
        <label for="username" class="block text-sm font-medium text-gray-700 mb-1">用户名</label>
        <input
          id="username"
          v-model="registerData.userName"
          type="text"
          class="w-full px-4 py-2 rounded-lg border border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all"
          placeholder="请输入用户名"
          required
        />
      </div>

      <!-- 邮箱输入框 -->
      <div>
        <label for="email" class="block text-sm font-medium text-gray-700 mb-1">邮箱</label>
        <div class="relative">
          <input
            id="email"
            v-model="registerData.userEmail"
            type="email"
            @blur="validateEmail"
            :class="[
              'w-full px-4 py-2 rounded-lg border transition-all pr-32',
              emailError 
                ? 'border-red-300 focus:ring-2 focus:ring-red-500 focus:border-transparent' 
                : 'border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-transparent'
            ]"
            placeholder="请输入邮箱"
            required
          />
          <button
            type="button"
            @click="handleGetCaptcha"
            :disabled="captchaButtonDisabled || !!emailError"
            :class="[
              'absolute right-2 top-1/2 -translate-y-1/2 px-4 py-1 rounded-md text-sm transition-all',
              captchaButtonDisabled 
                ? 'bg-gray-300 text-gray-500 cursor-not-allowed' 
                : (emailError 
                    ? 'bg-gray-300 text-gray-500 cursor-not-allowed' 
                    : 'bg-indigo-500 text-white hover:bg-indigo-600 cursor-pointer')
            ]"
          >
            {{ captchaButtonText }}
          </button>
        </div>
        <p v-if="emailError" class="text-red-500 text-sm mt-1">{{ emailError }}</p>
      </div>

      <!-- 密码输入框 -->
      <div>
        <label for="password" class="block text-sm font-medium text-gray-700 mb-1">密码</label>
        <input
          id="password"
          v-model="registerData.password"
          type="password"
          class="w-full px-4 py-2 rounded-lg border border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all"
          placeholder="请输入密码"
          required
        />
      </div>

      <!-- 确认密码输入框 -->
      <div>
        <label for="confirmPassword" class="block text-sm font-medium text-gray-700 mb-1">确认密码</label>
        <input
          id="confirmPassword"
          v-model="confirmPassword"
          type="password"
          :class="[
            'w-full px-4 py-2 rounded-lg border border-gray-300 transition-all',
            confirmPassword
              ? (passwordMatch
                ? 'focus:ring-2 focus:ring-green-500 focus:border-transparent'
                : 'focus:ring-2 focus:ring-red-500 focus:border-transparent')
              : 'focus:ring-2 focus:ring-indigo-500 focus:border-transparent'
          ]"
          placeholder="请再次输入密码"
          required
        />
        <p v-if="confirmPassword && !passwordMatch" class="text-red-500 text-sm mt-1">两次输入的密码不一致</p>
      </div>

      <!-- 验证码输入框 -->
      <div>
        <label for="captcha" class="block text-sm font-medium text-gray-700 mb-1">验证码</label>
        <input
          id="captcha"
          v-model="registerData.captcha"
          type="text"
          class="w-full px-4 py-2 rounded-lg border border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all"
          placeholder="请输入验证码"
          required
        />
      </div>

      <!-- 用户协议 -->
      <div class="flex items-center">
        <input
          id="agreement"
          type="checkbox"
          class="h-4 w-4 text-indigo-500 focus:ring-indigo-500 border-gray-300 rounded"
          required
        />
        <label for="agreement" class="ml-2 block text-sm text-gray-700 cursor-pointer">
          我已阅读并同意
          <a href="#" class="text-indigo-500 hover:text-indigo-600">《用户协议》</a>
        </label>
      </div>

      <!-- 注册按钮 -->
      <button
        type="submit"
        :disabled="!passwordMatch"
        :class="[
          'w-full py-3 px-4 text-white rounded-lg transition-all font-medium',
          passwordMatch
            ? 'bg-indigo-500 hover:bg-indigo-600 focus:ring-2 focus:ring-indigo-300 focus:ring-offset-2 shadow-md hover:shadow-lg'
            : 'bg-gray-400 cursor-not-allowed'
        ]"
      >
        注册账号
      </button>
    </form>

    <!-- 底部链接 -->
    <div class="mt-6 text-center">
      <p class="text-sm text-gray-600">
        已有账号？
        <router-link to="/auth/login" class="text-indigo-500 hover:text-indigo-600 font-medium">
          立即登录
        </router-link>
      </p>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { register, getEmailCaptcha, validateVerificationCode } from '@/api/auth/register';
import { validateForm } from '@/api/auth/validate';

const router = useRouter();
const confirmPassword = ref('');
const emailError = ref('');
const captchaButtonDisabled = ref(false);
const captchaButtonText = ref('获取验证码');
const countdownSeconds = ref(0);

const registerData = reactive({
  userName: '',
  userEmail: '',
  password: '',
  captcha: '',
  codeKey: ''
});

const passwordMatch = computed(() => {
  if (!confirmPassword.value) return true;
  return registerData.password === confirmPassword.value;
});

// 验证邮箱格式
function validateEmail() {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!registerData.userEmail) {
    emailError.value = '请输入邮箱地址';
    return false;
  } else if (!emailRegex.test(registerData.userEmail)) {
    emailError.value = '请输入有效的邮箱地址';
    return false;
  } else {
    emailError.value = '';
    return true;
  }
}

// 开始倒计时
function startCountdown(seconds = 60) {
  captchaButtonDisabled.value = true;
  countdownSeconds.value = seconds;
  captchaButtonText.value = `${countdownSeconds.value}秒后重试`;
  
  const timer = setInterval(() => {
    countdownSeconds.value--;
    captchaButtonText.value = `${countdownSeconds.value}秒后重试`;
    
    if (countdownSeconds.value <= 0) {
      clearInterval(timer);
      captchaButtonDisabled.value = false;
      captchaButtonText.value = '获取验证码';
    }
  }, 1000);
}

async function handleRegister() {
  if (!passwordMatch.value) return;
  if (!validateEmail()) return;

  const isFormValid = await validateForm(registerData);
  if (!isFormValid) return;

  const isCodeValid = await validateVerificationCode(registerData.codeKey, registerData.captcha);
  if (!isCodeValid) return;

  const isSuccess = await register(registerData);
  if (isSuccess) {
    router.push('/auth/login');
  }
}

async function handleGetCaptcha() {
  if (captchaButtonDisabled.value) return;
  if (!validateEmail()) return;
  
  startCountdown();
  const codeKey = await getEmailCaptcha(registerData.userEmail);
  if (codeKey) {
    registerData.codeKey = codeKey;
  }
}
</script>

<style scoped>
.h-500 {
  height: 500px;
}
</style>
