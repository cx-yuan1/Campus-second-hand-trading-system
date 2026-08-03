<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <el-icon class="stat-icon goods"><Goods /></el-icon>
            <div>
              <div class="stat-value">{{ stats.goods }}</div>
              <div class="stat-label">商品数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <el-icon class="stat-icon order"><List /></el-icon>
            <div>
              <div class="stat-value">{{ stats.orders }}</div>
              <div class="stat-label">订单数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <el-icon class="stat-icon user"><User /></el-icon>
            <div>
              <div class="stat-value">{{ stats.users }}</div>
              <div class="stat-label">用户数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <el-icon class="stat-icon chart"><DataAnalysis /></el-icon>
            <div>
              <div class="stat-value">{{ stats.qiugou }}/{{ stats.juanzeng }}</div>
              <div class="stat-label">求购/捐赠</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>数据统计</span>
          </template>
          <div ref="chartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>订单状态分布</span>
          </template>
          <div ref="orderChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Goods, List, User, DataAnalysis } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getDashboardStats } from '../../api/admin'

const stats = reactive({
  goods: 0,
  orders: 0,
  users: 0,
  qiugou: 0,
  juanzeng: 0,
  orderStatusCount: {} as Record<string, number>,
})

const chartRef = ref<HTMLElement>()
const orderChartRef = ref<HTMLElement>()

const loadStats = async () => {
  try {
    const data = await getDashboardStats()
    if (data) {
      stats.goods = data.goods ?? 0
      stats.orders = data.orders ?? 0
      stats.users = data.users ?? 0
      stats.qiugou = data.qiugou ?? 0
      stats.juanzeng = data.juanzeng ?? 0
      stats.orderStatusCount = data.orderStatusCount ?? {}
    }
  } catch {
    // ignore
  }
}

const initChart = () => {
  if (!chartRef.value) return
  const chart = echarts.init(chartRef.value)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['商品', '订单', '用户'] },
    xAxis: { type: 'category', data: ['商品', '订单', '用户', '求购', '捐赠'] },
    yAxis: { type: 'value' },
    series: [
      {
        name: '数量',
        type: 'bar',
        data: [stats.goods, stats.orders, stats.users, stats.qiugou, stats.juanzeng],
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#409EFF' },
            { offset: 1, color: '#79bbff' },
          ]),
        },
      },
    ],
  })
}

const initOrderChart = () => {
  if (!orderChartRef.value) return
  const chart = echarts.init(orderChartRef.value)
  const statusList = ['待付款', '待发货', '待收货', '已完成', '已取消', '已退款']
  const data = statusList.map((name) => ({
    name,
    value: (stats.orderStatusCount[name] as number) ?? 0,
  })).filter((d) => d.value > 0)
  if (data.length === 0) {
    data.push({ name: '暂无订单', value: 1 })
  }
  chart.setOption({
    tooltip: { trigger: 'item' },
    legend: { orient: 'vertical', left: 'left' },
    series: [
      {
        name: '订单状态',
        type: 'pie',
        radius: '60%',
        data,
        emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0,0,0,0.2)' } },
      },
    ],
  })
}

onMounted(async () => {
  await loadStats()
  initChart()
  initOrderChart()
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.stat-card {
  background: var(--card-bg);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  font-size: 24px;
}

.stat-icon.goods {
  background: #ecf5ff;
  color: var(--primary-color);
}

.stat-icon.order {
  background: #f0f9eb;
  color: #67c23a;
}

.stat-icon.user {
  background: #fdf6ec;
  color: #e6a23c;
}

.stat-icon.chart {
  background: #f4f4f5;
  color: #909399;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.chart-container {
  height: 300px;
}
</style>
