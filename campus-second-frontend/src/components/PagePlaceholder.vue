<template>
  <el-card class="page-placeholder">
    <h2>{{ title }}</h2>
    <p>{{ desc }}</p>
  </el-card>
</template>

<script setup lang="ts">
defineProps<{
  title: string
  desc?: string
}>()
</script>

<style scoped>
.page-placeholder {
  background: var(--card-bg);
}
.page-placeholder h2 {
  margin-bottom: 12px;
  color: var(--primary-color);
}
.page-placeholder p {
  color: #666;
}
</style>
