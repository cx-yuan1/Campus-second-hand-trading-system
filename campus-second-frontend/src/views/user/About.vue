<template>
  <div class="about-page">
    <el-card v-loading="loading">
      <template v-if="detail">
        <h1 class="title">{{ detail.title }}</h1>
        <div v-if="detail.subtitle" class="subtitle">{{ detail.subtitle }}</div>
        <el-row v-if="hasPictures" :gutter="16" class="pictures">
          <el-col v-if="detail.picture1" :span="8">
            <img :src="getImgUrl(detail.picture1)" class="about-img" @error="onImgError" />
          </el-col>
          <el-col v-if="detail.picture2" :span="8">
            <img :src="getImgUrl(detail.picture2)" class="about-img" @error="onImgError" />
          </el-col>
          <el-col v-if="detail.picture3" :span="8">
            <img :src="getImgUrl(detail.picture3)" class="about-img" @error="onImgError" />
          </el-col>
        </el-row>
        <div v-html="detail.content" class="content" />
      </template>
      <el-empty v-else-if="!loading" description="暂无内容" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { getAboutusDetail } from '../../api/aboutus'
import { getImgUrl } from '../../utils/imgUrl'
import type { AboutusItem } from '../../api/aboutus'

const detail = ref<AboutusItem | null>(null)
const loading = ref(false)

const hasPictures = computed(() =>
  !!(detail.value?.picture1 || detail.value?.picture2 || detail.value?.picture3)
)

const onImgError = (e: Event) => {
  ;(e.target as HTMLImageElement).style.display = 'none'
}

onMounted(async () => {
  loading.value = true
  try {
    detail.value = await getAboutusDetail()
  } catch {
    detail.value = null
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.about-page {
  padding: 0;
}

.title {
  margin-bottom: 8px;
}

.subtitle {
  color: #909399;
  margin-bottom: 24px;
}

.pictures {
  margin-bottom: 24px;
}

.about-img {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 8px;
}

.content {
  line-height: 1.8;
  white-space: pre-wrap;
}

.content :deep(img) {
  max-width: 100%;
}
</style>
