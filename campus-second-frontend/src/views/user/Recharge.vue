<template>
  <div class="recharge-page">
    <el-card>
      <h3>账户充值</h3>
      <div class="balance-box">
        <span class="label">当前余额</span>
        <span class="amount">¥{{ (userMoney ?? 0).toFixed(2) }}</span>
      </div>
      <el-form :model="form" label-width="100px" style="max-width: 400px; margin-top: 24px">
        <el-form-item label="充值金额">
          <el-input-number v-model="form.amount" :min="0.01" :max="99999" :precision="2" :step="10" style="width: 100%" />
        </el-form-item>
        <el-form-item label="快捷充值">
          <div class="quick-amounts">
            <el-button v-for="a in quickAmounts" :key="a" @click="form.amount = a">¥{{ a }}</el-button>
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleRecharge">确认充值</el-button>
        </el-form-item>
      </el-form>
      <p class="tip">本平台为校园二手交易系统，充值金额仅用于演示，不会产生实际扣款。</p>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getYonghuDetail, recharge as rechargeApi } from '../../api/yonghu'
import { useUserStore } from '../../stores/user'

const userStore = useUserStore()
const userMoney = ref<number>(0)
const loading = ref(false)
const form = ref({ amount: 10 })
const quickAmounts = [10, 50, 100, 200, 500]

const loadUser = async () => {
  const id = userStore.userInfo?.id
  if (!id) return
  try {
    const u = await getYonghuDetail(id)
    userMoney.value = u?.money ?? 0
  } catch {
    userMoney.value = 0
  }
}

const handleRecharge = async () => {
  const id = userStore.userInfo?.id
  if (!id) {
    ElMessage.warning('请先登录')
    return
  }
  const amount = form.value.amount
  if (!amount || amount <= 0) {
    ElMessage.warning('请输入有效充值金额')
    return
  }
  loading.value = true
  try {
    await rechargeApi({ userid: id, amount })
    ElMessage.success('充值成功')
    loadUser()
  } catch (e: unknown) {
    ElMessage.error((e as Error).message || '充值失败')
  } finally {
    loading.value = false
  }
}

onMounted(loadUser)
</script>

<style scoped>
.recharge-page {
  max-width: 560px;
  margin: 0 auto;
}

.balance-box {
  margin-top: 20px;
  padding: 20px 24px;
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  border-radius: 12px;
  color: #fff;
}

.balance-box .label {
  font-size: 14px;
  opacity: 0.9;
}

.balance-box .amount {
  display: block;
  font-size: 28px;
  font-weight: bold;
  margin-top: 4px;
}

.quick-amounts {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.quick-amounts .el-button {
  min-width: 70px;
}

.tip {
  margin-top: 24px;
  font-size: 13px;
  color: #909399;
}
</style>
