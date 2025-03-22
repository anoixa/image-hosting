<template>
  <div>
    <div class="w-full max-w-lg p-6 bg-white bg-opacity-70 rounded-xl shadow-xl justify-center">
      <div class="relative flex w-full">
          <input type="text" v-model="UserData.userEmail" class="rounded-xl w-full pr-20 mb-6 h-10 p-4 border border-gray-300 text-gray-500/90 text-sm
          transition-all ease-in-out outline-none focus:ring-indigo-500/70 focus:ring-2 duration-300"
            placeholder="请输入邮箱">
          <button type="button" @click="getCaptcha" class="absolute right-0 top-0 bottom-0 w-1/5 h-10 bg-indigo-500 text-white rounded-r-xl
          transition-all ease-in-out hover:bg-indigo-600 focus:ring-2 duration-300 text-sm">
            获取验证码
          </button>
        </div>
        <div class="relative mb-6 flex w-full">
          <input type="text" v-model="UserData.captcha" class="rounded-xl w-full pr-20 mb-6 h-10 p-4 border border-gray-300 text-gray-500/90 text-sm
          transition-all ease-in-out outline-none focus:ring-indigo-500/70 focus:ring-2 duration-300"
            placeholder="请输入验证码">
        </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { reactive } from 'vue';
import axios from 'axios';
import { ElMessage } from 'element-plus';
import { API_BASE_URL } from '@/config';
// import { useRouter } from 'vue-router';

// 路由
// const router = useRouter();

// 一个UserData对象，使用reactive
const UserData = reactive({
  userEmail: '',
  password: '',
  captcha: '',
  codeKey: ''
})

// 获取验证码
async function getCaptcha() {
  console.log('当前 userEmail:', UserData.userEmail); // 调试信息
  if (!UserData.userEmail) {
    ElMessage.error('请输入邮箱地址');
    return;
  }

  let sendVerificationCodeUrl = `${API_BASE_URL}/register/sendVerificationCode`;

  try {
    //将UserData.userEmail拼接到sendVerificationCodeUrl上
    sendVerificationCodeUrl += `?userEmail=${UserData.userEmail}`;
    //发起请求
    const response = await axios.post(sendVerificationCodeUrl);
    if (response.data.code === 200) {
      UserData.codeKey = response.data.data.codeKey;
      console.log('获取验证码成功:', response.data);
      ElMessage.success('获取验证码成功');
    } else {
      console.error('获取验证码失败:', response.data.msg);
      ElMessage.error(response.data.msg);
    }
  } catch (error) {
    console.error('获取验证码失败:', error);
    ElMessage.error('获取验证码失败');
  }
}

</script>

<style lang="scss" scoped>

</style>
