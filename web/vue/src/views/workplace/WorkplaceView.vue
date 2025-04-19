<template>
  <div class="min-h-screen w-full bg-gradient-to-br from-indigo-500/10 to-purple-500/10 flex pt-16">

    <div class="w-full fixed top-0 left-0 right-0 z-10">
      <WorkplaceTopbarComponent />
    </div>

    <div class="flex-shrink-0 h-auto w-1/12 min-w-min flex-wrap">
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
import WorkplaceTopbarComponent from '@/components/WorkplaceTopbarComponent.vue';
</script>

<style scoped>
</style>
