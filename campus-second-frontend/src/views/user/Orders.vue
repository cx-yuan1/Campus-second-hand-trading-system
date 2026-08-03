<template>
  <div class="orders-page">
    <el-card>
      <h3>我的订单</h3>
      <el-radio-group v-model="status" style="margin: 16px 0" @change="loadList">
        <el-radio-button label="">全部</el-radio-button>
        <el-radio-button label="待付款">待付款</el-radio-button>
        <el-radio-button label="待发货">待发货</el-radio-button>
        <el-radio-button label="待收货">待收货</el-radio-button>
        <el-radio-button label="已完成">已完成</el-radio-button>
        <el-radio-button label="已取消">已取消</el-radio-button>
        <el-radio-button label="已退款">已退款</el-radio-button>
      </el-radio-group>
      <el-table v-loading="loading" :data="list">
        <el-table-column prop="orderid" label="订单编号" min-width="180" />
        <el-table-column min-width="80">
          <template #default="{ row }">
            <img :src="getImgUrl(row.picture)" class="order-img" @error="onImgError" />
          </template>
        </el-table-column>
        <el-table-column prop="goodname" label="商品" min-width="180" show-overflow-tooltip />
        <el-table-column prop="buynumber" label="数量" min-width="80" />
        <el-table-column prop="discounttotal" label="金额" min-width="100">
          <template #default="{ row }">¥{{ row.discounttotal }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" min-width="100" />
        <el-table-column label="下单时间" min-width="180">
          <template #default="{ row }">{{ formatTime(row.addtime) }}</template>
        </el-table-column>
        <el-table-column label="操作" min-width="150">
          <template #default="{ row }">
            <template v-if="row.status === '待付款'">
              <el-button type="primary" link @click="openPayDialog(row)">去付款</el-button>
              <el-button type="danger" link @click="handleCancel(row.id)">取消订单</el-button>
            </template>
            <template v-else-if="row.status === '待收货'">
              <el-button type="primary" link @click="handleConfirmReceive(row.id)">确认收货</el-button>
            </template>
          </template>
        </el-table-column>
        <el-table-column label="" />
      </el-table>
      <el-pagination
        :current-page="page"
        :total="total"
        :page-size="limit"
        layout="total, prev, pager, next"
        style="margin-top: 20px; justify-content: center"
        @current-change="onPageChange"
      />
      <el-empty v-if="!loading && !list.length" description="暂无订单" />
    </el-card>

    <!-- 付款说明弹窗 -->
    <el-dialog v-model="payDialogVisible" title="付款说明" width="480px">
      <div v-if="currentOrder" class="pay-dialog">
        <p class="pay-tip">使用<strong>账户余额</strong>支付订单。请确保余额充足，支付成功后将从您的账户扣减相应金额。</p>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="卖家账号">{{ currentOrder.yonghuzhanghao || '-' }}</el-descriptions-item>
          <el-descriptions-item label="收货人">{{ currentOrder.consignee }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentOrder.tel }}</el-descriptions-item>
          <el-descriptions-item label="收货地址">{{ currentOrder.address }}</el-descriptions-item>
          <el-descriptions-item label="订单金额">¥{{ currentOrder.discounttotal }}</el-descriptions-item>
        </el-descriptions>
        <p class="pay-note">点击「确认已付款」后，将从您的账户余额中扣款，卖家将为您安排发货。余额不足请先<router-link to="/home/recharge" @click="payDialogVisible = false">充值</router-link>。</p>
      </div>
      <template #footer>
        <el-button @click="payDialogVisible = false">关闭</el-button>
        <el-button type="primary" :loading="payLoading" @click="handleConfirmPay">
          确认已付款
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrdersList, updateOrderStatus } from '../../api/orders'
import { getImgUrl } from '../../utils/imgUrl'
import { useUserStore } from '../../stores/user'
import type { OrderItem } from '../../api/orders'

const userStore = useUserStore()
const list = ref<OrderItem[]>([])
const total = ref(0)
const loading = ref(false)
const page = ref(1)
const limit = ref(10)
const status = ref('')
const payDialogVisible = ref(false)
const currentOrder = ref<OrderItem | null>(null)
const payLoading = ref(false)

const onImgError = (e: Event) => {
  ;(e.target as HTMLImageElement).style.display = 'none'
}

const formatTime = (t: string | undefined) => {
  if (!t) return '-'
  return t.replace('T', ' ').substring(0, 19)
}

const loadList = async () => {
  const userid = userStore.userInfo?.id
  if (!userid) return
  loading.value = true
  try {
    const data = await getOrdersList({
      userid,
      page: page.value,
      limit: limit.value,
      status: status.value || undefined,
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

const openPayDialog = (row: OrderItem) => {
  currentOrder.value = row
  payDialogVisible.value = true
}

const handleConfirmPay = async () => {
  if (!currentOrder.value) return
  payLoading.value = true
  try {
    await updateOrderStatus(currentOrder.value.id, '待发货')
    ElMessage.success('已确认付款，请等待卖家发货')
    payDialogVisible.value = false
    loadList()
  } catch (e: unknown) {
    ElMessage.error((e as Error).message || '操作失败')
  } finally {
    payLoading.value = false
  }
}

const handleCancel = async (id: number) => {
  await ElMessageBox.confirm('确定取消该订单？', '提示', { type: 'warning' })
  try {
    await updateOrderStatus(id, '已取消')
    ElMessage.success('订单已取消')
    loadList()
  } catch (e: unknown) {
    ElMessage.error((e as Error).message || '取消失败')
  }
}

const handleConfirmReceive = async (id: number) => {
  await ElMessageBox.confirm('确认已收到商品？', '提示', { type: 'info' })
  try {
    await updateOrderStatus(id, '已完成')
    ElMessage.success('交易完成')
    loadList()
  } catch (e: unknown) {
    ElMessage.error((e as Error).message || '操作失败')
  }
}

const onPageChange = (p: number) => {
  page.value = p
  loadList()
}

onMounted(loadList)
</script>

<style scoped>
.order-img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
}

.pay-dialog {
  padding: 0 8px;
}

.pay-tip {
  margin-bottom: 16px;
  line-height: 1.6;
  color: #606266;
}

.pay-note {
  margin-top: 16px;
  font-size: 13px;
  color: #909399;
}
</style>
