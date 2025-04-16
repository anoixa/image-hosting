<template>
  <div class="min-h-screen w-full bg-gradient-to-br from-indigo-500/10 to-purple-500/10 flex">
    <div class="w-64 flex-shrink-0">
      <WorkplaceLeftbarComponent />
    </div>

    <div class="flex-grow p-8">
      <router-view></router-view>
    </div>
  </div>
</template>

<script setup lang="ts">
import WorkplaceLeftbarComponent from '@/components/WorkplaceLeftbarComponent.vue';
</script>

<style scoped>
/* You can add any specific styles for this view here */
</style>
