<template>
  <div class="page">
    <el-card>
      <el-form v-loading="loading" :model="form" label-width="100px" style="max-width: 700px">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="副标题">
          <el-input v-model="form.subtitle" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="图片1">
          <ImageUpload v-model="form.picture1" />
        </el-form-item>
        <el-form-item label="图片2">
          <ImageUpload v-model="form.picture2" />
        </el-form-item>
        <el-form-item label="图片3">
          <ImageUpload v-model="form.picture3" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="8" placeholder="请输入" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAboutusDetail, updateAboutus } from '../../api/aboutus'
import ImageUpload from '../../components/ImageUpload.vue'
import type { AboutusItem } from '../../api/aboutus'

const loading = ref(false)
const submitLoading = ref(false)
const form = reactive<Partial<AboutusItem>>({
  title: '',
  subtitle: '',
  content: '',
  picture1: '',
  picture2: '',
  picture3: '',
})

const loadDetail = async () => {
  loading.value = true
  try {
    const data = await getAboutusDetail()
    if (data) Object.assign(form, data)
  } catch {
    // ignore
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  submitLoading.value = true
  try {
    await updateAboutus(form)
    ElMessage.success('保存成功')
  } catch (e: any) {
    ElMessage.error(e?.message || '保存失败')
  } finally {
    submitLoading.value = false
  }
}

onMounted(loadDetail)
</script>

<style scoped>
.page { padding: 0; }
</style>
