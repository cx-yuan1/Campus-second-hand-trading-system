<template>
  <div class="home">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="banner-card">
          <div v-if="bannerList.length" class="banner-aspect">
            <el-carousel class="banner-carousel" height="320px" :interval="4000" autoplay arrow="always">
              <el-carousel-item v-for="item in bannerList" :key="item.id">
                <div class="banner-wrap">
                  <img :src="getImgUrl(item.value)" class="banner-img" :alt="item.name" @error="onImgError" />
                </div>
              </el-carousel-item>
            </el-carousel>
          </div>
          <div v-else class="banner-placeholder">
            <el-icon class="banner-icon"><Picture /></el-icon>
            <p>暂无轮播图</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="16">
        <el-card>
          <template #header>
            <span>商品推荐</span>
            <el-button type="primary" link style="float: right" @click="$router.push('/home/goods')">
              更多
            </el-button>
          </template>
          <el-row v-loading="goodsLoading" :gutter="12">
            <el-col v-for="item in goodsList" :key="item.id" :span="6">
              <el-card class="goods-card" shadow="hover" @click="$router.push(`/home/goods/${item.id}`)">
                <img :src="getImgUrl(getFirstImg(item.tupian))" class="goods-img" @error="onImgError" />
                <div class="goods-name">{{ item.shangpinmingcheng }}</div>
                <div class="goods-price">¥{{ item.price }}</div>
              </el-card>
            </el-col>
          </el-row>
          <el-empty v-if="!goodsLoading && !goodsList.length" description="暂无商品" />
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>公告信息</span>
            <el-button type="primary" link style="float: right" @click="$router.push('/home/news')">
              更多
            </el-button>
          </template>
          <div v-loading="newsLoading" class="news-list">
            <div
              v-for="item in newsList"
              :key="item.id"
              class="news-item"
              @click="$router.push(`/home/news/${item.id}`)"
            >
              <span class="news-title">{{ item.title }}</span>
            </div>
          </div>
          <el-empty v-if="!newsLoading && !newsList.length" description="暂无公告" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getBannerList } from '../../api/config'
import { getShangpinxinxiList } from '../../api/goods'
import { getNewsList } from '../../api/news'
import { getImgUrl } from '../../utils/imgUrl'
import type { ConfigItem } from '../../api/config'
import type { Shangpinxinxi } from '../../api/goods'
import type { NewsItem } from '../../api/news'

const bannerList = ref<ConfigItem[]>([])
const goodsList = ref<Shangpinxinxi[]>([])
const newsList = ref<NewsItem[]>([])
const goodsLoading = ref(false)
const newsLoading = ref(false)

const getFirstImg = (tupian: string | undefined) => {
  if (!tupian) return ''
  return tupian.split(',')[0]
}

const onImgError = (e: Event) => {
  ;(e.target as HTMLImageElement).style.display = 'none'
}

const loadBanner = async () => {
  try {
    const data = await getBannerList()
    bannerList.value = data || []
  } catch {
    bannerList.value = []
  }
}

const loadGoods = async () => {
  goodsLoading.value = true
  try {
    const data = await getShangpinxinxiList({ page: 1, limit: 8 })
    goodsList.value = data?.records || []
  } catch {
    goodsList.value = []
  } finally {
    goodsLoading.value = false
  }
}

const loadNews = async () => {
  newsLoading.value = true
  try {
    const data = await getNewsList({ page: 1, limit: 5 })
    newsList.value = data?.records || []
  } catch {
    newsList.value = []
  } finally {
    newsLoading.value = false
  }
}

onMounted(() => {
  loadBanner()
  loadGoods()
  loadNews()
})
</script>

<style scoped>
.home {
  padding: 0;
}

.banner-card {
  background: var(--card-bg);
  overflow: hidden;
}

.banner-card :deep(.el-card__body) {
  padding: 0;
}

.banner-aspect {
  border-radius: 8px;
  overflow: hidden;
}

@media (max-width: 768px) {
  .banner-aspect :deep(.el-carousel__container) {
    height: 200px !important;
  }
}

.banner-carousel :deep(.el-carousel__item) {
  overflow: hidden;
}

.banner-wrap {
  width: 100%;
  height: 100%;
  overflow: hidden;
  background: #f5f7fa;
}

.banner-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
  display: block;
}

.banner-placeholder {
  height: 320px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f0f4f8 0%, #e8eef5 100%);
  border-radius: 8px;
  color: #999;
}

.banner-icon {
  font-size: 48px;
  margin-bottom: 12px;
}

.goods-card {
  cursor: pointer;
  margin-bottom: 12px;
}

.goods-img {
  width: 100%;
  height: 120px;
  object-fit: cover;
  border-radius: 4px;
}

.goods-name {
  margin-top: 8px;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.goods-price {
  margin-top: 4px;
  color: var(--primary-color);
  font-weight: bold;
}

.news-list {
  min-height: 120px;
}

.news-item {
  padding: 8px 0;
  border-bottom: 1px solid #eee;
  cursor: pointer;
}

.news-item:hover {
  color: var(--primary-color);
}

.news-title {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: block;
}
</style>
