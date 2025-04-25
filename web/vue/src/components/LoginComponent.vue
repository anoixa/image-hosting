<template>
  <div class="flex items-center justify-center w-96 backdrop:blur-sm">
    <div class="w-full max-w-md p-6 bg-white bg-opacity-20 rounded-xl shadow-xl">
      <!-- <el-form> -->
      <!-- <el-form-item class="mb-6">
          <el-input v-model="loginData.userName" placeholder="请输入用户名" class="w-full" size="large"></el-input>
        </el-form-item> -->

      <div class="mb-4 w-full">
        <label for="userName" class="block text-sm font-medium text-gray-700 mb-1">用户名</label>
        <input type="text" v-model="loginData.userName"
          class="rounded-xl w-full h-10 p-4 border border-gray-300 text-gray-500/90 text-sm
            transition-all ease-in-out outline-none focus:ring-indigo-500/70 focus:ring-2 duration-300"
          placeholder="请输入用户名或邮箱" />
      </div>
      <div class="mb-4 w-full">
        <label for="password" class="block text-sm font-medium text-gray-700 mb-1">密码</label>
        <input type="password" v-model="loginData.password"
          class="rounded-xl w-full h-10 p-4 border border-gray-300 text-gray-500/90 text-sm
            transition-all ease-in-out outline-none focus:ring-indigo-500/70 focus:ring-2 duration-300"
          placeholder="请输入密码" />
      </div>

      <!-- <el-form-item class="mb-6">
          <el-input v-model="loginData.password" placeholder="请输入密码" class="w-full" show-password
            size="large"></el-input>
        </el-form-item> -->
      <div class="mb-4">
        <label for="captcha" class="block text-sm font-medium text-gray-700 mb-1 w-full">验证码</label>
        <input type="text" v-model="loginData.captcha"
          class="rounded-xl w-full h-10 p-4 border border-gray-300 text-gray-500/90 text-sm
            transition-all ease-in-out outline-none focus:ring-indigo-500/70 focus:ring-2 duration-300"
          placeholder="请输入验证码" />

      </div>
      <!-- <el-form-item class="mb-6">
          <el-input v-model="loginData.captcha" placeholder="请输入验证码" class="w-full" size="large"></el-input>
        </el-form-item> -->

      <div class="mb-6 flex justify-between ">
        <img :src="loginData.captchaImage" @click="getGraghCaptcha()" alt="验证码"
          class="cursor-pointer h-10 w-24 shadow-md shadow-indigo-500/40 border border-gray-300/40 rounded-md ml-2 transition-all ease-in-out hover:-translate-y-1 duration-300" />
          <router-link to="/auth/find-password" class="min-w-24">
          <button class="flex-initial mr-1 h-10 w-24 text-indigo-700 hover:bg-indigo-400/90 hover:text-white duration-300
        rounded-lg hover:shadow-indigo-500/50 hover:shadow-md border border-indigo-400/80 transition-all ease-in-out hover:-translate-y-1">忘记密码</button>
        </router-link>
      </div>
      <button class="rounded-xl w-full mb-6 mt-3 bg-indigo-500 shadow-md shadow-indigo-400/70 text-cyan-50 h-10 hover:bg-indigo-600
        transition-all ease-in-out hover:-translate-y-1 duration-300 font-semibold" @click="handleLogin()">登录
      </button>
      <!-- </el-form> -->
    </div>
  </div>
</template>

<script lang="ts" setup>
import { useRouter } from 'vue-router';
import { onMounted, reactive } from 'vue';
import { useUserStore } from '@/stores/user' // 引入用户状态 store
import { API_BASE_URL } from '@/config';
import { ElMessage } from 'element-plus';
import { login } from '@/api/auth/login';
import axios from 'axios';
// import 'element-plus/dist/index.css';

// 路由
const router = useRouter();

// 登录数据
const loginData = reactive({
  userName: "",
  password: "",
  captcha: "",
  codeKey: "",
  captchaImage: ""
});

// 获取用户 store 实例
const userStore = useUserStore();

// 获取验证码
async function getGraghCaptcha() {
  const apiUrl = `${API_BASE_URL}/auth/getValidateCode`;
  try {
    const response = await axios.get(apiUrl);
    if (response.data.code === 200) {
      loginData.codeKey = response.data.data.codeKey;
      loginData.captchaImage = response.data.data.codeValue;
      console.log(response);
    }
  } catch (error) {
    console.error("获取验证码失败:", error);
    ElMessage.error("获取验证码失败");
  }
}

// 处理登录
async function handleLogin() {
  if (!loginData.userName || !loginData.password || !loginData.captcha) {
    ElMessage.warning('请填写完整的登录信息');
    return;
  }

  try {
    // 调用登录API
    const loginResponse = await login(loginData);

    // 保存登录信息到store
    userStore.setLoginInfo({
      token: loginResponse.token
    });

    // 加载用户信息
    await userStore.loadUserInfo();

    // 跳转到工作区
    router.push('/workplace');
  } catch (error: any) {
    // 错误已在API中处理
    console.error('登录失败:', error);

    // 刷新验证码
    getGraghCaptcha();
  }
}

onMounted(() => {
  // 获取验证码
  getGraghCaptcha();

  // 如果已登录，直接跳转到工作区
  if (userStore.isLoggedIn) {
    router.push('/workplace');
  }
});
</script>

<style lang="scss" scoped>
input {
  outline: none; // 去除聚焦时的默认边框
}
</style>
