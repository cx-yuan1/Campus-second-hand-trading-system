<template>
  <div class="page">
    <el-card>
      <el-form :inline="true" class="filter-form">
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="商品名称/编号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadList">搜索</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="loading" :data="list">
        <el-table-column prop="qiugoubianhao" label="求购编号" min-width="140" />
        <el-table-column prop="shangpinmingcheng" label="商品名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="shangpinfenlei" label="分类" min-width="100" />
        <el-table-column prop="qiugoujiage" label="求购价格" min-width="100" />
        <el-table-column prop="yonghuzhanghao" label="发布账号" min-width="100" />
        <el-table-column prop="qiugoushijian" label="求购时间" min-width="110" />
        <el-table-column label="操作" min-width="150">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDialog(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
        <el-table-column label="" />
      </el-table>
      <el-pagination
        v-model:current-page="query.page"
        v-model:page-size="query.limit"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        style="margin-top: 16px"
        @current-change="loadList"
        @size-change="loadList"
      />
      <el-empty v-if="!loading && !list.length" description="暂无求购信息" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="editId ? '编辑求购' : '新增求购'" width="500px" @close="editId = null">
      <el-form :model="form" label-width="100px">
        <el-form-item label="商品名称" required>
          <el-input v-model="form.shangpinmingcheng" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="分类">
          <el-input v-model="form.shangpinfenlei" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="规格">
          <el-input v-model="form.guige" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="求购价格">
          <el-input-number v-model="form.qiugoujiage" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="求购详情">
          <el-input v-model="form.qiugouxiangqing" type="textarea" :rows="2" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="发布账号">
          <el-input v-model="form.yonghuzhanghao" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="发布姓名">
          <el-input v-model="form.yonghuxingming" placeholder="请输入" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getQiugouxinxiList,
  addQiugouxinxi,
  updateQiugouxinxi,
  deleteQiugouxinxi,
} from '../../api/qiugou'
import type { QiugouxinxiItem } from '../../api/qiugou'

const list = ref<QiugouxinxiItem[]>([])
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const submitLoading = ref(false)
const editId = ref<number | null>(null)
const query = reactive({ page: 1, limit: 10, keyword: '' })
const form = reactive<Partial<QiugouxinxiItem>>({
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
      page: query.page,
      limit: query.limit,
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

const openDialog = (row?: QiugouxinxiItem) => {
  editId.value = row?.id ?? null
  Object.assign(form, row || {
    shangpinmingcheng: '',
    shangpinfenlei: '',
    guige: '',
    qiugoujiage: 0,
    qiugouxiangqing: '',
    yonghuzhanghao: '',
    yonghuxingming: '',
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!form.shangpinmingcheng) {
    ElMessage.warning('请输入商品名称')
    return
  }
  submitLoading.value = true
  try {
    if (editId.value) {
      await updateQiugouxinxi({ ...form, id: editId.value })
      ElMessage.success('更新成功')
    } else {
      await addQiugouxinxi(form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadList()
  } catch (e: any) {
    ElMessage.error(e?.message || '操作失败')
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (id: number) => {
  await ElMessageBox.confirm('确定删除该求购信息？', '提示', { type: 'warning' })
  try {
    await deleteQiugouxinxi(id)
    ElMessage.success('删除成功')
    loadList()
  } catch (e: any) {
    ElMessage.error(e?.message || '删除失败')
  }
}

onMounted(loadList)
</script>

<style scoped>
.page { padding: 0; }
.filter-form { margin-bottom: 16px; }
</style>
