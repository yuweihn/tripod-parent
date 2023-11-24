<template>
	<section>
		<!--工具条-->
		<el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
			<el-form :inline="true">
				<el-form-item label="关键字：">
					<el-input v-model="filters.keywords" clearable placeholder="" style="width: 255px;" />
				</el-form-item>
				<el-form-item>
					<el-button type="primary" v-on:click="getRoleList(1)" :icon="Search" v-hasPerm="['sys.role.list']"> 查询</el-button>
				</el-form-item>
				<el-form-item>
					<el-button type="primary" @click="$refs.createRole.show()" :icon="EditPen" v-hasPerm="['sys.role.create']"> 新增</el-button>
				</el-form-item>
			</el-form>
		</el-col>

		<!--列表-->
		<el-table :data="roleList" highlight-current-row v-loading="listLoading" @selection-change="onSelsChanged" style="width: 100%;">
			<el-table-column type="expand" width="25">
			    <template #default="{row, $index}">
					<el-form label-position="left" inline class="table-expand">
						<el-form-item label="ID：">
							<span>{{row.id}}</span>
						</el-form-item>
						<el-form-item label="角色编码：">
							<span>{{row.roleNo}}</span>
						</el-form-item>
						<el-form-item label="角色名称：">
							<span>{{row.roleName}}</span>
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
			<el-table-column prop="id" label="ID" width="80" sortable />
			<el-table-column prop="roleNo" label="角色编码" width="150" :show-overflow-tooltip="true" />
			<el-table-column prop="roleName" label="角色名称" width="150" :show-overflow-tooltip="true" />
			<!--
			<el-table-column prop="updater" label="更新人" width="90" :formatter="formatUpdater" :show-overflow-tooltip="true" />
			<el-table-column prop="updateTime" label="更新时间" width="155" :formatter="formatUpdateTime" :show-overflow-tooltip="true" />
			-->
			<el-table-column label="操作" width="180">
                <template #default="{row, $index}">
					<el-tooltip content="编辑" placement="top">
						<el-button text circle :icon="EditPen" @click="$refs.editRole.show($index, row)" v-hasPerm="['sys.role.update']" />
					</el-tooltip>
                    <el-tooltip content="更多" placement="top">
                        <el-dropdown trigger="click" v-hasPerm="['sys.role.permission.list', 'sys.role.delete']">
                            <el-button text circle :icon="MoreFilled" />
                            <template #dropdown>
                                <el-dropdown-menu>
                                    <div @click="$refs.rolePermission.show(row.id, row.roleName)"  v-hasPerm="['sys.role.permission.list']">
                                        <el-dropdown-item>
                                            <el-tooltip content="角色权限" placement="top">
                                                <el-button text circle :icon="Wallet"> 角色权限</el-button>
                                            </el-tooltip>
                                        </el-dropdown-item>
                                    </div>
                                    <div @click="handleDel($index, row)" v-hasPerm="['sys.role.delete']">
                                        <el-dropdown-item divided>
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
			<el-button type="danger" @click="batchRemove" :disabled="sels.length === 0" :icon="Delete" v-hasPerm="['sys.role.delete']"> 批量删除</el-button>
			<el-pagination layout="total, sizes, prev, pager, next, jumper" background
						@size-change="handleSizeChange" @current-change="handleCurrentChange" :pager-count="5"
						:page-sizes="[10,20,50,100]" :current-page="pageNo" :page-size="pageSize" :total="total" style="float: right;" />
		</el-col>

		<!--新增界面-->
		<create-role ref="createRole" v-on:success="getRoleList"/>

		<!--编辑界面-->
		<edit-role ref="editRole" v-on:success="getRoleList"/>

		<role-permission ref="rolePermission"/>
	</section>
</template>

<script setup>
import CreateRole from './components/CreateRole';
import EditRole from './components/EditRole';
import RolePermission from "./RolePermission";
import {Delete, EditPen, Search, MoreFilled, Wallet} from '@element-plus/icons-vue';

const {proxy} = getCurrentInstance();
const filters = ref({
    keywords: null
});
const total = ref(0);
const roleList = ref([]);
const pageNo = ref(1);
const pageSize = ref(10);
const listLoading = ref(false);
const sels = ref([]);

function formatUpdater(row, column) {
    return row.updater || row.creator;
}
function formatUpdateTime(row, column) {
    return proxy.$date.formatDate(row.updateTime || row.createTime, 'yyyy-MM-dd HH:mm:ss');
}
function onSelsChanged(v) {
    sels.value = v;
}
function handleSizeChange(psize) {
    getRoleList(1, psize);
}
function handleCurrentChange(pno) {
    getRoleList(pno, null);
}
function getRoleList(pno, psize) {
    if (pno != null) {
        pageNo.value = pno;
    }
    if (psize != null) {
        pageSize.value = psize;
    }
    var params = {
        keywords: filters.value.keywords ? filters.value.keywords : null,
        pageNo: pageNo.value,
        pageSize: pageSize.value
    };
    listLoading.value = true;
    proxy.request.get('/sys/role/list', params).then((res) => {
        total.value = res.data.data.size;
        roleList.value = res.data.data.list;
        listLoading.value = false;
    }).catch((err) => {
        total.value = 0;
        roleList.value = [];
        listLoading.value = false;
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
        listLoading.value = true;
        proxy.request.delete('/sys/role/delete', {ids: ids}).then((res) => {
            proxy.$modal.msgSuccess(res.data.msg);
            proxy.removeDynamicLoaded();
            listLoading.value = false;
            getRoleList();
        }).catch((err) => {
            listLoading.value = false;
        });
    }).catch((err) => {});
    sels.value = [];
}

onMounted(() => {
    getRoleList();
})
</script>

<style scoped>

</style>

