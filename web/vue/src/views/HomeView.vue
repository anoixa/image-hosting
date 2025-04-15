<template>
  <!-- Hero -->
  <div
    class="relative overflow-hidden before:absolute before:top-0 before:left-0 before:right-0 before:bg-[url('https://preline.co/assets/svg/examples/polygon-bg-element.svg')] before:bg-no-repeat before:bg-center before:bg-cover before:h-full before:-z-[1] h-screen">
    <div
      class="max-w-[85rem] mx-auto px-4 sm:px-6 lg:px-8 pt-24 pb-10 flex flex-col justify-center items-center h-full">
      <!-- Announcement Banner -->
      <div class="flex justify-center mb-5">
        <a class="inline-flex items-center gap-x-2 bg-white border border-gray-200 text-sm text-gray-800 p-1 ps-3 rounded-full transition hover:border-gray-300 focus:outline-none focus:border-gray-300"
          href="#">
          PRO 版发布 - 成为高级会员
          <span
            class="py-1.5 px-2.5 inline-flex justify-center items-center gap-x-2 rounded-full bg-gray-200 font-semibold text-sm text-gray-600">
            <svg class="shrink-0 size-4" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"
              fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="m9 18 6-6-6-6" />
            </svg>
          </span>
        </a>
      </div>
      <!-- End Announcement Banner -->

      <!-- Title -->
      <div class="mt-5 max-w-2xl text-center mx-auto">
        <h1 class="block font-bold text-gray-800 text-4xl md:text-5xl lg:text-6xl">
          image-hosting<span
            class="bg-clip-text bg-gradient-to-tl from-blue-600 to-violet-600 text-transparent">图床</span>
        </h1>
      </div>
      <!-- End Title -->

      <div class="mt-5 max-w-3xl text-center mx-auto">
        <p class="text-lg text-gray-600">
          Element UIX 是一个基于 Vue 的开源 UI 组件套件，内含预构建组件、现成示例
        </p>
      </div>

      <!-- Buttons -->
      <div class="mt-8 gap-3 flex justify-center">
        <router-link
          class="inline-flex justify-center items-center gap-x-3 text-center bg-gradient-to-tl from-blue-600 to-violet-600 hover:from-violet-600 hover:to-blue-600 border border-transparent text-white text-sm font-medium rounded-md focus:outline-none focus:from-violet-600 focus:to-blue-600 py-3 px-4"
          to="/auth/login">
          开始体验
          <svg class="shrink-0 size-4" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"
            fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="m9 18 6-6-6-6" />
          </svg>
        </router-link>
        <button type="button"
          class="relative group p-2 ps-3 inline-flex items-center gap-x-2 text-sm font-mono rounded-lg border border-gray-200 bg-white text-gray-800 shadow-sm hover:bg-gray-50 focus:outline-none focus:bg-gray-50 disabled:opacity-50 disabled:pointer-events-none">
          $ npm i element-uix
          <span class="flex justify-center items-center bg-gray-200 rounded-md size-7">
            <svg class="shrink-0 size-4 group-hover:rotate-6 transition" xmlns="http://www.w3.org/2000/svg" width="24"
              height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
              stroke-linejoin="round">
              <rect width="8" height="4" x="8" y="2" rx="1" ry="1" />
              <path d="M16 4h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2" />
            </svg>
          </span>
        </button>
      </div>
      <!-- End Buttons -->

      <div class="mt-5 flex justify-center items-center gap-x-1 sm:gap-x-3">
        <span class="text-sm text-gray-600">包管理 :</span>
        <span class="text-sm font-bold text-gray-900">npm</span>
        <svg class="size-5 text-gray-300" width="16" height="16" viewBox="0 0 16 16" fill="none"
          xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
          <path d="M6 13L10 3" stroke="currentColor" stroke-linecap="round" />
        </svg>
        <a class="inline-flex items-center gap-x-1 text-sm text-blue-600 decoration-2 hover:underline focus:outline-none focus:underline font-medium"
          href="#">
          使用文档
          <svg class="shrink-0 size-4" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"
            fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="m9 18 6-6-6-6" />
          </svg>
        </a>
      </div>
    </div>
  </div>
  <!-- End Hero -->
  <div>
    <router-link to="/">首页</router-link>
    <router-link to="/auth/login">登录</router-link>
    <router-link to="/register">注册</router-link>
    <router-link to="/about">关于</router-link>
  </div>
</template>

<style scoped>
/* 确保背景图片覆盖整个视口 */
.h-screen {
  height: 100vh;
}
</style>
