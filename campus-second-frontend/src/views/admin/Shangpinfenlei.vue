<template>
  <div class="page">
    <el-card>
      <el-button type="primary" @click="openDialog()" style="margin-bottom: 16px">新增分类</el-button>
      <el-table v-loading="loading" :data="list">
        <el-table-column type="index" label="序号" min-width="80" />
        <el-table-column prop="shangpinfenlei" label="分类名称" min-width="120" />
        <el-table-column label="操作" min-width="150">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDialog(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
        <el-table-column label="" />
      </el-table>
      <el-empty v-if="!loading && !list.length" description="暂无分类" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="editId ? '编辑分类' : '新增分类'" width="400px" @close="editId = null">
      <el-form :model="form" label-width="80px">
        <el-form-item label="分类名称" required>
          <el-input v-model="form.shangpinfenlei" placeholder="请输入" />
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
  getShangpinfenleiList,
  addShangpinfenlei,
  updateShangpinfenlei,
  deleteShangpinfenlei,
} from '../../api/goods'
import type { Shangpinfenlei } from '../../api/goods'

const list = ref<Shangpinfenlei[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitLoading = ref(false)
const editId = ref<number | null>(null)
const form = reactive<Partial<Shangpinfenlei>>({ shangpinfenlei: '' })

const loadList = async () => {
  loading.value = true
  try {
    list.value = (await getShangpinfenleiList()) || []
  } catch {
    list.value = []
  } finally {
    loading.value = false
  }
}

const openDialog = (row?: Shangpinfenlei) => {
  editId.value = row?.id ?? null
  form.shangpinfenlei = row?.shangpinfenlei ?? ''
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!form.shangpinfenlei) {
    ElMessage.warning('请输入分类名称')
    return
  }
  submitLoading.value = true
  try {
    if (editId.value) {
      await updateShangpinfenlei({ ...form, id: editId.value })
      ElMessage.success('更新成功')
    } else {
      await addShangpinfenlei(form)
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
  await ElMessageBox.confirm('确定删除该分类？', '提示', { type: 'warning' })
  try {
    await deleteShangpinfenlei(id)
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
