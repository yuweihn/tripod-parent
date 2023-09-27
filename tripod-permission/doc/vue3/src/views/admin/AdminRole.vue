<template>
<el-dialog :title="title" v-model="formVisible" width="700px" :close-on-click-modal="true" append-to-body draggable>
	<section>
		<!--工具条-->
		<el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
			<el-form :inline="true" style="padding-bottom: 0px;">
				<el-form-item label="关键字：">
					<el-input v-model="filters.keywords" clearable placeholder="" style="width: 200px;" />
				</el-form-item>
				<el-form-item>
					<el-button type="primary" v-on:click="getAdminRoleList(1)" :icon="Search" v-hasPerm="['sys.admin.role.list']"> 查询</el-button>
				</el-form-item>
				<el-form-item>
					<el-button type="primary" @click="$refs.createAdminRole.show(adminId)" :icon="EditPen" v-hasPerm="['sys.admin.role.create']"> 新增</el-button>
				</el-form-item>
			</el-form>
		</el-col>

		<!--列表-->
		<el-table :data="adminRoleList" highlight-current-row v-loading="loading" @selection-change="onSelsChanged" style="width: 100%;">
			<el-table-column type="expand" width="25">
			    <template #default="{row, $index}">
					<el-form label-position="left" inline class="table-expand">
						<el-form-item label="ID：">
							<span>{{row.id}}</span>
						</el-form-item>
						<el-form-item label="角色：">
							<span>{{row.roleId}} - {{row.roleNo}} - {{row.roleName}}</span>
						</el-form-item>
						<el-form-item label="创建人：">
							<span>{{row.creator}}</span>
						</el-form-item>
						<el-form-item label="创建时间：">
							<span>{{row.createTime}}</span>
						</el-form-item>
						<el-form-item label="修改人：">
							<span>{{row.modifier}}</span>
						</el-form-item>
						<el-form-item label="修改时间：">
							<span>{{row.modifyTime}}</span>
						</el-form-item>
					</el-form>
				</template>
			</el-table-column>
			<el-table-column type="selection" width="30" />
			<!--
			<el-table-column prop="id" label="ID" width="90" sortable />
			<el-table-column prop="roleId" label="角色ID" width="90" />
			-->
			<el-table-column prop="roleNo" label="角色编码" :show-overflow-tooltip="true" />
			<el-table-column prop="roleName" label="角色名" :show-overflow-tooltip="true">
                <template #default="{row, $index}">
					<a href="javascript:void(0);" @click="$refs.rolePermission.show(row.roleId, row.roleName)">{{row.roleName}}</a>
				</template>
			</el-table-column>
			<!--
			<el-table-column prop="updater" label="更新人" width="90" :formatter="formatUpdater">
			</el-table-column>
			<el-table-column prop="updateTime" label="更新时间" width="155" :formatter="formatUpdateTime">
			</el-table-column>
			-->
			<el-table-column label="操作" width="100" fixed="right">
                <template #default="{row, $index}">
					<el-tooltip content="编辑" placement="top">
						<el-button text circle :icon="EditPen" @click="$refs.editAdminRole.show(adminId, $index, row)" v-hasPerm="['sys.admin.role.update']" />
					</el-tooltip>
                    <el-tooltip content="更多" placement="top">
                        <el-dropdown trigger="click" v-hasPerm="['sys.admin.role.delete']">
                            <el-button text circle :icon="MoreFilled" />
                            <template #dropdown>
                                <el-dropdown-menu>
                                    <div @click="handleDel($index, row)" v-hasPerm="['sys.admin.role.delete']">
                                        <el-dropdown-item>
                                            <el-tooltip content="删除" placement="top">
                                                <el-button text circle :icon="Delete"> 删除</el-button>
                                            </el-tooltip>
                                        </el-dropdown-item>
                                    </div>
                                </el-dropdown-menu>
                            </template>
                        </el-dropdown>
                    </el-tooltip>
				</template>
			</el-table-column>
			<el-table-column label="" />
		</el-table>

		<!--工具条-->
		<el-col :span="24" class="toolbar2">
			<el-button type="danger" @click="batchRemove" :disabled="sels.length === 0" :icon="Delete" v-hasPerm="['sys.admin.role.delete']"> 批量删除</el-button>
			<el-pagination layout="total, sizes, prev, pager, next, jumper" background
						@size-change="handleSizeChange" @current-change="handleCurrentChange" :pager-count="5"
						:page-sizes="[10,20,50,100]" :current-page="pageNo" :page-size="pageSize" :total="total" style="float: right;" />

		</el-col>

		<!--新增界面-->
		<create-admin-role ref="createAdminRole" v-on:success="getAdminRoleList"/>

		<!--编辑界面-->
		<edit-admin-role ref="editAdminRole" v-on:success="getAdminRoleList"/>

		<role-permission ref="rolePermission"/>
	</section>
</el-dialog>
</template>

<script setup>
import CreateAdminRole from './components/CreateAdminRole';
import EditAdminRole from './components/EditAdminRole';
import RolePermission from "./RolePermission";
import {Delete, EditPen, Search, MoreFilled} from '@element-plus/icons-vue';

const {proxy} = getCurrentInstance();
const adminId = ref(null);
const formVisible = ref(false);
const loading = ref(false);
const title = ref(null);
const filters = ref({
    keywords: null
});
const total = ref(0);
const adminRoleList = ref([]);
const pageNo = ref(1);
const pageSize = ref(10);
const sels = ref([]);

function show(aId, accountNo) {
    adminId.value = aId;
    title.value = accountNo == null ? '管理员角色' : '管理员角色 - ' + accountNo;
    loading.value = false;
    getAdminRoleList();
    formVisible.value = true;
}
function handleSizeChange(psize) {
    getAdminRoleList(1, psize);
}
function handleCurrentChange(pno) {
    getAdminRoleList(pno, null);
}
function onSelsChanged(v) {
    sels.value = v;
}
function getAdminRoleList(pno, psize) {
    if (pno != null) {
        pageNo.value = pno;
    }
    if (psize != null) {
        pageSize.value = psize;
    }
    var params = {adminId: adminId.value, keywords: filters.value.keywords, pageNo: pageNo.value, pageSize: pageSize.value};
    loading.value = true;
    proxy.request.get('/sys/admin/role/list', params).then((res) => {
        total.value = res.data.data.size;
        adminRoleList.value = res.data.data.list;
        loading.value = false;
    }).catch((err) => {
        total.value = 0;
        adminRoleList.value = [];
        loading.value = false;
    });
}
function handleDel(index, row) {
    sels.value = [];
    sels.value.push(row);
    batchRemove();
}
function batchRemove() {
    var ids = sels.value.map(item => item.id).toString();
    proxy.$modal.confirm('确定删除选中记录吗?', '提示', {type: 'warning'}).then(function() {
        var params = {ids: ids};
        loading.value = true;
        proxy.request.delete('/sys/admin/role/delete', params).then((res) => {
            proxy.$modal.msgSuccess(res.data.msg);
            proxy.removeDynamicLoaded();
            loading.value = false;
            getAdminRoleList();
        }).catch((err) => {
            loading.value = false;
        });
    }).catch(() => {

    });
    sels.value = [];
}
defineExpose({
    show
})
</script>

<style scoped>
a {
    text-decoration: none;
    color: #5cb6ff;
}
a:hover {
    color: #ff0000;
}
</style>

