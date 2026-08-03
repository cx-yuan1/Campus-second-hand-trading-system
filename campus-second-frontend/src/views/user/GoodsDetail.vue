<template>
  <div class="goods-detail">
    <el-card v-loading="loading">
      <template v-if="detail">
        <el-row :gutter="24">
          <el-col :span="10">
            <el-image
              :src="getImgUrl(currentImg || '')"
              fit="contain"
              class="main-img"
              @error="onImgError"
            />
            <div v-if="imgList.length > 1" class="thumb-list">
              <img
                v-for="(img, i) in imgList"
                :key="i"
                :src="getImgUrl(img)"
                :class="['thumb', currentImg === img ? 'active' : '']"
                @click="currentImg = img || ''"
              />
            </div>
          </el-col>
          <el-col :span="14">
            <h1 class="title">{{ detail.shangpinmingcheng }}</h1>
            <div class="price">¥{{ detail.price }}</div>
            <el-descriptions :column="1" border>
              <el-descriptions-item label="商品编号">{{ detail.shangpinbianhao }}</el-descriptions-item>
              <el-descriptions-item label="商品分类">{{ detail.shangpinfenlei }}</el-descriptions-item>
              <el-descriptions-item label="规格">{{ detail.guige || '-' }}</el-descriptions-item>
              <el-descriptions-item label="新旧程度">{{ detail.xinjiuchengdu || '-' }}</el-descriptions-item>
              <el-descriptions-item label="库存">{{ detail.alllimittimes }}</el-descriptions-item>
              <el-descriptions-item label="发布人">{{ detail.yonghuxingming }}</el-descriptions-item>
              <el-descriptions-item label="发布时间">{{ detail.fabushijian }}</el-descriptions-item>
            </el-descriptions>
            <div class="actions">
              <el-button type="primary" size="large" @click="handleBuy">立即购买</el-button>
              <el-button size="large" @click="handleAddCart">加入购物车</el-button>
              <el-button size="large" @click="handleCollect">收藏</el-button>
            </div>
          </el-col>
        </el-row>
        <el-divider />
        <div class="detail-content">
          <h3>商品详情</h3>
          <div v-html="detail.shangpinxiangqing" class="content-html" />
        </div>
      </template>
      <el-empty v-else-if="!loading" description="商品不存在" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getShangpinxinxiDetail } from '../../api/goods'
import { addCart } from '../../api/cart'
import { addStoreup } from '../../api/storeup'
import { getImgUrl } from '../../utils/imgUrl'
import { useUserStore } from '../../stores/user'
import { useCartStore } from '../../stores/cart'
import type { Shangpinxinxi } from '../../api/goods'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()
const detail = ref<Shangpinxinxi | null>(null)
const loading = ref(false)

const imgList = computed(() => {
  if (!detail.value?.tupian) return []
  return detail.value.tupian.split(',').filter(Boolean)
})

const currentImg = ref<string>('')

const onImgError = (e: Event) => {
  ;(e.target as HTMLImageElement).style.display = 'none'
}

const loadDetail = async () => {
  const id = Number(route.params.id)
  if (!id) return
  loading.value = true
  try {
    detail.value = await getShangpinxinxiDetail(id)
    if (detail.value?.tupian) {
      currentImg.value = detail.value.tupian.split(',')[0] || ''
    }
  } catch {
    detail.value = null
  } finally {
    loading.value = false
  }
}

const handleBuy = async () => {
  if (!detail.value) return
  const userid = userStore.userInfo?.id
  if (!userid) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    await addCart({ userid, goodid: detail.value.id, buynumber: 1 })
    await cartStore.refreshCount(userid)
    ElMessage.success('已加入购物车')
    router.push('/home/cart')
  } catch (e: unknown) {
    ElMessage.error((e as Error).message || '添加失败')
  }
}

const handleAddCart = async () => {
  if (!detail.value) return
  const userid = userStore.userInfo?.id
  if (!userid) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    await addCart({ userid, goodid: detail.value.id, buynumber: 1 })
    await cartStore.refreshCount(userid)
    ElMessage.success('已加入购物车')
  } catch (e: unknown) {
    ElMessage.error((e as Error).message || '添加失败')
  }
}

const handleCollect = async () => {
  if (!detail.value) return
  const userid = userStore.userInfo?.id
  if (!userid) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    await addStoreup({ userid, refid: detail.value.id })
    ElMessage.success('收藏成功')
  } catch (e: unknown) {
    ElMessage.error((e as Error).message || '收藏失败')
  }
}

onMounted(loadDetail)
</script>

<style scoped>
.goods-detail {
  padding: 0;
}

.main-img {
  width: 100%;
  height: 360px;
  border-radius: 8px;
}

.thumb-list {
  display: flex;
  gap: 8px;
  margin-top: 12px;
}

.thumb {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
  cursor: pointer;
  border: 2px solid transparent;
}

.thumb.active {
  border-color: var(--primary-color);
}

.title {
  margin-bottom: 12px;
  font-size: 24px;
}

.price {
  font-size: 28px;
  color: var(--primary-color);
  font-weight: bold;
  margin-bottom: 20px;
}

.actions {
  margin-top: 24px;
  display: flex;
  gap: 12px;
}

.detail-content {
  margin-top: 24px;
}

.content-html {
  margin-top: 12px;
  line-height: 1.8;
}

.content-html :deep(img) {
  max-width: 100%;
}
</style>
