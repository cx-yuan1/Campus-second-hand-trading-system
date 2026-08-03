<template>
  <div class="page">
    <el-card>
      <el-form :inline="true" class="filter-form">
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="待付款" value="待付款" />
            <el-option label="待发货" value="待发货" />
            <el-option label="待收货" value="待收货" />
            <el-option label="已完成" value="已完成" />
            <el-option label="已取消" value="已取消" />
            <el-option label="已退款" value="已退款" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadList">搜索</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="loading" :data="list">
        <el-table-column prop="orderid" label="订单号" min-width="180" />
        <el-table-column prop="goodname" label="商品名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="buynumber" label="数量" min-width="70" />
        <el-table-column prop="discounttotal" label="金额" min-width="90" />
        <el-table-column prop="status" label="状态" min-width="90" />
        <el-table-column prop="yonghuzhanghao" label="卖家账号" min-width="100" />
        <el-table-column prop="consignee" label="收货人" min-width="90" />
        <el-table-column prop="tel" label="电话" min-width="120" />
        <el-table-column label="操作" min-width="220">
          <template #default="{ row }">
            <template v-if="row.status === '待付款'">
              <el-button type="primary" link @click="handleStatusChange(row.id, '待发货')">确认付款</el-button>
              <el-button type="danger" link @click="handleStatusChange(row.id, '已取消')">取消订单</el-button>
            </template>
            <template v-else-if="row.status === '待发货'">
              <el-button type="primary" link @click="handleStatusChange(row.id, '待收货')">发货</el-button>
              <el-button type="danger" link @click="handleStatusChange(row.id, '已取消')">取消订单</el-button>
              <el-button type="warning" link @click="handleStatusChange(row.id, '已退款')">退款</el-button>
            </template>
            <template v-else-if="row.status === '待收货'">
              <el-button type="primary" link @click="handleStatusChange(row.id, '已完成')">确认收货</el-button>
              <el-button type="warning" link @click="handleStatusChange(row.id, '已退款')">退款</el-button>
            </template>
            <template v-else-if="row.status === '已完成'">
              <el-button type="warning" link @click="handleStatusChange(row.id, '已退款')">退款</el-button>
            </template>
            <template v-else>
              <span class="status-text">-</span>
            </template>
          </template>
        </el-table-column>
        <el-table-column label="" />
      </el-table>
      <el-pagination
        :current-page="query.page"
        :page-size="query.limit"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        style="margin-top: 16px"
        @current-change="onPageChange"
        @size-change="onSizeChange"
      />
      <el-empty v-if="!loading && !list.length" description="暂无订单" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getOrdersListAdmin, updateOrderStatus } from '../../api/admin'

const list = ref<any[]>([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ page: 1, limit: 10, status: '' })

const onPageChange = (p: number) => {
  query.page = p
  loadList()
}
const onSizeChange = (s: number) => {
  query.limit = s
  loadList()
}

const loadList = async () => {
  loading.value = true
  try {
    const data = await getOrdersListAdmin({
      page: query.page,
      limit: query.limit,
      status: query.status || undefined,
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

const handleStatusChange = async (id: number, status: string) => {
  try {
    await updateOrderStatus(id, status)
    ElMessage.success('状态更新成功')
    loadList()
  } catch (e: unknown) {
    ElMessage.error((e as Error)?.message || '更新失败')
    loadList()
  }
}

onMounted(loadList)
</script>

<style scoped>
.page { padding: 0; }
.filter-form { margin-bottom: 16px; }
.status-text { color: #909399; font-size: 13px; }
</style>
