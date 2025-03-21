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
          <button type="button" @click="getCaptcha" class="absolute right-0 top-0 bottom-0 w-1/5 h-10 bg-indigo-500 text-white rounded-r-xl
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
        <button type="button" @click="register" class="rounded-xl w-3/5 h-10 text-white bg-indigo-500/90 text-md transition-all ease-in-out hover:bg-indigo-600 focus:ring-2 font-semibold
          duration-300 hover:-translate-y-1">
          注册
        </button>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { reactive } from 'vue';
import axios from 'axios';
import { ElMessage } from 'element-plus';
import { API_BASE_URL } from '@/config';
import { useRouter } from 'vue-router';

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
function register() {
  console.log(registerData)
  const registerUrl = `${API_BASE_URL}/register/register`
  let state = validateVerificationCode();
  state = checkForm();
  if(!state){
    return;
  }
    axios.post(registerUrl, {
      userName: registerData.userName,
      userEmail: registerData.userEmail,
      password: registerData.password,
      captcha: registerData.captcha
    })
    .then(response => {
      if (response.data.code === 200) {
        console.log('注册成功');
        ElMessage.success('注册成功');
        // 跳转到登陆页面
        router.push('/auth/login');
      }
      else {
        console.error('注册失败:', response.data.msg);
        ElMessage.error(response.data.msg);
      }
    })
    .catch(error => {
      console.error('注册失败:', error);
      ElMessage.error('注册失败');
    });
  }

// 验证验证码函数
function validateVerificationCode() {
  console.log(registerData)
  // 验证验证码
  const validateVerificationCodeUrl = `${API_BASE_URL}/register/validateVerificationCode`
  // 将registerData.codeKey 和 registerData.captcha 拼接到 validateVerificationCodeUrl 上
  const validateVerificationCodeUrlWithParams =`${validateVerificationCodeUrl}?key=${registerData.codeKey}&codeValue=${registerData.captcha}`;
  // 发起 POST 请求
  console.log('验证码验证请求发送:', validateVerificationCodeUrlWithParams);
  axios.post(validateVerificationCodeUrlWithParams)
    .then(response => {
      if (response.data.code === 200) {
        console.log('验证码验证成功');
        ElMessage.success('验证码验证成功');
        return true;
      } else {
        console.error('验证码验证失败:', response.data.msg);
        console.log('验证码验证失败:', response.data.msg);
        ElMessage.error(response.data.msg);
        return false;
      }
    })
    .catch(error => {
      console.error('验证码验证失败:', error);
      ElMessage.error('验证码验证失败');
      return false;
    });
    return false;
}

// 获取验证码函数
async function getCaptcha() {
  console.log('当前 userEmail:', registerData.userEmail); // 调试信息
  if (!registerData.userEmail) {
    ElMessage.error('请输入邮箱地址');
    return;
  }

  let sendVerificationCodeUrl = `${API_BASE_URL}/register/sendVerificationCode`;

  try {
    //将registerData.userEmail拼接到sendVerificationCodeUrl上
    sendVerificationCodeUrl += `?userEmail=${registerData.userEmail}`;
    //发起请求
    const response = await axios.post(sendVerificationCodeUrl);
    if (response.data.code === 200) {
      registerData.codeKey = response.data.data.codeKey;
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

// 检查表单
function checkForm() {
  const checkFormUrl = `${API_BASE_URL}/register/validateTable`
  if (!registerData.userName || !registerData.userEmail || !registerData.password || !registerData.captcha) {
    ElMessage.error('请填写完整表单');
    return false;
  }
  else {
    axios.post(checkFormUrl, {
      userName: registerData.userName,
      userEmail: registerData.userEmail,
      password: registerData.password,
      captcha: registerData.captcha
    }).then(response => {
      if (response.data.code === 200) {
        // 表单验证通过
        console.log('表单验证成功');
        ElMessage.success('表单验证成功');
        return true;
      } else {
        console.error('表单验证失败:', response.data.msg);
        ElMessage.error(response.data.msg);
        return false;
      }
    })
      .catch(error => {
        console.error('表单验证失败:', error);
        ElMessage.error('表单验证失败');
        return false;
      });
  }
  return true;
}

</script>

<style lang="scss" scoped></style>
