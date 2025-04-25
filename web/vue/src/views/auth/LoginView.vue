<template>
  <div>
    <div>
      <!-- Bg -->
      <div
        class="absolute h-full w-full bg-white [background:radial-gradient(125%_125%_at_50%_10%,#fff_40%,#63e_100%)]">
      </div>

      <div>

        <!-- Header -->
        <header class="flex items-center justify-between py-8">
          <div></div>
          <nav class="flex gap-6">
            <!-- <a href="#" target="_blank" rel="noopener noreferrer" class="inline-flex">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
                stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"
                class="h-6 w-6 text-neutral-800 transition-colors hover:text-neutral-600 dark:text-neutral-200 dark:hover:text-neutral-400">
                <path
                  d="M22 4s-.7 2.1-2 3.4c1.6 10-9.4 17.3-18 11.6 2.2.1 4.4-.6 6-2C3 15.5.5 9.6 3 5c2.2 2.6 5.6 4.1 9 4-.9-4.2 4-6.6 7-3.8 1.1 0 3-1.2 3-1.2z">
                </path>
              </svg>
            </a> -->
            <a href="https://github.com/anoixa/image-hosting" target="_blank" rel="noopener noreferrer"
              class="inline-flex">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
                stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"
                class="h-6 w-6 text-neutral-800 transition-colors hover:text-neutral-600 dark:text-neutral-200 dark:hover:text-neutral-400">
                <path
                  d="M15 22v-4a4.8 4.8 0 0 0-1-3.5c3 0 6-2 6-5.5.08-1.25-.27-2.48-1-3.5.28-1.15.28-2.35 0-3.5 0 0-1 0-3 1.5-2.64-.5-5.36-.5-8 0C6 2 5 2 5 2c-.3 1.15-.3 2.35 0 3.5A5.403 5.403 0 0 0 4 9c0 3.5 3 5.5 6 5.5-.39.49-.68 1.05-.85 1.65-.17.6-.22 1.23-.15 1.85v4">
                </path>
                <path d="M9 18c-4.51 2-5-2-7-2"></path>
              </svg>
            </a>
          </nav>
        </header>

        <!-- hero -->
        <div class="pt-10">
          <div class="relative mx-auto flex max-w-3xl flex-col items-center">
            <!-- <div
              class="border border-gray-600 inline-flex h-full cursor-pointer justify-center rounded-full bg-white px-3 py-1 text-xs font-medium leading-5 text-slate-600 backdrop-blur-xl dark:bg-black dark:text-slate-200">
              <a href="https://github.com/anoixa/image-hosting">
              已发布新的组件 ⚡️<span class="inline-flex items-center pl-2 text-black dark:text-white">查看
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none"
                  stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                  class="pl-0.5 text-black dark:text-white">
                  <path d="M5 12h14"></path>
                  <path d="m12 5 7 7-7 7"></path>
                </svg></span>
              </a>
            </div> -->

            <h2
              class="text-center text-xl mt-6 font-medium text-gray-900 dark:text-gray-50 sm:text-6xl cursor-default">
              image-hosting 在线图床
            </h2>

            <p
              class="mt-6 text-center text-lg leading-6 animate-pulse text-gray-600 dark:text-gray-200 cursor-default">
              快捷部署，一次上传，永久保存
            </p>

            <div class="mt-6 flex flex-col gap-4">
              <LoginComponent />
            </div>

            <div class="mt-10 flex text-sm font-medium">没有账户？</div>

            <div class="mt-5 flex gap-4">
              <router-link to="/register" class="min-w-48">
                <button
                  class="inline-flex bg-white  text-indigo-500 items-center justify-center rounded-xl text-md font-medium ring-offset-background
                  focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2disabled:pointer-events-none
                  disabled:opacity-50 bg-primary text-primary-foreground hover:bg-primary/90 h-10 px-4 py-2 w-full hover:bg-gray-100 transition-all
                  ease-in-out hover:-translate-y-1 duration-300 border-indigo-500 border shadow-indigo-500/50 shadow hover:opacity-90">
                  立即注册
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none"
                    stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                    class="pl-0.5">
                    <path d="M5 12h14"></path>
                    <path d="m12 5 7 7-7 7"></path>
                  </svg>
                </button>
              </router-link>
            </div>
          </div>
        </div>

      </div>
      <!-- 底部信息 -->
      <div class="mt-8 text-center text-sm text-gray-500 fixed bottom-0 w-full">
        <p>© 2024 Image-Hosting. All rights reserved.</p>
        <div class="mt-2 space-x-6">
          <a href="#" class="hover:text-indigo-600 transition-colors">关于我们</a>
          <a href="#" class="hover:text-indigo-600 transition-colors">服务条款</a>
          <a href="#" class="hover:text-indigo-600 transition-colors">隐私政策</a>
          <a href="#" class="hover:text-indigo-600 transition-colors">帮助中心</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import LoginComponent from '@/components/LoginComponent.vue'

</script>

<style lang="scss" scoped></style>
