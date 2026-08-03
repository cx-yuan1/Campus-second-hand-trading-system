<template>
  <div class="news-page">
    <el-card>
      <h3>公告列表</h3>
      <div v-loading="loading" class="news-list">
        <div
          v-for="item in list"
          :key="item.id"
          class="news-item"
          @click="$router.push(`/home/news/${item.id}`)"
        >
          <div class="news-img-wrap">
            <img
              v-if="item.picture && !imgError[item.id]"
              :src="getImgUrl(item.picture)"
              class="news-img"
              @error="onImgError(item.id)"
            />
            <div v-else class="news-img-placeholder">
              <el-icon :size="32"><Picture /></el-icon>
              <span>暂无图片</span>
            </div>
          </div>
          <div class="news-info">
            <div class="news-title">{{ item.title }}</div>
            <div class="news-intro">{{ item.introduction }}</div>
            <div v-if="item.addtime" class="news-time">{{ formatTime(item.addtime) }}</div>
          </div>
        </div>
      </div>
      <el-pagination
        :current-page="query.page"
        :total="total"
        :page-size="query.limit"
        layout="total, prev, pager, next"
        style="margin-top: 20px; justify-content: center"
        @current-change="onPageChange"
      />
      <el-empty v-if="!loading && !list.length" description="暂无公告" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Picture } from '@element-plus/icons-vue'
import { getNewsList } from '../../api/news'
import { getImgUrl } from '../../utils/imgUrl'
import type { NewsItem } from '../../api/news'

const list = ref<NewsItem[]>([])
const total = ref(0)
const loading = ref(false)
const imgError = ref<Record<number, boolean>>({})

const query = reactive({ page: 1, limit: 10 })

const onImgError = (id: number) => {
  imgError.value[id] = true
}

const formatTime = (t: string | undefined) => {
  if (!t) return ''
  return t.replace('T', ' ').substring(0, 16)
}

const onPageChange = (p: number) => {
  query.page = p
  loadList()
}

const loadList = async () => {
  loading.value = true
  try {
    const data = await getNewsList({ page: query.page, limit: query.limit })
    list.value = data?.records || []
    total.value = data?.total || 0
  } catch {
    list.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

onMounted(loadList)
</script>

<style scoped>
.news-page {
  padding: 0;
  max-width: 900px;
  margin: 0 auto;
}

.news-list {
  min-height: 200px;
}

.news-item {
  display: flex;
  gap: 20px;
  padding: 20px 0;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  align-items: center;
}

.news-item:hover {
  color: var(--primary-color);
}

.news-item:hover .news-img,
.news-item:hover .news-img-placeholder {
  transform: scale(1.02);
}

.news-img-wrap {
  flex-shrink: 0;
  width: 180px;
  height: 120px;
  border-radius: 8px;
  overflow: hidden;
  background: #f5f7fa;
}

.news-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.2s;
}

.news-img-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
  font-size: 12px;
  gap: 4px;
  transition: transform 0.2s;
}

.news-info {
  flex: 1;
  min-width: 0;
}

.news-title {
  font-weight: 600;
  font-size: 16px;
  margin-bottom: 8px;
}

.news-intro {
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.news-time {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}
</style>
