<template>
  <div class="h-full w-full relative flex items-center justify-center">
    <!-- 背景 -->
    <div
      class="absolute inset-0 -z-10 h-full w-full bg-white [background:radial-gradient(125%_125%_at_50%_5%,#63e_5%,#fff_100%)]">
    </div>
    <div class="w-full max-w-[500px]">
      <FindPasswordComponent />
    </div>

  </div>
</template>

<script lang="ts" setup>
import FindPasswordComponent from '@/components/FindPasswordComponent.vue';


</script>

<style lang="scss" scoped></style>
