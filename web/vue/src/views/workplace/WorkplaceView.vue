<template>
  <div class="min-h-screen w-full bg-gradient-to-br from-indigo-500/10 to-purple-500/10">

    <div class="w-full fixed top-0 left-0 right-0 h-16 bg-opacity-50 shadow-md z-10">
      <WorkplaceTopbarComponent />
    </div>

    <div class="fixed left-0 bottom-0 w-1/12 min-w-min z-20 bg-white shadow-lg overflow-y-auto top-16 mt-1 rounded-md">
      <WorkplaceLeftbarComponent />
    </div>

    <div class="absolute top-2 left-50 pl-10 right-0 bottom-0 overflow-y-auto p-auto w-11/12">
      <router-view v-slot="{ Component }">
            <component :is="Component" />
      </router-view>
    </div>

  </div>
</template>

<script setup lang="ts">
import WorkplaceLeftbarComponent from '@/components/WorkplaceLeftbarComponent.vue';
import WorkplaceTopbarComponent from '@/components/WorkplaceTopbarComponent.vue';
</script>

<style scoped></style>
