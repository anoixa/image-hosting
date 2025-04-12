<template>
  <div
    class="flex-col justify-center items-center w-3/5 h-full rounded-xl shadow-lg pt-4 pb-4 bg-white bg-opacity-90 backdrop:blur-sm">
    <div class="w-full h-full p-10">
      <div class="flex flex-col justify-center items-center">
        <div>
          <!-- LOGO -->
        </div>
        <input type="text" v-model="registerData.userName" class="rounded-xl w-full mb-6 h-10 mr-1 p-4 border border-gray-300 text-gray-500/90 text-sm
        transition-all ease-in-out outline-none focus: ring-indigo-500/70 focus:ring-2 duration-300"
          placeholder="请输入用户名">
        <!-- <input type="email" v-model="registerData.userEmail" class="rounded-xl w-full mb-6 h-10 mr-1 p-4 border border-gray-300 text-gray-500/90 text-sm
        transition-all ease-in-out outline-none focus:ring-indigo-500/70 focus:ring-2 duration-300"
          placeholder="请输入邮箱"> -->
        <div class="relative flex w-full">
          <input type="text" v-model="registerData.userEmail" class="rounded-xl w-full pr-20 mb-6 h-10 p-4 border border-gray-300 text-gray-500/90 text-sm
          transition-all ease-in-out outline-none focus:ring-indigo-500/70 focus:ring-2 duration-300"
            placeholder="请输入邮箱">
          <button type="button" @click="handleGetCaptcha" class="absolute right-0 top-0 bottom-0 w-1/5 h-10 bg-indigo-500 text-white rounded-r-xl
          transition-all ease-in-out hover:bg-indigo-600 focus:ring-2 duration-300 text-sm">
            获取验证码
          </button>
        </div>
        <input type="password" v-model="registerData.password" class="rounded-xl w-full mb-6 h-10 mr-1 p-4 border border-gray-300 text-gray-500/90 text-sm
        transition-all ease-in-out outline-none focus:ring-indigo-500/70 focus:ring-2 duration-300"
          placeholder="请输入密码">
        <div class="relative mb-6 flex w-full">
          <input type="text" v-model="registerData.captcha" class="rounded-xl w-full pr-20 mb-6 h-10 p-4 border border-gray-300 text-gray-500/90 text-sm
          transition-all ease-in-out outline-none focus:ring-indigo-500/70 focus:ring-2 duration-300"
            placeholder="请输入验证码">
          <!-- <button type="button" @click="getCaptcha" class="absolute right-0 top-0 bottom-0 w-1/5 h-10 bg-indigo-500 text-white rounded-r-xl
          transition-all ease-in-out hover:bg-indigo-600 focus:ring-2 duration-300 text-sm">
            获取验证码
          </button> -->
        </div>
        <div class="mb-6 flex justify-between">
          <div class="flex justify-center items-center pb-3">
            <input type="checkbox" class="mr-1 ">
            <span class="text-gray-500/90 text-sm []">我已阅读并同意</span>
            <a href="#" class="text-indigo-500/90 text-sm">《用户协议》</a>
          </div>
          <div class="flex justify-center items-center"></div>
        </div>
        <button type="button" @click="handleRegister" class="rounded-xl w-3/5 h-10 text-white bg-indigo-500/90 text-md transition-all ease-in-out hover:bg-indigo-600 focus:ring-2 font-semibold
          duration-300 hover:-translate-y-1">
          注册
        </button>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { reactive } from 'vue';
import { useRouter } from 'vue-router';
import { register, getEmailCaptcha, validateVerificationCode } from '@/api/auth/register';
import { validateForm } from '@/api/auth/validate';

// 路由
const router = useRouter();

// 一个RegisterData对象，使用reactive
const registerData = reactive({
  userName: '',
  userEmail: '',
  password: '',
  captcha: '',
  codeKey: ''
})

//注册函数
async function handleRegister() {
  // 验证表单
  const isFormValid = await validateForm(registerData);
  if (!isFormValid) return;

  // 验证验证码
  const isCodeValid = await validateVerificationCode(registerData.codeKey, registerData.captcha);
  if (!isCodeValid) return;

  // 执行注册
  const isSuccess = await register(registerData);
  if (isSuccess) {
    router.push('/auth/login');
  }
}

// 获取验证码函数
async function handleGetCaptcha() {
  const codeKey = await getEmailCaptcha(registerData.userEmail);
  if (codeKey) {
    registerData.codeKey = codeKey;
  }
}
</script>

<style lang="scss" scoped></style>
