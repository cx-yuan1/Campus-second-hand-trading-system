<template>
  <div class="user-layout">
    <!-- 前台顶部导航栏 -->
    <header class="header">
      <div class="logo" @click="$router.push('/home')">
        <el-icon class="logo-icon"><Shop /></el-icon>
        <span>校园二手交易</span>
      </div>
      <nav class="nav">
        <router-link to="/home" class="nav-item" :class="{ active: route.path === '/home' }">首页</router-link>
        <router-link to="/home/goods" class="nav-item" :class="{ active: route.path.startsWith('/home/goods') }">商品</router-link>
        <router-link to="/home/juanzeng" class="nav-item" :class="{ active: route.path === '/home/juanzeng' }">捐赠</router-link>
        <router-link to="/home/news" class="nav-item" :class="{ active: route.path === '/home/news' }">公告</router-link>
      </nav>
      <div class="header-right">
        <router-link to="/home/cart" class="nav-item cart-link">
          <span>购物车</span>
          <el-badge :value="cartCount" :max="99" :hidden="cartCount === 0" class="cart-badge">
            <el-icon><ShoppingCart /></el-icon>
          </el-badge>
        </router-link>
        <el-dropdown trigger="click" @command="handleCommand">
          <span class="user-dropdown">
            <el-icon><User /></el-icon>
            {{ userStore.userInfo?.username }}
            <el-icon class="el-icon--right"><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="orders">
                <el-icon><List /></el-icon>
                我的订单
              </el-dropdown-item>
              <el-dropdown-item command="address">
                <el-icon><Location /></el-icon>
                收货地址
              </el-dropdown-item>
              <el-dropdown-item command="collect">
                <el-icon><Star /></el-icon>
                我的收藏
              </el-dropdown-item>
              <el-dropdown-item command="recharge">
                <el-icon><Wallet /></el-icon>
                账户充值
              </el-dropdown-item>
              <el-dropdown-item divided command="logout">
                <el-icon><SwitchButton /></el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </header>
    <main class="content">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { useCartStore } from '../stores/cart'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const cartCount = computed(() => cartStore.count)

onMounted(() => {
  cartStore.refreshCount(userStore.userInfo?.id)
})

watch(() => userStore.userInfo?.id, (id) => {
  cartStore.refreshCount(id)
}, { immediate: true })

const handleCommand = (command: string) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  } else if (command === 'orders') {
    router.push('/home/orders')
  } else if (command === 'address') {
    router.push('/home/address')
  } else if (command === 'collect') {
    router.push('/home/collect')
  } else if (command === 'recharge') {
    router.push('/home/recharge')
  }
}
</script>

<style scoped>
.user-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--bg-color);
}

/* 前台顶部导航 */
.header {
  height: 60px;
  padding: 0 24px;
  background: var(--card-bg);
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  flex-shrink: 0;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 20px;
  font-weight: bold;
  color: var(--primary-color);
  cursor: pointer;
}

.logo:hover {
  opacity: 0.9;
}

.logo-icon {
  font-size: 28px;
}

.nav {
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 8px 16px;
  color: #333;
  text-decoration: none;
  border-radius: 6px;
  transition: all 0.2s;
}

.nav-item:hover {
  color: var(--primary-color);
  background: rgba(64, 158, 255, 0.08);
}

.nav-item.active {
  color: var(--primary-color);
  font-weight: 500;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.cart-link {
  position: relative;
}

.cart-badge :deep(.el-badge__content) {
  background-color: #f56c6c;
  border: none;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  color: #333;
  padding: 6px 12px;
  border-radius: 6px;
}

.user-dropdown:hover {
  color: var(--primary-color);
  background: rgba(64, 158, 255, 0.08);
}

.content {
  flex: 1;
  padding: 20px;
  overflow: auto;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
