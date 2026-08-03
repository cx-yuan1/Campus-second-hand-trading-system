<template>
  <div class="page">
    <el-card>
      <el-button type="primary" @click="openDialog()" style="margin-bottom: 16px">新增公告</el-button>
      <el-table v-loading="loading" :data="list">
        <el-table-column prop="title" label="标题" min-width="180" />
        <el-table-column prop="introduction" label="简介" min-width="200" show-overflow-tooltip />
        <el-table-column prop="addtime" label="发布时间" min-width="170" />
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
      <el-empty v-if="!loading && !list.length" description="暂无公告" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="editId ? '编辑公告' : '新增公告'" width="600px" @close="editId = null">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题" required>
          <el-input v-model="form.title" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="form.introduction" type="textarea" :rows="2" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="图片">
          <ImageUpload v-model="form.picture" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="5" placeholder="请输入" />
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
import { getNewsList, addNews, updateNews, deleteNews } from '../../api/news'
import ImageUpload from '../../components/ImageUpload.vue'
import type { NewsItem } from '../../api/news'

const list = ref<NewsItem[]>([])
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const submitLoading = ref(false)
const editId = ref<number | null>(null)
const query = reactive({ page: 1, limit: 10 })
const form = reactive<Partial<NewsItem>>({
  title: '',
  introduction: '',
  picture: '',
  content: '',
})

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

const openDialog = (row?: NewsItem) => {
  editId.value = row?.id ?? null
  Object.assign(form, row || { title: '', introduction: '', picture: '', content: '' })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!form.title) {
    ElMessage.warning('请输入标题')
    return
  }
  submitLoading.value = true
  try {
    if (editId.value) {
      await updateNews({ ...form, id: editId.value })
      ElMessage.success('更新成功')
    } else {
      await addNews(form)
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
  await ElMessageBox.confirm('确定删除该公告？', '提示', { type: 'warning' })
  try {
    await deleteNews(id)
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
</style>
