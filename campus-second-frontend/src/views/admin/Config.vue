<template>
  <div class="page">
    <el-card>
      <template #header>
        <span>轮播图配置</span>
      </template>
      <el-table v-loading="loading" :data="list">
        <el-table-column prop="name" label="名称" min-width="120" />
        <el-table-column label="图片" min-width="120">
          <template #default="{ row }">
            <el-image
              v-if="row.value"
              :src="getImgUrl(row.value)"
              style="width: 80px; height: 50px"
              fit="cover"
            />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="value" label="路径" min-width="200" />
        <el-table-column label="操作" min-width="120">
          <template #default="{ row }">
            <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
          </template>
        </el-table-column>
        <el-table-column label="" />
      </el-table>
      <el-empty v-if="!loading && !list.length" description="暂无轮播图配置" />
    </el-card>
    <el-dialog v-model="dialogVisible" title="编辑轮播图" width="450px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="editForm.name" disabled />
        </el-form-item>
        <el-form-item label="图片">
          <ImageUpload v-model="editForm.value" />
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
import { ElMessage } from 'element-plus'
import { getBannerList, updateConfig } from '../../api/config'
import ImageUpload from '../../components/ImageUpload.vue'
import { getImgUrl } from '../../utils/imgUrl'
import type { ConfigItem } from '../../api/config'

const list = ref<ConfigItem[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitLoading = ref(false)
const editForm = reactive<Partial<ConfigItem>>({ id: 0, name: '', value: '' })

const loadList = async () => {
  loading.value = true
  try {
    list.value = (await getBannerList()) || []
  } catch {
    list.value = []
  } finally {
    loading.value = false
  }
}

const openEdit = (row: ConfigItem) => {
  editForm.id = row.id
  editForm.name = row.name
  editForm.value = row.value
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!editForm.id) return
  submitLoading.value = true
  try {
    await updateConfig({ id: editForm.id, name: editForm.name, value: editForm.value })
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadList()
  } catch (e: any) {
    ElMessage.error(e?.message || '保存失败')
  } finally {
    submitLoading.value = false
  }
}

onMounted(loadList)
</script>

<style scoped>
.page { padding: 0; }
</style>
