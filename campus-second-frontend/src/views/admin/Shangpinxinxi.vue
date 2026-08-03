<template>
  <div class="page">
    <el-card>
      <el-form :inline="true" class="filter-form">
        <el-form-item label="分类">
          <el-select v-model="query.shangpinfenlei" placeholder="全部分类" clearable style="width: 120px">
            <el-option v-for="item in fenleiList" :key="item.id" :label="item.shangpinfenlei" :value="item.shangpinfenlei" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="商品名称/编号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadList">搜索</el-button>
          <el-button @click="openDialog()">新增商品</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="loading" :data="list">
        <el-table-column prop="shangpinbianhao" label="商品编号" min-width="140" />
        <el-table-column prop="shangpinmingcheng" label="商品名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="shangpinfenlei" label="分类" min-width="100" />
        <el-table-column label="图片" min-width="80">
          <template #default="{ row }">
            <el-image
              v-if="getFirstImg(row.tupian)"
              :src="getImgUrl(getFirstImg(row.tupian))"
              style="width: 50px; height: 50px"
              fit="cover"
            />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="xinjiuchengdu" label="新旧程度" min-width="90" />
        <el-table-column prop="price" label="价格" min-width="80" />
        <el-table-column prop="clicknum" label="点击" min-width="70" />
        <el-table-column label="操作" min-width="180">
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
      <el-empty v-if="!loading && !list.length" description="暂无商品" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="editId ? '编辑商品' : '新增商品'" width="600px" @close="resetForm">
      <el-form :model="form" label-width="100px">
        <el-form-item label="商品编号">
          <el-input v-model="form.shangpinbianhao" placeholder="留空自动生成" />
        </el-form-item>
        <el-form-item label="商品名称" required>
          <el-input v-model="form.shangpinmingcheng" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="分类" required>
          <el-select v-model="form.shangpinfenlei" placeholder="请选择" style="width: 100%">
            <el-option v-for="item in fenleiList" :key="item.id" :label="item.shangpinfenlei" :value="item.shangpinfenlei" />
          </el-select>
        </el-form-item>
        <el-form-item label="规格">
          <el-input v-model="form.guige" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="图片">
          <ImageUpload v-model="form.tupian" :multiple="true" />
        </el-form-item>
        <el-form-item label="新旧程度">
          <el-input v-model="form.xinjiuchengdu" placeholder="如：九成新" />
        </el-form-item>
        <el-form-item label="价格" required>
          <el-input-number v-model="form.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="商品详情">
          <el-input v-model="form.shangpinxiangqing" type="textarea" :rows="3" placeholder="请输入" />
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
  getShangpinxinxiList,
  getShangpinfenleiList,
  addShangpinxinxi,
  updateShangpinxinxi,
  deleteShangpinxinxi,
} from '../../api/goods'
import { getImgUrl } from '../../utils/imgUrl'
import ImageUpload from '../../components/ImageUpload.vue'
import type { Shangpinxinxi, Shangpinfenlei } from '../../api/goods'

const fenleiList = ref<Shangpinfenlei[]>([])
const list = ref<Shangpinxinxi[]>([])
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const submitLoading = ref(false)
const editId = ref<number | null>(null)

const query = reactive({ page: 1, limit: 10, shangpinfenlei: '', keyword: '' })

const form = reactive<Partial<Shangpinxinxi>>({
  shangpinbianhao: '',
  shangpinmingcheng: '',
  shangpinfenlei: '',
  guige: '',
  tupian: '',
  xinjiuchengdu: '',
  price: 0,
  shangpinxiangqing: '',
  yonghuzhanghao: '',
  yonghuxingming: '',
})

const getFirstImg = (tupian: string | undefined) => (tupian ? tupian.split(',')[0] : '')

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

const openDialog = (row?: Shangpinxinxi) => {
  editId.value = row?.id ?? null
  Object.assign(form, row || {
    shangpinbianhao: '',
    shangpinmingcheng: '',
    shangpinfenlei: fenleiList.value[0]?.shangpinfenlei || '',
    guige: '',
    tupian: '',
    xinjiuchengdu: '',
    price: 0,
    shangpinxiangqing: '',
    yonghuzhanghao: '',
    yonghuxingming: '',
  })
  dialogVisible.value = true
}

const resetForm = () => {
  editId.value = null
}

const handleSubmit = async () => {
  if (!form.shangpinmingcheng) {
    ElMessage.warning('请输入商品名称')
    return
  }
  submitLoading.value = true
  try {
    if (editId.value) {
      await updateShangpinxinxi({ ...form, id: editId.value })
      ElMessage.success('更新成功')
    } else {
      await addShangpinxinxi(form)
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
  await ElMessageBox.confirm('确定删除该商品？', '提示', { type: 'warning' })
  try {
    await deleteShangpinxinxi(id)
    ElMessage.success('删除成功')
    loadList()
  } catch (e: any) {
    ElMessage.error(e?.message || '删除失败')
  }
}

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
.page { padding: 0; }
.filter-form { margin-bottom: 16px; }
</style>
