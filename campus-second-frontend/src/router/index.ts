import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('../views/login/Login.vue'),
      meta: { title: '登录', noAuth: true },
    },
    {
      path: '/home',
      component: () => import('../layout/UserLayout.vue'),
      meta: { requiresAuth: true, role: 'user' },
      children: [
        { path: '', name: 'Home', component: () => import('../views/user/Home.vue'), meta: { title: '首页' } },
        { path: 'goods', name: 'GoodsList', component: () => import('../views/user/GoodsList.vue'), meta: { title: '商品列表' } },
        { path: 'goods/:id', name: 'GoodsDetail', component: () => import('../views/user/GoodsDetail.vue'), meta: { title: '商品详情' } },
        { path: 'qiugou', name: 'Qiugou', component: () => import('../views/user/Qiugou.vue'), meta: { title: '求购信息' } },
        { path: 'juanzeng', name: 'Juanzeng', component: () => import('../views/user/Juanzeng.vue'), meta: { title: '捐赠信息' } },
        { path: 'cart', name: 'Cart', component: () => import('../views/user/Cart.vue'), meta: { title: '购物车' } },
        { path: 'orders', name: 'Orders', component: () => import('../views/user/Orders.vue'), meta: { title: '我的订单' } },
        { path: 'address', name: 'Address', component: () => import('../views/user/Address.vue'), meta: { title: '收货地址' } },
        { path: 'collect', name: 'Collect', component: () => import('../views/user/Collect.vue'), meta: { title: '我的收藏' } },
        { path: 'recharge', name: 'Recharge', component: () => import('../views/user/Recharge.vue'), meta: { title: '账户充值' } },
        { path: 'news/:id', name: 'NewsDetail', component: () => import('../views/user/NewsDetail.vue'), meta: { title: '公告详情' } },
        { path: 'news', name: 'News', component: () => import('../views/user/News.vue'), meta: { title: '公告列表' } },
        { path: 'about', name: 'About', component: () => import('../views/user/About.vue'), meta: { title: '关于我们' } },
      ],
    },
    {
      path: '/admin',
      component: () => import('../layout/AdminLayout.vue'),
      meta: { requiresAuth: true, role: 'admin' },
      children: [
        { path: '', name: 'AdminDashboard', component: () => import('../views/admin/Dashboard.vue'), meta: { title: '仪表盘' } },
        { path: 'shangpinxinxi', name: 'Shangpinxinxi', component: () => import('../views/admin/Shangpinxinxi.vue'), meta: { title: '商品管理' } },
        { path: 'shangpinfenlei', name: 'Shangpinfenlei', component: () => import('../views/admin/Shangpinfenlei.vue'), meta: { title: '商品分类' } },
        { path: 'qiugouxinxi', name: 'Qiugouxinxi', component: () => import('../views/admin/Qiugouxinxi.vue'), meta: { title: '求购管理' } },
        { path: 'juanzengxinxi', name: 'Juanzengxinxi', component: () => import('../views/admin/Juanzengxinxi.vue'), meta: { title: '捐赠管理' } },
        { path: 'orders', name: 'AdminOrders', component: () => import('../views/admin/Orders.vue'), meta: { title: '订单管理' } },
        { path: 'yonghu', name: 'Yonghu', component: () => import('../views/admin/Yonghu.vue'), meta: { title: '用户管理' } },
        { path: 'news', name: 'AdminNews', component: () => import('../views/admin/News.vue'), meta: { title: '公告管理' } },
        { path: 'aboutus', name: 'Aboutus', component: () => import('../views/admin/Aboutus.vue'), meta: { title: '关于我们' } },
        { path: 'systemintro', name: 'Systemintro', component: () => import('../views/admin/Systemintro.vue'), meta: { title: '系统简介' } },
        { path: 'config', name: 'Config', component: () => import('../views/admin/Config.vue'), meta: { title: '轮播图配置' } },
      ],
    },
    { path: '/', redirect: '/home' },
    { path: '/:pathMatch(.*)*', redirect: '/login' },
  ],
})

router.beforeEach((to) => {
  const userStore = useUserStore()
  userStore.initFromStorage()

  document.title = to.meta.title ? `${to.meta.title} - 校园二手交易系统` : '校园二手交易系统'

  if (to.meta.noAuth) {
    if (userStore.token && to.path === '/login') {
      return userStore.isAdmin() ? '/admin' : '/home'
    }
    return true
  }

  if (!userStore.token) {
    return '/login'
  }

  const role = userStore.userInfo?.role
  // 会话损坏（token 存在但角色既非用户也非管理员）时直接退出登录，
  // 否则会在 /home 与 /admin 之间无限跳转，导致浏览器卡死
  if (role !== '用户' && role !== '管理员') {
    userStore.logout()
    return '/login'
  }

  if (to.meta.role === 'admin' && role !== '管理员') {
    return '/home'
  }
  if (to.meta.role === 'user' && role !== '用户') {
    return '/admin'
  }

  return true
})

export default router
