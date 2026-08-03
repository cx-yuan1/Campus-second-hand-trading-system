<template>
  <div class="address-page">
    <el-card>
      <el-button type="primary" @click="openDialog()">新增地址</el-button>
      <el-table v-loading="loading" :data="list" style="margin-top: 20px">
        <el-table-column prop="name" label="收货人" min-width="120" />
        <el-table-column prop="phone" label="电话" min-width="140" />
        <el-table-column prop="address" label="地址" min-width="180" />
        <el-table-column prop="isdefault" label="默认" min-width="80" />
        <el-table-column label="操作" min-width="150">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDialog(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
        <el-table-column label="" />
      </el-table>
      <el-empty v-if="!loading && !list.length" description="暂无收货地址" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="editId ? '编辑地址' : '新增地址'" width="450px" @close="resetForm">
      <el-form :model="form" label-width="80px">
        <el-form-item label="收货人" required>
          <el-input v-model="form.name" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="电话" required>
          <el-input v-model="form.phone" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="地址" required>
          <el-input v-model="form.address" type="textarea" :rows="2" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="默认地址">
          <el-radio-group v-model="form.isdefault">
            <el-radio label="是">是</el-radio>
            <el-radio label="否">否</el-radio>
          </el-radio-group>
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
import { getAddressList, addAddress, updateAddress, deleteAddress } from '../../api/address'
import { useUserStore } from '../../stores/user'
import type { AddressItem } from '../../api/address'

const userStore = useUserStore()
const list = ref<AddressItem[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitLoading = ref(false)
const editId = ref<number | null>(null)

const form = reactive({
  id: 0,
  address: '',
  name: '',
  phone: '',
  isdefault: '否',
  userid: 0,
})

const loadList = async () => {
  const userid = userStore.userInfo?.id
  if (!userid) return
  loading.value = true
  try {
    list.value = (await getAddressList(userid)) || []
  } catch {
    list.value = []
  } finally {
    loading.value = false
  }
}

const openDialog = (row?: AddressItem) => {
  editId.value = row?.id ?? null
  if (row) {
    form.id = row.id
    form.address = row.address
    form.name = row.name
    form.phone = row.phone
    form.isdefault = row.isdefault || '否'
    form.userid = row.userid
  } else {
    resetForm()
  }
  dialogVisible.value = true
}

const resetForm = () => {
  editId.value = null
  form.id = 0
  form.address = ''
  form.name = ''
  form.phone = ''
  form.isdefault = '否'
  form.userid = userStore.userInfo?.id || 0
}

const handleSubmit = async () => {
  if (!form.name || !form.phone || !form.address) {
    ElMessage.warning('请填写必填项')
    return
  }
  const userid = userStore.userInfo?.id
  if (!userid) return
  form.userid = userid
  submitLoading.value = true
  try {
    if (editId.value) {
      await updateAddress(form)
      ElMessage.success('更新成功')
    } else {
      await addAddress(form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadList()
  } catch (e: unknown) {
    ElMessage.error((e as Error).message || '操作失败')
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (id: number) => {
  const userid = userStore.userInfo?.id
  if (!userid) return
  try {
    await deleteAddress(id, userid)
    ElMessage.success('删除成功')
    loadList()
  } catch (e: unknown) {
    ElMessage.error((e as Error).message || '删除失败')
  }
}

onMounted(loadList)
</script>
