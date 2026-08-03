<template>
  <div class="page">
    <el-card>
      <el-form :inline="true" class="filter-form">
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="账号/姓名" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadList">搜索</el-button>
          <el-button @click="openDialog()">新增用户</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="loading" :data="list">
        <el-table-column label="头像" min-width="80">
          <template #default="{ row }">
            <el-image
              v-if="row.touxiang"
              :src="getImgUrl(row.touxiang)"
              style="width: 40px; height: 40px; border-radius: 4px"
              fit="cover"
            >
              <template #error>
                <span class="img-placeholder">-</span>
              </template>
            </el-image>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="yonghuzhanghao" label="账号" min-width="120" />
        <el-table-column prop="yonghuxingming" label="姓名" min-width="100" />
        <el-table-column prop="xingbie" label="性别" min-width="70" />
        <el-table-column prop="lianxifangshi" label="联系方式" min-width="120" />
        <el-table-column prop="money" label="余额" min-width="90" />
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
      <el-empty v-if="!loading && !list.length" description="暂无用户" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="editId ? '编辑用户' : '新增用户'" width="450px" @close="editId = null">
      <el-form :model="form" label-width="90px">
        <el-form-item label="账号" required>
          <el-input v-model="form.yonghuzhanghao" placeholder="请输入" :disabled="!!editId" />
        </el-form-item>
        <el-form-item label="密码" :required="!editId">
          <el-input v-model="form.mima" type="password" placeholder="留空则不修改" show-password />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.yonghuxingming" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.xingbie">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input v-model="form.lianxifangshi" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="头像">
          <ImageUpload v-model="form.touxiang" />
        </el-form-item>
        <el-form-item label="余额">
          <el-input-number v-model="form.money" :min="0" :precision="2" />
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
  getYonghuList,
  addYonghu,
  updateYonghu,
  deleteYonghu,
} from '../../api/yonghu'
import type { YonghuItem } from '../../api/yonghu'
import ImageUpload from '../../components/ImageUpload.vue'
import { getImgUrl } from '../../utils/imgUrl'

const list = ref<YonghuItem[]>([])
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const submitLoading = ref(false)
const editId = ref<number | null>(null)
const query = reactive({ page: 1, limit: 10, keyword: '' })
const form = reactive<Partial<YonghuItem>>({
  yonghuzhanghao: '',
  mima: '',
  yonghuxingming: '',
  xingbie: '男',
  lianxifangshi: '',
  touxiang: '',
  money: 0,
})

const loadList = async () => {
  loading.value = true
  try {
    const data = await getYonghuList({
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

const openDialog = (row?: YonghuItem) => {
  editId.value = row?.id ?? null
  Object.assign(form, row || {
    yonghuzhanghao: '',
    mima: '',
    yonghuxingming: '',
    xingbie: '男',
    lianxifangshi: '',
    touxiang: '',
    money: 0,
  })
  if (editId.value) (form as any).mima = ''
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!form.yonghuzhanghao) {
    ElMessage.warning('请输入账号')
    return
  }
  if (!editId.value && !form.mima) {
    ElMessage.warning('请输入密码')
    return
  }
  submitLoading.value = true
  try {
    const submitData = { ...form }
    if (editId.value && !submitData.mima) delete (submitData as any).mima
    if (editId.value) {
      await updateYonghu({ ...submitData, id: editId.value })
      ElMessage.success('更新成功')
    } else {
      await addYonghu(submitData)
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
  await ElMessageBox.confirm('确定删除该用户？', '提示', { type: 'warning' })
  try {
    await deleteYonghu(id)
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
.img-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  background: #f5f5f5;
  color: #909399;
  font-size: 12px;
}
</style>
