<template>
	<section>
		<!--工具条-->
		<el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
			<el-form :inline="true" :model="filters">
				<el-form-item label="用户名：">
					<el-input v-model="filters.keywords" clearable placeholder="" style="width: 255px;"></el-input>
				</el-form-item>
				<el-form-item>
					<el-button type="primary" v-on:click="getAdminList(1)" :icon="Search" v-hasPerm="['sys.admin.list']"> 查询</el-button>
				</el-form-item>
				<el-form-item>
					<el-button type="primary" @click="$refs.createAdmin.show()" :icon="EditPen" v-hasPerm="['sys.admin.create']"> 新增</el-button>
				</el-form-item>
			</el-form>
		</el-col>

		<!--列表-->
		<el-table :data="adminList" highlight-current-row v-loading="listLoading" @selection-change="onSelsChanged" style="width: 100%;">
			<el-table-column type="expand" width="25">
			    <template #default="{row, $index}">
					<el-form label-position="left" inline class="table-expand">
						<el-form-item label="ID：">
							<span>{{row.id}}</span>
						</el-form-item>
						<el-form-item label="用户名：">
							<span>{{row.accountNo}}</span>
						</el-form-item>
						<el-form-item label="真实姓名：">
							<span>{{row.realName}}</span>
						</el-form-item>
						<el-form-item label="性别：">
							<span>{{row.genderName}}</span>
						</el-form-item>
						<el-form-item label="最近登录时间：">
							<span>{{row.lastLoginTime}}</span>
						</el-form-item>
						<el-form-item label="头像：">
							<el-image :src="row.avatar" style="width: 80px; height: 80px" :hide-on-click-modal="true" :preview-teleported="true"
								:preview-src-list="preview.showList" @click="showBigPict(row.avatar)"/>
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
			<el-table-column prop="accountNo" label="用户名" width="130" :show-overflow-tooltip="true" />
			<el-table-column prop="realName" label="真实姓名" width="130" :show-overflow-tooltip="true" />
			<el-table-column prop="genderName" label="性别" width="80" />
			<el-table-column prop="lastLoginTime" label="最近登录时间" width="145" />
			<el-table-column prop="createTime" label="创建时间" width="145" />
			<!--
			<el-table-column prop="updater" label="更新人" width="90" :formatter="formatUpdater" />
			<el-table-column prop="updateTime" label="更新时间" width="155" :formatter="formatUpdateTime" />
			-->
			<el-table-column label="操作" width="100">
                <template #default="{row, $index}">
					<el-tooltip content="编辑" placement="top">
						<el-button text circle :icon="EditPen" @click="$refs.editAdmin.show($index, row)" v-hasPerm="['sys.admin.update']" />
					</el-tooltip>
                    <el-tooltip content="更多" placement="top">
                        <el-dropdown trigger="click" v-hasPerm="['sys.admin.change.password', 'sys.admin.role.list', 'sys.admin.delete']">
                            <el-button text circle :icon="MoreFilled" />
                            <template #dropdown>
                                <el-dropdown-menu>
                                    <div @click="handlePassword($index, row)" v-hasPerm="['sys.admin.change.password']">
                                        <el-dropdown-item>
                                            <el-tooltip content="修改密码" placement="top">
                                                <el-button text circle :icon="Lock"> 修改密码</el-button>
                                            </el-tooltip>
                                        </el-dropdown-item>
                                    </div>
                                    <div @click="$refs.adminRole.show(row.id, row.accountNo)" v-hasPerm="['sys.admin.role.list']">
                                        <el-dropdown-item>
                                            <el-tooltip content="管理员角色" placement="top">
                                                <el-button text circle :icon="User"> 管理员角色</el-button>
                                            </el-tooltip>
                                        </el-dropdown-item>
                                    </div>
                                    <div @click="handleDel($index, row)" v-hasPerm="['sys.admin.delete']">
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
			<el-button type="danger" @click="batchRemove" :disabled="sels.length === 0" :icon="Delete" v-hasPerm="['sys.admin.delete']"> 批量删除</el-button>
			<el-pagination layout="total, sizes, prev, pager, next, jumper" background
						@size-change="handleSizeChange" @current-change="handleCurrentChange" :pager-count="5"
						:page-sizes="[10,20,50,100]" :current-page="pageNo" :page-size="pageSize" :total="total" style="float: right;" />
		</el-col>

		<!--新增界面-->
		<create-admin ref="createAdmin" v-on:success="getAdminList"/>

		<!--编辑界面-->
		<edit-admin ref="editAdmin" v-on:success="getAdminList"/>

		<admin-role ref="adminRole"/>

		<!--修改密码界面-->
		<el-dialog title="修改密码" v-model="pwdFormVisible" width="450px" :close-on-click-modal="true" draggable>
			<el-form :model="pwdForm" label-width="75px" ref="pwdFormRef" style="padding-right: 10px;">
				<el-form-item label="新密码" prop="password" :rules="[{required: true, message: '请输入新密码', trigger: 'blur'}]">
					<el-input v-model="pwdForm.password" clearable autocomplete="off" show-password style="width: 100%"/>
				</el-form-item>
			</el-form>
			<div slot="footer" class="dialog-footer" style="padding-right: 10px;">
				<el-button @click.native="pwdFormVisible = false">取消</el-button>
				<el-button type="primary" @click.native="pwdSubmit" :loading="pwdLoading" v-hasPerm="['sys.admin.change.password']">提交</el-button>
			</div>
		</el-dialog>
	</section>
</template>

<script setup>
import CreateAdmin from './components/CreateAdmin';
import EditAdmin from './components/EditAdmin';
import AdminRole from "./AdminRole";
import {Delete, EditPen, Search, MoreFilled, Lock, User} from '@element-plus/icons-vue';

const {proxy} = getCurrentInstance();
const filters = ref({
    keywords: null
});
const total = ref(0);
const adminList = ref([]);
const pageNo = ref(1);
const pageSize = ref(10);
const listLoading = ref(false);
const sels = ref([]);
const preview = ref({
    initList: [],
    showList: []
});

const pwdFormVisible = ref(false);
const pwdLoading = ref(false);
const pwdForm = ref({
    id: null,
    password: null
});

function formatUpdater(row, column) {
    return row.updater || row.creator;
}
function formatUpdateTime(row, column) {
    return proxy.$date.formatDate(row.updateTime || row.createTime, 'yyyy-MM-dd HH:mm:ss');
}
function handleSizeChange(psize) {
    getAdminList(1, psize);
}
function handleCurrentChange(pno) {
    getAdminList(pno, null);
}
function onSelsChanged(v) {
    sels.value = v;
}
function getAdminList(pno, psize) {
    if (pno != null) {
        pageNo.value = pno;
    }
    if (psize != null) {
        pageSize.value = psize;
    }
    var params = {keywords: filters.value.keywords, pageNo: pageNo.value, pageSize: pageSize.value};
    listLoading.value = true;
    proxy.request.get('/sys/admin/list', params).then((res) => {
        total.value = res.data.data.size;
        adminList.value = res.data.data.list;
        preview.value.initList = [];
        adminList.value.forEach(function(m) {
            if (m.avatar) {
                preview.value.initList.push(m.avatar);
            }
        });
        listLoading.value = false;
    }).catch((err) => {
        total.value = 0;
        adminList.value = [];
        preview.value.initList = [];
        listLoading.value = false;
    });
}
function handlePassword(index, row) {
    proxy.resetForm("pwdFormRef");
    pwdForm.value.id = row.id;
    pwdForm.value.password = null;
    pwdFormVisible.value = true;
}
function pwdSubmit() {
    proxy.$refs.pwdFormRef.validate((valid) => {
        if (valid) {
            var params = "accountId=" + pwdForm.value.id + "&password=" + proxy.$md5(pwdForm.value.password);
            pwdLoading.value = true;
            proxy.request.post('/sys/admin/change-password', params).then((res) => {
                proxy.$modal.msgSuccess(res.data.msg);
                proxy.$refs['pwdFormRef'].resetFields();
                pwdFormVisible.value = false;
                pwdLoading.value = false;
                getAdminList();
            }).catch((err) => {
                pwdLoading.value = false;
            });
        }
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
        listLoading.value = true;
        proxy.request.delete('/sys/admin/delete', params).then((res) => {
            proxy.$modal.msgSuccess(res.data.msg);
            listLoading.value = false;
            getAdminList();
        }).catch((err) => {
            listLoading.value = false;
        });
    }).catch(() => {

    });
    sels.value = [];
}
function showAdminRoles(adminId) {
    //proxy.$router.push({path: '/admin/' + adminId + '/roles'}, onComplete => {}, onAbort => {});
    var {href} = proxy.$router.resolve({path: '/admin/' + adminId + '/roles'});
    window.open(href, '_blank');
}
function showBigPict(img) {
    var idx = preview.value.initList.indexOf(img);
    if (idx <= 0) {
        preview.value.showList = preview.value.initList;
        return;
    }
    preview.value.showList = preview.value.initList.slice(idx, preview.value.initList.length).concat(preview.value.initList.slice(0, idx));
}

onMounted(() => {
    getAdminList();
})
</script>

<style scoped>

</style>

