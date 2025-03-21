<template>
  <div>
    <!-- Bg -->
    <div
      class="absolute inset-0 -z-10 h-full w-full bg-white [background:linear-gradient(to_bottom_right,#63e_20%,#fff_100%)] animate-pulse">
    </div>
    <!-- 顶部 -->
     <div class="flex justify-between w-full h-1/6">
      <h2 class="text-4xl font-bold p-4 w-full flex">Placeholder</h2>
     </div>
    <!-- 一左一右 -->
    <div class="flex justify-between w-full h-full">
      <div class="w-1/2 flex justify-center items-center pt-4">
        <!-- 注册组件 -->
        <RegisterLeftComponent />
      </div>
      <div class="w-1/2 flex justify-center items-center">
        <!-- 图片组件 -->
        <!-- <img src="https://via.placeholder.com/400" alt="Placeholder Image" class="w-full h-full object-cover"> -->
         <h2 class="text-4xl font-bold">Placeholder</h2>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import RegisterLeftComponent from '@/components/RegisterLeftComponent.vue';


</script>

<style lang="scss" scoped>
.animate-pulse {
  animation: pulse 10s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}
</style>
