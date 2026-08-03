<template>
  <div class="juanzeng-page">
    <el-card>
      <el-button type="primary" @click="dialogVisible = true">发布捐赠</el-button>
      <el-input
        v-model="keyword"
        placeholder="搜索商品名称"
        style="width: 200px; margin-left: 12px"
        clearable
        @keyup.enter="loadList"
      />
      <el-button style="margin-left: 8px" @click="loadList">搜索</el-button>
      <el-table v-loading="loading" :data="list" style="margin-top: 20px">
        <el-table-column prop="juanzengbianhao" label="捐赠编号" min-width="140" />
        <el-table-column prop="shangpinmingcheng" label="商品名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="juanzengshuliang" label="捐赠数量" min-width="100" />
        <el-table-column prop="juanzengdizhi" label="捐赠地址" min-width="180" show-overflow-tooltip />
        <el-table-column prop="yonghuxingming" label="捐赠人" min-width="100" />
        <el-table-column prop="juanzengshijian" label="捐赠时间" min-width="120" />
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
    <el-dialog v-model="dialogVisible" title="发布捐赠" width="500px" @close="resetForm">
      <el-form :model="form" label-width="90px">
        <el-form-item label="商品名称" required>
          <el-input v-model="form.shangpinmingcheng" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="捐赠数量" required>
          <el-input-number v-model="form.juanzengshuliang" :min="1" />
        </el-form-item>
        <el-form-item label="捐赠地址" required>
          <el-input v-model="form.juanzengdizhi" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input v-model="form.lianxifangshi" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.beizhu" type="textarea" :rows="4" placeholder="请输入" />
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
import { getJuanzengxinxiList, addJuanzengxinxi } from '../../api/juanzeng'
import { useUserStore } from '../../stores/user'
import type { JuanzengxinxiItem } from '../../api/juanzeng'

const userStore = useUserStore()
const list = ref<JuanzengxinxiItem[]>([])
const total = ref(0)
const loading = ref(false)
const page = ref(1)
const limit = ref(10)
const keyword = ref('')
const dialogVisible = ref(false)
const submitLoading = ref(false)

const form = reactive({
  shangpinmingcheng: '',
  juanzengshuliang: 1,
  juanzengdizhi: '',
  lianxifangshi: '',
  beizhu: '',
  yonghuzhanghao: '',
  yonghuxingming: '',
})

const loadList = async () => {
  loading.value = true
  try {
    const data = await getJuanzengxinxiList({
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
  form.juanzengshuliang = 1
  form.juanzengdizhi = ''
  form.lianxifangshi = ''
  form.beizhu = ''
}

const handleSubmit = async () => {
  if (!form.shangpinmingcheng || !form.juanzengdizhi) {
    ElMessage.warning('请填写必填项')
    return
  }
  submitLoading.value = true
  try {
    await addJuanzengxinxi({
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
