<template>
  <div class="min-h-screen w-full bg-gradient-to-br from-indigo-500/10 to-purple-500/10 flex">
    <div class="flex-shrink-0 h-screen w-1/12 min-w-min">
      <WorkplaceLeftbarComponent />
      <div class="flex-1 overflow-y-auto">
      </div>
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
