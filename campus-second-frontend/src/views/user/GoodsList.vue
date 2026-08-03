<template>
  <div class="goods-list">
    <el-card>
      <el-form :inline="true" class="filter-form">
        <el-form-item label="分类">
          <el-select v-model="query.shangpinfenlei" placeholder="全部分类" clearable style="width: 120px">
            <el-option
              v-for="item in fenleiList"
              :key="item.id"
              :label="item.shangpinfenlei"
              :value="item.shangpinfenlei"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="商品名称/编号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadList">搜索</el-button>
        </el-form-item>
      </el-form>
      <el-row v-loading="loading" :gutter="16">
        <el-col v-for="item in list" :key="item.id" :xs="12" :sm="8" :md="6">
          <el-card class="goods-card" shadow="hover" @click="$router.push(`/home/goods/${item.id}`)">
            <img :src="getImgUrl(getFirstImg(item.tupian))" class="goods-img" @error="onImgError" />
            <div class="goods-name">{{ item.shangpinmingcheng }}</div>
            <div class="goods-meta">{{ item.xinjiuchengdu }} · ¥{{ item.price }}</div>
          </el-card>
        </el-col>
      </el-row>
      <el-pagination
        v-model:current-page="query.page"
        v-model:page-size="query.limit"
        :total="total"
        :page-sizes="[12, 24, 48]"
        layout="total, sizes, prev, pager, next"
        style="margin-top: 20px; justify-content: center"
        @current-change="loadList"
        @size-change="loadList"
      />
      <el-empty v-if="!loading && !list.length" description="暂无商品" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { getShangpinfenleiList, getShangpinxinxiList } from '../../api/goods'
import { getImgUrl } from '../../utils/imgUrl'
import type { Shangpinfenlei, Shangpinxinxi } from '../../api/goods'

const fenleiList = ref<Shangpinfenlei[]>([])
const list = ref<Shangpinxinxi[]>([])
const total = ref(0)
const loading = ref(false)

const query = reactive({
  page: 1,
  limit: 12,
  shangpinfenlei: '',
  keyword: '',
})

const getFirstImg = (tupian: string | undefined) => {
  if (!tupian) return ''
  return tupian.split(',')[0]
}

const onImgError = (e: Event) => {
  ;(e.target as HTMLImageElement).style.display = 'none'
}

const loadList = async () => {
  loading.value = true
  try {
    const data = await getShangpinxinxiList({
      page: query.page,
      limit: query.limit,
      shangpinfenlei: query.shangpinfenlei || undefined,
      keyword: query.keyword || undefined,
    })
    list.value = data?.records || []
    total.value = data?.total || 0
  } catch {
    list.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

watch(
  () => [query.shangpinfenlei, query.keyword],
  () => {
    query.page = 1
    loadList()
  }
)

onMounted(async () => {
  try {
    fenleiList.value = (await getShangpinfenleiList()) || []
  } catch {
    fenleiList.value = []
  }
  loadList()
})
</script>

<style scoped>
.goods-list {
  padding: 0;
}

.filter-form {
  margin-bottom: 20px;
}

.goods-card {
  cursor: pointer;
  margin-bottom: 16px;
}

.goods-img {
  width: 100%;
  height: 160px;
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

.goods-meta {
  margin-top: 4px;
  font-size: 12px;
  color: #909399;
}

.goods-meta::before {
  content: '';
}
</style>
