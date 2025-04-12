<template>
  <div>
    <div class="w-full max-w-lg p-6 bg-white bg-opacity-70 rounded-xl shadow-xl justify-center">
      <div class="relative flex w-full">
          <input type="text" v-model="resetData.userEmail" class="rounded-xl w-full pr-20 mb-6 h-10 p-4 border border-gray-300 text-gray-500/90 text-sm
          transition-all ease-in-out outline-none focus:ring-indigo-500/70 focus:ring-2 duration-300"
            placeholder="请输入邮箱">
          <button type="button" @click="handleSendCode" class="absolute right-0 top-0 bottom-0 w-1/5 h-10 bg-indigo-500 text-white rounded-r-xl
          transition-all ease-in-out hover:bg-indigo-600 focus:ring-2 duration-300 text-sm">
            获取验证码
          </button>
        </div>
        <div class="relative mb-6 flex w-full">
          <input type="text" v-model="resetData.captcha" class="rounded-xl w-full pr-20 mb-6 h-10 p-4 border border-gray-300 text-gray-500/90 text-sm
          transition-all ease-in-out outline-none focus:ring-indigo-500/70 focus:ring-2 duration-300"
            placeholder="请输入验证码">
        </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { reactive } from 'vue';
import { useRouter } from 'vue-router';
import { sendResetPasswordCode, validateResetCode, resetPassword } from '@/api/auth/password';

// 路由
const router = useRouter();

// 找回密码数据
const resetData = reactive({
  userEmail: '',
  newPassword: '',
  captcha: '',
  codeKey: ''
});

// 处理发送验证码
async function handleSendCode() {
  const codeKey = await sendResetPasswordCode(resetData.userEmail);
  if (codeKey) {
    resetData.codeKey = codeKey;
  }
}

// 处理重置密码
async function handleResetPassword() {
  // 验证验证码
  const isCodeValid = await validateResetCode(resetData.codeKey, resetData.captcha);
  if (!isCodeValid) return;

  // 执行密码重置
  const isSuccess = await resetPassword(resetData);
  if (isSuccess) {
    router.push('/auth/login');
  }
}
</script>

<style lang="scss" scoped>

</style>
