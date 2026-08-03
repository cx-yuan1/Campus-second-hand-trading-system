<template>
  <div class="collect-page">
    <el-card>
      <h3>我的收藏</h3>
      <el-row v-loading="loading" :gutter="16">
        <el-col v-for="item in list" :key="item.id" :xs="12" :sm="8" :md="6">
          <el-card class="collect-card" shadow="hover">
            <img :src="getImgUrl(item.picture)" class="collect-img" @error="onImgError" />
            <div class="collect-name">{{ item.name }}</div>
            <el-button type="danger" link size="small" @click="handleDelete(item.id)">取消收藏</el-button>
            <el-button type="primary" link size="small" @click="$router.push(`/home/goods/${item.refid}`)">
              查看
            </el-button>
          </el-card>
        </el-col>
      </el-row>
      <el-empty v-if="!loading && !list.length" description="暂无收藏" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getStoreupList, deleteStoreup } from '../../api/storeup'
import { getImgUrl } from '../../utils/imgUrl'
import { useUserStore } from '../../stores/user'
import type { StoreupItem } from '../../api/storeup'

const userStore = useUserStore()
const list = ref<StoreupItem[]>([])
const loading = ref(false)

const onImgError = (e: Event) => {
  ;(e.target as HTMLImageElement).style.display = 'none'
}

const loadList = async () => {
  const userid = userStore.userInfo?.id
  if (!userid) return
  loading.value = true
  try {
    list.value = (await getStoreupList(userid)) || []
  } catch {
    list.value = []
  } finally {
    loading.value = false
  }
}

const handleDelete = async (id: number) => {
  const userid = userStore.userInfo?.id
  if (!userid) return
  try {
    await deleteStoreup(id, userid)
    ElMessage.success('已取消收藏')
    loadList()
  } catch (e: unknown) {
    ElMessage.error((e as Error).message || '操作失败')
  }
}

onMounted(loadList)
</script>

<style scoped>
.collect-card {
  margin-bottom: 16px;
}

.collect-img {
  width: 100%;
  height: 140px;
  object-fit: cover;
  border-radius: 4px;
}

.collect-name {
  margin: 8px 0;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
