<template>
  <div class="image-upload-single">
    <div class="upload-container" :class="{ 'is-multiple': multiple }">
      <!-- 多图模式：已上传的图片列表 -->
      <template v-if="multiple">
        <div
          v-for="(path, idx) in pathList"
          :key="idx"
          class="image-item"
        >
          <el-image :src="getImgUrl(path)" fit="cover">
            <template #error>
              <div class="upload-placeholder-inline">
                <el-icon><Plus /></el-icon>
                <span>加载失败</span>
              </div>
            </template>
          </el-image>
          <el-icon class="remove-btn" @click="removeImg(idx)"><Close /></el-icon>
        </div>
      </template>
      <!-- 上传触发框：单图时唯一框，多图时为添加框 -->
      <el-upload
        :http-request="customUpload"
        name="file"
        :show-file-list="false"
        accept="image/*"
        class="upload-trigger"
      >
        <div class="upload-box">
          <template v-if="pathList.length && !multiple">
            <div class="image-item">
              <el-image :src="getImgUrl(pathList[0])" fit="cover">
                <template #error>
                  <div class="upload-placeholder">
                    <el-icon><Plus /></el-icon>
                    <span>点击上传</span>
                  </div>
                </template>
              </el-image>
              <el-icon class="remove-btn" @click.stop="removeImg(0)"><Close /></el-icon>
            </div>
          </template>
          <template v-else>
            <div class="upload-placeholder">
              <el-icon><Plus /></el-icon>
              <span>{{ multiple && pathList.length ? '添加' : '点击上传' }}</span>
            </div>
          </template>
        </div>
      </el-upload>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Close } from '@element-plus/icons-vue'
import { getImgUrl } from '../utils/imgUrl'
import { uploadFile } from '../api/file'

const props = defineProps<{
  modelValue: string
  multiple?: boolean
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', v: string): void
}>()

const pathList = computed(() => {
  const v = props.modelValue || ''
  return v ? v.split(',').filter(Boolean) : []
})

const customUpload = async (options: { file: File }) => {
  const file = options.file
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB')
    return
  }
  try {
    const path = await uploadFile(file)
    if (path) {
      // 单图模式：新上传覆盖旧的；多图模式：追加
      const newVal = props.multiple
        ? `${props.modelValue || ''}${props.modelValue ? ',' : ''}${path}`
        : path
      emit('update:modelValue', newVal)
    }
  } catch (e: any) {
    ElMessage.error(e?.message || '上传失败')
  }
}

const removeImg = (idx: number) => {
  const arr = [...pathList.value]
  arr.splice(idx, 1)
  emit('update:modelValue', arr.join(','))
}
</script>

<style scoped>
.image-upload-single {
  width: 100%;
}

.upload-container {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
}

.upload-container.is-multiple .upload-box {
  flex-shrink: 0;
}

.upload-box {
  width: 100px;
  min-height: 100px;
  padding: 0;
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  background: #fafafa;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.upload-box:hover {
  border-color: var(--el-color-primary);
}

.upload-placeholder,
.upload-placeholder-inline {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  min-height: 98px;
  color: #909399;
  font-size: 12px;
  gap: 4px;
}

.upload-placeholder .el-icon,
.upload-placeholder-inline .el-icon {
  font-size: 24px;
}

.upload-placeholder:hover,
.upload-box:hover .upload-placeholder {
  color: var(--el-color-primary);
}

.upload-placeholder-inline {
  background: #f5f5f5;
  min-height: 98px;
}

.image-item {
  position: relative;
  width: 100px;
  height: 100px;
  border-radius: 6px;
  overflow: hidden;
  flex-shrink: 0;
}

.upload-box .image-item {
  width: 100%;
  height: 100%;
  min-height: 98px;
}

.image-item .el-image {
  width: 100%;
  height: 100%;
}

.remove-btn {
  position: absolute;
  top: 4px;
  right: 4px;
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  border-radius: 4px;
  cursor: pointer;
  padding: 2px;
}

.remove-btn:hover {
  background: #f56c6c;
}

.upload-trigger {
  display: inline-block;
}

.upload-trigger :deep(.el-upload) {
  display: block;
  border: none;
  background: transparent;
  width: 100%;
}
</style>
