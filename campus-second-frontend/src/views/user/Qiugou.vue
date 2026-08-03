<template>
  <div class="qiugou-page">
    <el-card>
      <el-button type="primary" @click="dialogVisible = true">发布求购</el-button>
      <el-input
        v-model="keyword"
        placeholder="搜索商品名称"
        style="width: 200px; margin-left: 12px"
        clearable
        @keyup.enter="loadList"
      />
      <el-button style="margin-left: 8px" @click="loadList">搜索</el-button>
      <el-table v-loading="loading" :data="list" style="margin-top: 20px">
        <el-table-column prop="qiugoubianhao" label="求购编号" min-width="140" />
        <el-table-column prop="shangpinmingcheng" label="商品名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="shangpinfenlei" label="分类" min-width="100" />
        <el-table-column prop="qiugoujiage" label="求购价格" min-width="100">
          <template #default="{ row }">¥{{ row.qiugoujiage }}</template>
        </el-table-column>
        <el-table-column prop="yonghuxingming" label="发布人" min-width="100" />
        <el-table-column prop="qiugoushijian" label="求购时间" min-width="120" />
        <el-table-column label="" />
      </el-table>
      <el-pagination
        v-model:current-page="page"
        :total="total"
        :page-size="limit"
        layout="total, prev, pager, next"
        style="margin-top: 20px; justify-content: center"
        @current-change="loadList"
      />
    </el-card>
    <el-dialog v-model="dialogVisible" title="发布求购" width="500px" @close="resetForm">
      <el-form :model="form" label-width="80px">
        <el-form-item label="商品名称" required>
          <el-input v-model="form.shangpinmingcheng" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="商品分类">
          <el-input v-model="form.shangpinfenlei" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="规格">
          <el-input v-model="form.guige" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="求购价格" required>
          <el-input-number v-model="form.qiugoujiage" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="求购详情">
          <el-input v-model="form.qiugouxiangqing" type="textarea" :rows="4" placeholder="请输入" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getQiugouxinxiList, addQiugouxinxi } from '../../api/qiugou'
import { useUserStore } from '../../stores/user'
import type { QiugouxinxiItem } from '../../api/qiugou'

const userStore = useUserStore()
const list = ref<QiugouxinxiItem[]>([])
const total = ref(0)
const loading = ref(false)
const page = ref(1)
const limit = ref(10)
const keyword = ref('')
const dialogVisible = ref(false)
const submitLoading = ref(false)

const form = reactive({
  shangpinmingcheng: '',
  shangpinfenlei: '',
  guige: '',
  qiugoujiage: 0,
  qiugouxiangqing: '',
  yonghuzhanghao: '',
  yonghuxingming: '',
})

const loadList = async () => {
  loading.value = true
  try {
    const data = await getQiugouxinxiList({
      page: page.value,
      limit: limit.value,
      keyword: keyword.value || undefined,
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

const resetForm = () => {
  form.shangpinmingcheng = ''
  form.shangpinfenlei = ''
  form.guige = ''
  form.qiugoujiage = 0
  form.qiugouxiangqing = ''
}

const handleSubmit = async () => {
  if (!form.shangpinmingcheng) {
    ElMessage.warning('请输入商品名称')
    return
  }
  submitLoading.value = true
  try {
    await addQiugouxinxi({
      ...form,
      yonghuzhanghao: userStore.userInfo?.username || '',
      yonghuxingming: userStore.userInfo?.username || '',
    })
    ElMessage.success('发布成功')
    dialogVisible.value = false
    loadList()
  } catch (e: unknown) {
    ElMessage.error((e as Error).message || '发布失败')
  } finally {
    submitLoading.value = false
  }
}

onMounted(loadList)
</script>
