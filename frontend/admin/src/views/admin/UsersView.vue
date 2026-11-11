<template>
  <AdminLayout>
    <el-card><template #header><span>用户管理</span></template>
      <el-button type="primary" style="margin-bottom:16px" @click="showCreate">新增管理员</el-button>
      <el-table :data="users" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="roleName" label="角色" width="120" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="lastLoginAt" label="最后登录" width="160" />
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button size="small" @click="resetPwd(row)">重置密码</el-button>
            <el-button size="small" type="danger" @click="delUser(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="createVisible" title="新增管理员" width="400px">
      <el-form :model="createForm" label-width="80px">
        <el-form-item label="用户名"><el-input v-model="createForm.username" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="createForm.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="createForm.email" /></el-form-item>
        <el-form-item label="初始密码"><el-input v-model="createForm.password" type="password" show-password /></el-form-item>
        <el-form-item label="角色">
          <el-select v-model="createForm.roleId" placeholder="请选择">
            <el-option v-for="r in roles" :key="r.id" :label="r.name" :value="r.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" @click="doCreate">保存</el-button>
      </template>
    </el-dialog>
  </AdminLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import AdminLayout from '../LayoutView.vue'
import api from '@/api'

const users = ref<any[]>([])
const roles = ref<any[]>([])
const loading = ref(false)
const createVisible = ref(false)
const createForm = ref({ username: '', phone: '', email: '', password: '', roleId: null as number | null })

async function load() {
  loading.value = true
  const res = await api.get('/api/admin/users')
  if (res.data.code === 0) users.value = res.data.data || []
  loading.value = false
}

async function loadRoles() {
  const res = await api.get('/api/admin/roles')
  if (res.data.code === 0) roles.value = res.data.data || []
}

async function doCreate() {
  const res = await api.post('/api/admin/users', createForm.value)
  if (res.data.code === 0) { ElMessage.success('创建成功'); createVisible.value = false; load() }
  else ElMessage.error(res.data.message || '创建失败')
}

async function resetPwd(u: any) {
  const pw = prompt('请输入新密码（8-32位，含字母+数字）:')
  if (!pw) return
  const res = await api.post(`/api/admin/users/${u.id}/reset-password`, { newPassword: pw })
  if (res.data.code === 0) ElMessage.success('密码已重置')
  else ElMessage.error(res.data.message || '重置失败')
}

async function delUser(u: any) {
  await ElMessageBox.confirm(`确认删除用户 ${u.username}？`, '提示')
  const res = await api.delete('/api/admin/users/batch', { data: { ids: [u.id] } })
  if (res.data.code === 0) { ElMessage.success('删除成功'); load() }
}

onMounted(() => { load(); loadRoles() })
</script>
