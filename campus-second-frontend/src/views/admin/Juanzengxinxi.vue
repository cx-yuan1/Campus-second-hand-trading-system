<template>
  <div class="page">
    <el-card>
      <el-form :inline="true" class="filter-form">
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="商品名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadList">搜索</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="loading" :data="list">
        <el-table-column prop="juanzengbianhao" label="捐赠编号" min-width="140" />
        <el-table-column prop="shangpinmingcheng" label="商品名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="juanzengshuliang" label="捐赠数量" min-width="90" />
        <el-table-column prop="juanzengdizhi" label="捐赠地址" min-width="180" show-overflow-tooltip />
        <el-table-column prop="yonghuzhanghao" label="发布账号" min-width="100" />
        <el-table-column prop="juanzengshijian" label="捐赠时间" min-width="110" />
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
      <el-empty v-if="!loading && !list.length" description="暂无捐赠信息" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="editId ? '编辑捐赠' : '新增捐赠'" width="500px" @close="editId = null">
      <el-form :model="form" label-width="100px">
        <el-form-item label="商品名称" required>
          <el-input v-model="form.shangpinmingcheng" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="捐赠数量">
          <el-input-number v-model="form.juanzengshuliang" :min="1" />
        </el-form-item>
        <el-form-item label="捐赠地址">
          <el-input v-model="form.juanzengdizhi" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.beizhu" type="textarea" :rows="2" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="发布账号">
          <el-input v-model="form.yonghuzhanghao" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="发布姓名">
          <el-input v-model="form.yonghuxingming" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input v-model="form.lianxifangshi" placeholder="请输入" />
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
  getJuanzengxinxiList,
  addJuanzengxinxi,
  updateJuanzengxinxi,
  deleteJuanzengxinxi,
} from '../../api/juanzeng'
import type { JuanzengxinxiItem } from '../../api/juanzeng'

const list = ref<JuanzengxinxiItem[]>([])
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const submitLoading = ref(false)
const editId = ref<number | null>(null)
const query = reactive({ page: 1, limit: 10, keyword: '' })
const form = reactive<Partial<JuanzengxinxiItem>>({
  shangpinmingcheng: '',
  juanzengshuliang: 1,
  juanzengdizhi: '',
  beizhu: '',
  yonghuzhanghao: '',
  yonghuxingming: '',
  lianxifangshi: '',
})

const loadList = async () => {
  loading.value = true
  try {
    const data = await getJuanzengxinxiList({
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

const openDialog = (row?: JuanzengxinxiItem) => {
  editId.value = row?.id ?? null
  Object.assign(form, row || {
    shangpinmingcheng: '',
    juanzengshuliang: 1,
    juanzengdizhi: '',
    beizhu: '',
    yonghuzhanghao: '',
    yonghuxingming: '',
    lianxifangshi: '',
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
      await updateJuanzengxinxi({ ...form, id: editId.value })
      ElMessage.success('更新成功')
    } else {
      await addJuanzengxinxi(form)
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
  await ElMessageBox.confirm('确定删除该捐赠信息？', '提示', { type: 'warning' })
  try {
    await deleteJuanzengxinxi(id)
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
