<template>
  <div class="news-detail">
    <el-card v-loading="loading" class="detail-card">
      <template v-if="detail">
        <div class="back-link" @click="$router.back()">
          <el-icon><ArrowLeft /></el-icon>
          <span>返回公告列表</span>
        </div>
        <h1 class="title">{{ detail.title }}</h1>
        <div v-if="detail.introduction" class="intro">{{ detail.introduction }}</div>
        <div v-if="detail.addtime" class="meta">{{ formatTime(detail.addtime) }}</div>
        <div v-if="detail.picture && !imgError" class="cover">
          <img :src="getImgUrl(detail.picture)" @error="onImgError" />
        </div>
        <div v-else-if="detail.picture" class="cover-placeholder">
          <el-icon :size="48"><Picture /></el-icon>
          <span>图片加载失败</span>
        </div>
        <div class="content" v-html="detail.content" />
      </template>
      <el-empty v-else-if="!loading" description="公告不存在" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ArrowLeft, Picture } from '@element-plus/icons-vue'
import { useRoute } from 'vue-router'
import { getNewsDetail } from '../../api/news'
import { getImgUrl } from '../../utils/imgUrl'
import type { NewsItem } from '../../api/news'

const route = useRoute()
const detail = ref<NewsItem | null>(null)
const loading = ref(false)
const imgError = ref(false)

const onImgError = () => {
  imgError.value = true
}

const formatTime = (t: string | undefined) => {
  if (!t) return ''
  return t.replace('T', ' ').substring(0, 16)
}

onMounted(async () => {
  const id = Number(route.params.id)
  if (!id) return
  loading.value = true
  try {
    detail.value = await getNewsDetail(id)
  } catch {
    detail.value = null
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.news-detail {
  max-width: 800px;
  margin: 0 auto;
}

.detail-card {
  padding: 32px 40px;
}

.back-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #606266;
  font-size: 14px;
  cursor: pointer;
  margin-bottom: 24px;
  padding: 4px 0;
}

.back-link:hover {
  color: var(--primary-color);
}

.title {
  font-size: 26px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 12px 0;
  line-height: 1.4;
}

.intro {
  font-size: 15px;
  color: #606266;
  line-height: 1.6;
  margin-bottom: 12px;
}

.meta {
  font-size: 13px;
  color: #909399;
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
}

.cover {
  margin-bottom: 28px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.cover img {
  width: 100%;
  display: block;
  object-fit: cover;
  max-height: 400px;
}

.cover-placeholder {
  margin-bottom: 28px;
  height: 200px;
  background: #f5f7fa;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #c0c4cc;
  font-size: 14px;
}

.content {
  line-height: 1.9;
  font-size: 15px;
  color: #303133;
}

.content :deep(p) {
  margin: 0 0 12px 0;
}

.content :deep(p:last-child) {
  margin-bottom: 0;
}

.content :deep(img) {
  max-width: 100%;
  border-radius: 8px;
  margin: 12px 0;
}
</style>
