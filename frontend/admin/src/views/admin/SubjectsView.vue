<template>
  <AdminLayout>
    <el-card>
      <template #header><span>学科管理</span></template>
      <el-button type="primary" @click="dialogVisible = true" style="margin-bottom:16px">新增学科</el-button>
      <el-table :data="subjects" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="icon" label="图标" width="80" />
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="editSubject(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteSubject(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="新增/编辑学科" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="图标"><el-input v-model="form.icon" placeholder="如 ☕" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" /></el-form-item>
        <el-form-item label="颜色"><el-color-picker v-model="form.color" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveSubject">保存</el-button>
      </template>
    </el-dialog>
  </AdminLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import AdminLayout from '../LayoutView.vue'
import api from '@/api'

const subjects = ref<any[]>([])
const dialogVisible = ref(false)
const form = ref({ name: '', icon: '', description: '', sortOrder: 0, color: '#409eff', status: 1 })

async function loadSubjects() {
  const res = await api.get('/api/admin/subjects')
  if (res.data.code === 0) subjects.value = res.data.data || []
}

function editSubject(s: any) {
  form.value = { ...s }
  dialogVisible.value = true
}

async function saveSubject() {
  const url = form.value.id ? `/api/admin/subjects/${form.value.id}` : '/api/admin/subjects'
  const method = form.value.id ? 'patch' : 'post'
  const res = await api[method](url, form.value)
  if (res.data.code === 0) { ElMessage.success('保存成功'); dialogVisible.value = false; loadSubjects() }
  else ElMessage.error(res.data.message || '保存失败')
}

async function deleteSubject(id: number) {
  await ElMessageBox.confirm('确认删除该学科？', '提示')
  const res = await api.delete(`/api/admin/subjects/${id}`)
  if (res.data.code === 0) { ElMessage.success('删除成功'); loadSubjects() }
}

onMounted(loadSubjects)
</script>
