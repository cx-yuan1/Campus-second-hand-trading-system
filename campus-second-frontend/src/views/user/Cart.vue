<template>
  <div class="cart-page">
    <el-card>
      <h3>购物车</h3>
      <el-table v-loading="loading" :data="list" style="margin-top: 20px">
        <el-table-column min-width="80">
          <template #default="{ row }">
            <img :src="getImgUrl(row.picture)" class="cart-img" @error="onImgError" />
          </template>
        </el-table-column>
        <el-table-column prop="goodname" label="商品名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="price" label="单价" min-width="100">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="buynumber" label="数量" min-width="120">
          <template #default="{ row }">{{ row.buynumber }}</template>
        </el-table-column>
        <el-table-column label="小计" min-width="100">
          <template #default="{ row }">¥{{ (row.price * row.buynumber).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="操作" min-width="100">
          <template #default="{ row }">
            <el-button type="danger" link @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
        <el-table-column label="" />
      </el-table>
      <div class="cart-footer">
        <span>合计: ¥{{ totalPrice.toFixed(2) }}</span>
        <el-button type="primary" :disabled="!list.length" @click="openCheckout">去结算</el-button>
      </div>
      <el-empty v-if="!loading && !list.length" description="购物车为空" />
    </el-card>

    <!-- 结算弹窗 -->
    <el-dialog v-model="checkoutVisible" title="确认订单" width="600px" :close-on-click-modal="false">
      <el-form label-width="90px">
        <el-form-item label="收货地址" required>
          <el-radio-group v-model="selectedAddressId">
            <div v-for="addr in addressList" :key="addr.id" class="address-item">
              <el-radio :label="addr.id">
                {{ addr.name }} {{ addr.phone }}<br />
                {{ addr.address }}
              </el-radio>
            </div>
          </el-radio-group>
          <el-empty v-if="!addressLoading && !addressList.length" description="暂无收货地址" />
          <el-button type="primary" link style="margin-top: 8px" @click="goAddAddress">去添加地址</el-button>
        </el-form-item>
        <el-form-item label="商品清单">
          <div class="checkout-goods">
            <div v-for="item in list" :key="item.id" class="checkout-item">
              <img :src="getImgUrl(item.picture)" class="checkout-img" @error="onImgError" />
              <div class="checkout-info">
                <span>{{ item.goodname }}</span>
                <span class="checkout-meta">¥{{ item.price }} × {{ item.buynumber }}</span>
              </div>
              <span class="checkout-sub">¥{{ (item.price * item.buynumber).toFixed(2) }}</span>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="合计">
          <span class="total-price">¥{{ totalPrice.toFixed(2) }}</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="checkoutVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" :disabled="!selectedAddressId" @click="handleCheckout">
          提交订单
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCartList, deleteCart } from '../../api/cart'
import { getAddressList } from '../../api/address'
import { checkout } from '../../api/orders'
import { getImgUrl } from '../../utils/imgUrl'
import { useUserStore } from '../../stores/user'
import { useCartStore } from '../../stores/cart'
import type { CartItem } from '../../api/cart'
import type { AddressItem } from '../../api/address'

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()
const list = ref<CartItem[]>([])
const loading = ref(false)
const checkoutVisible = ref(false)
const addressList = ref<AddressItem[]>([])
const addressLoading = ref(false)
const selectedAddressId = ref<number | null>(null)
const submitLoading = ref(false)

const totalPrice = computed(() =>
  list.value.reduce((sum, item) => sum + item.price * item.buynumber, 0)
)

const onImgError = (e: Event) => {
  ;(e.target as HTMLImageElement).style.display = 'none'
}

const loadList = async () => {
  const userid = userStore.userInfo?.id
  if (!userid) return
  loading.value = true
  try {
    list.value = (await getCartList(userid)) || []
    await cartStore.refreshCount(userid)
  } catch {
    list.value = []
  } finally {
    loading.value = false
  }
}

const loadAddress = async () => {
  const userid = userStore.userInfo?.id
  if (!userid) return
  addressLoading.value = true
  try {
    addressList.value = (await getAddressList(userid)) || []
    const defaultAddr = addressList.value.find((a) => a.isdefault === '是')
    selectedAddressId.value = defaultAddr?.id ?? addressList.value[0]?.id ?? null
  } catch {
    addressList.value = []
  } finally {
    addressLoading.value = false
  }
}

const openCheckout = async () => {
  if (!list.value.length) return
  checkoutVisible.value = true
  await loadAddress()
}

const goAddAddress = () => {
  checkoutVisible.value = false
  router.push('/home/address')
}

const handleCheckout = async () => {
  const userid = userStore.userInfo?.id
  if (!userid || !selectedAddressId.value) {
    ElMessage.warning('请选择收货地址')
    return
  }
  if (!list.value.length) {
    ElMessage.warning('购物车为空')
    return
  }
  submitLoading.value = true
  try {
    await checkout({
      userid,
      addressId: selectedAddressId.value,
      cartIds: list.value.map((i) => i.id),
    })
    ElMessage.success('订单提交成功')
    checkoutVisible.value = false
    await loadList()
    router.push('/home/orders')
  } catch (e: unknown) {
    ElMessage.error((e as Error).message || '提交失败')
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (id: number) => {
  const userid = userStore.userInfo?.id
  if (!userid) return
  try {
    await deleteCart(id, userid)
    ElMessage.success('已删除')
    await loadList()
  } catch (e: unknown) {
    ElMessage.error((e as Error).message || '删除失败')
  }
}

onMounted(loadList)
</script>

<style scoped>
.cart-img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
}

.cart-footer {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 20px;
}

.address-item {
  margin-bottom: 8px;
}

.checkout-goods {
  max-height: 200px;
  overflow-y: auto;
}

.checkout-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
  border-bottom: 1px solid #eee;
}

.checkout-item:last-child {
  border-bottom: none;
}

.checkout-img {
  width: 50px;
  height: 50px;
  object-fit: cover;
  border-radius: 4px;
}

.checkout-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.checkout-meta {
  font-size: 12px;
  color: #909399;
}

.checkout-sub {
  color: var(--primary-color);
  font-weight: 500;
}

.total-price {
  font-size: 20px;
  color: var(--primary-color);
  font-weight: bold;
}
</style>
