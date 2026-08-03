<template>
  <div class="login-page">
    <div class="login-card">
      <h1 class="title">校园二手交易系统</h1>
      <el-tabs v-model="loginType" class="login-tabs">
        <el-tab-pane label="用户登录" name="user" />
        <el-tab-pane label="管理员登录" name="admin" />
      </el-tabs>
      <el-form :model="form" :rules="rules" @submit.prevent="handleLogin">
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            :placeholder="loginType === 'admin' ? '管理员账号' : '用户账号'"
            size="large"
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            size="large"
            show-password
            @keyup.enter="handleLogin"
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" style="width: 100%" :loading="loading" @click="handleLogin">
            登 录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { adminLogin, userLogin } from '../../api/auth'
import { useUserStore } from '../../stores/user'

const router = useRouter()
const userStore = useUserStore()

const loginType = ref<'user' | 'admin'>('user')
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
})

const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

const handleLogin = async () => {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入账号和密码')
    return
  }
  loading.value = true
  try {
    const api = loginType.value === 'admin' ? adminLogin : userLogin
    const data = await api({ username: form.username, password: form.password })
    userStore.setLogin(data)
    ElMessage.success('登录成功')
    // 根据后端返回的角色跳转：管理员→后台，用户→前台
    router.push(data.role === '管理员' ? '/admin' : '/home')
  } catch (e: unknown) {
    ElMessage.error((e as Error).message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(rgba(250, 250, 252, 0.88), rgba(250, 250, 252, 0.88)),
    url('https://images.unsplash.com/photo-1541339907198-e08756dedf3f?w=1920&q=80') center/cover no-repeat;
  position: relative;
}

.login-card {
  width: 400px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  backdrop-filter: blur(10px);
}

.title {
  text-align: center;
  margin-bottom: 24px;
  font-size: 28px;
  color: var(--primary-color);
}

.login-tabs {
  margin-bottom: 20px;
}

/* 标签按钮居中显示 */
.login-tabs :deep(.el-tabs__nav-wrap) {
  display: flex;
  justify-content: center;
}

.login-tabs :deep(.el-tabs__nav-scroll) {
  display: flex;
  justify-content: center;
}

.login-tabs :deep(.el-tabs__nav) {
  float: none;
  display: flex;
}

.login-tabs :deep(.el-tabs__item) {
  font-size: 16px;
  padding: 0 30px;
}
</style>
