<template>
  <div class="admin-layout">
    <aside class="sidebar" :style="{ background: 'var(--sidebar-bg)' }">
      <div class="logo">
        <el-icon class="logo-icon"><Setting /></el-icon>
        <span>管理后台</span>
      </div>
      <el-menu
        :default-active="$route.path"
        router
        background-color="transparent"
        text-color="#333"
        active-text-color="var(--primary-color)"
      >
        <el-menu-item index="/admin">
          <el-icon><DataAnalysis /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/admin/shangpinxinxi">
          <el-icon><Goods /></el-icon>
          <span>商品管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/shangpinfenlei">
          <el-icon><Folder /></el-icon>
          <span>商品分类</span>
        </el-menu-item>
        <el-menu-item index="/admin/qiugouxinxi">
          <el-icon><Search /></el-icon>
          <span>求购管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/juanzengxinxi">
          <el-icon><Present /></el-icon>
          <span>捐赠管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/orders">
          <el-icon><List /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/yonghu">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/news">
          <el-icon><Bell /></el-icon>
          <span>公告管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/aboutus">
          <el-icon><InfoFilled /></el-icon>
          <span>关于我们</span>
        </el-menu-item>
        <el-menu-item index="/admin/systemintro">
          <el-icon><Document /></el-icon>
          <span>系统简介</span>
        </el-menu-item>
        <el-menu-item index="/admin/config">
          <el-icon><Picture /></el-icon>
          <span>轮播图配置</span>
        </el-menu-item>
      </el-menu>
    </aside>
    <div class="main">
      <header class="header">
        <span class="user-name">{{ userStore.userInfo?.username }}</span>
        <el-button type="danger" link @click="handleLogout">退出</el-button>
      </header>
      <main class="content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  height: 100vh;
}

.sidebar {
  width: 220px;
  padding: 20px 0;
  flex-shrink: 0;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 20px 20px;
  font-size: 18px;
  font-weight: bold;
  color: var(--primary-color);
}

.logo-icon {
  font-size: 24px;
}

.main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.header {
  height: 56px;
  padding: 0 24px;
  background: var(--card-bg);
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 16px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.content {
  flex: 1;
  padding: 20px;
  background: var(--bg-color);
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
