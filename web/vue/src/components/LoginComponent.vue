<template>
  <div>
    <div>
      <el-form>
        <el-form-item>
          <el-input v-model="loginData.userName" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="loginData.password" placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="loginData.captcha" placeholder="请输入验证码"></el-input>
        </el-form-item>
        <el-form-item>
          <img :src="loginData.captchaImage" @click="getCaptcha()" alt="验证码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="login()">登录</el-button>
        </el-form-item>
        <el-form-item>
          <router-link to="/register">
            <el-button type="link">注册</el-button>
          </router-link>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { useRouter } from 'vue-router';
import { onMounted, reactive } from 'vue';
import axios from 'axios';
import { API_BASE_URL } from '@/config';
import { ElMessage } from 'element-plus';
import 'element-plus/dist/index.css';

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


// 获取验证码
async function getCaptcha() {
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
    console.log(error);
    ElMessage.error("获取验证码失败");
  }
}

// 登录
async function login() {
  const apiUrl = `${API_BASE_URL}/auth/login`;
  try {
    const response = await axios.post(apiUrl, loginData);
    if (response.data.code === 200) {
      // 登录成功，跳转到首页
      router.push("/");
      ElMessage.success("登录成功");
    } else {
      // 登录失败，显示错误信息
      ElMessage.error(response.data.msg);
    }
  } catch (error) {
    console.error("登录失败:", error);
    ElMessage.error("登录失败");
  }
}


onMounted(() => {
  getCaptcha();
});
</script>

<style lang="scss" scoped>
/* 登录页面的样式 */
</style>
