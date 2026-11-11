<template>
  <AdminLayout>
    <el-card><template #header><span>角色管理</span></template>
      <el-button type="primary" style="margin-bottom:16px" @click="showCreate">新增角色</el-button>
      <el-table :data="roles" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="code" label="编码" width="150" />
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="description" label="描述" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button size="small" @click="editPermissions(row)">权限配置</el-button>
            <el-button size="small" type="danger" @click="deleteRole(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="permVisible" title="权限配置" width="600px">
      <el-tree
        :data="permissionTree"
        :props="{ children: 'children', label: 'name' }"
        node-key="code"
        show-checkbox
        default-checked-keys="checkedPerms"
        ref="treeRef"
      />
      <template #footer>
        <el-button @click="permVisible = false">取消</el-button>
        <el-button type="primary" @click="savePermissions">保存</el-button>
      </template>
    </el-dialog>
  </AdminLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import AdminLayout from '../LayoutView.vue'
import api from '@/api'

const roles = ref<any[]>([])
const permVisible = ref(false)
const treeRef = ref()

const permissionGroups = [
  { label: '仪表盘', children: [{ code: 'dashboard:view', name: '查看' }] },
  { label: '学科管理', children: [{ code: 'subject:view', name: '查看' }, { code: 'subject:add', name: '新增' }, { code: 'subject:edit', name: '编辑' }, { code: 'subject:delete', name: '删除' }] },
  { label: '课程管理', children: [{ code: 'course:view', name: '查看' }, { code: 'course:add', name: '新增' }, { code: 'course:edit', name: '编辑' }, { code: 'course:delete', name: '删除' }, { code: 'course:import', name: '导入' }, { code: 'course:export', name: '导出' }] },
  { label: '题库管理', children: [{ code: 'question:view', name: '查看' }, { code: 'question:add', name: '新增' }, { code: 'question:edit', name: '编辑' }, { code: 'question:delete', name: '删除' }, { code: 'question:import', name: '导入' }, { code: 'question:export', name: '导出' }] },
  { label: '面试题库', children: [{ code: 'interview_question:view', name: '查看' }, { code: 'interview_question:add', name: '新增' }, { code: 'interview_question:edit', name: '编辑' }, { code: 'interview_question:delete', name: '删除' }, { code: 'interview_question:import', name: '导入' }] },
  { label: '题库审核', children: [{ code: 'question:review', name: '审核' }] },
  { label: '路线管理', children: [{ code: 'route:view', name: '查看' }, { code: 'route:add', name: '新增' }, { code: 'route:edit', name: '编辑' }, { code: 'route:delete', name: '删除' }] },
  { label: '学员管理', children: [{ code: 'student:view', name: '查看' }, { code: 'student:edit', name: '编辑' }, { code: 'student:delete', name: '删除' }, { code: 'student:export', name: '导出' }] },
  { label: '系统管理', children: [{ code: 'user:view', name: '查看' }, { code: 'user:add', name: '新增' }, { code: 'user:edit', name: '编辑' }, { code: 'user:delete', name: '删除' }, { code: 'role:view', name: '查看' }, { code: 'role:add', name: '新增' }, { code: 'role:edit', name: '编辑' }, { code: 'role:delete', name: '删除' }, { code: 'log:view', name: '查看' }, { code: 'log:export', name: '导出' }, { code: 'log:clear', name: '清空' }, { code: 'setting:edit', name: '修改设置' }] },
]

const checkedPerms = ref<string[]>([])

async function loadRoles() {
  const res = await api.get('/api/admin/roles')
  if (res.data.code === 0) roles.value = res.data.data || []
}

function showCreate() { /* TODO: 新增角色弹窗 */ }
function editPermissions(role: any) {
  checkedPerms.value = role.permissions || []
  permVisible.value = true
}

async function savePermissions() {
  const keys = treeRef.value?.getCheckedKeys() || []
  // TODO: 调用 API 保存
  permVisible.value = false
}

async function deleteRole(id: number) {
  await ElMessageBox.confirm('确认删除该角色？', '提示')
  const res = await api.delete(`/api/admin/roles/${id}`)
  if (res.data.code === 0) { ElMessage.success('删除成功'); loadRoles() }
}

onMounted(loadRoles)
</script>
