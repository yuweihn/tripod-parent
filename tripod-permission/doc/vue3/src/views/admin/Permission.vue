<template>
	<section>
		<!--工具条-->
		<el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
			<el-form :inline="true">
				<el-form-item label="关键字：">
					<el-input v-model="filters.keywords" clearable placeholder="" style="width: 255px;" />
				</el-form-item>
                <el-form-item label="状态：">
                    <el-select v-model="filters.visible" placeholder="" clearable>
                        <el-option v-for="item in visibleList" :key="item.val" :label="item.label" :value="item.val" />
                    </el-select>
                </el-form-item>
				<el-form-item>
					<el-button type="primary" v-on:click="getPermissionList()" :icon="Search" v-hasPerm="['sys.permission.list']"> 查询</el-button>
				</el-form-item>
				<el-form-item>
					<el-button type="primary" @click="$refs.createPermission.show()" :icon="EditPen" v-hasPerm="['sys.permission.create']"> 新增</el-button>
				</el-form-item>
			</el-form>
		</el-col>

		<!--列表-->
		<el-table :data="permissionList" row-key="id" highlight-current-row v-loading="listLoading" @selection-change="onSelsChanged"
		            style="width: 100%;" :tree-props="{children: 'children', hasChildren: 'hasChildren'}">
			<el-table-column type="selection" width="30" />
			<el-table-column prop="id" label="ID" width="120" :show-overflow-tooltip="true" />
			<el-table-column prop="permNo" label="权限编码" width="150" :show-overflow-tooltip="true" />
			<el-table-column prop="title" label="标题" width="150" :show-overflow-tooltip="true" />
            <el-table-column prop="icon" label="图标" align="center" width="100">
                <template #default="{row, $index}">
                    <svg-icon v-if="row.icon" :icon-class="row.icon" />
                </template>
            </el-table-column>
            <el-table-column prop="orderNum" label="排序" width="60" />
            <el-table-column prop="path" label="路由地址" width="180" :show-overflow-tooltip="true" />
            <el-table-column prop="component" label="组件路径" width="180" :show-overflow-tooltip="true" />
			<el-table-column prop="visible" label="状态" width="80">
                <template #default="{row, $index}">
                    <el-switch v-model="row.visible" :active-value="true" :inactive-value="false"
                            active-color="#13ce66" inactive-color="#ff4949" disabled/>
				</template>
			</el-table-column>
			<el-table-column prop="creator" label="创建人" width="90" />
			<el-table-column prop="createTime" label="创建时间" width="145" />
			<el-table-column prop="modifier" label="更新人" width="90" />
			<el-table-column prop="modifyTime" label="更新时间" width="145" />

			<el-table-column label="操作" width="100" fixed="right">
                <template #default="{row, $index}">
					<el-tooltip content="编辑" placement="top">
						<el-button text circle :icon="EditPen" @click="$refs.editPermission.show($index, row)" v-hasPerm="['sys.permission.update']" />
					</el-tooltip>
                    <el-tooltip content="更多" placement="top">
                        <el-dropdown trigger="click" v-hasPerm="['sys.permission.delete']">
                            <el-button text circle :icon="MoreFilled" />
                            <template #dropdown>
                                <el-dropdown-menu>
                                    <div @click="handleDel($index, row)" v-hasPerm="['sys.permission.delete']">
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
			<el-button type="danger" @click="batchRemove" :disabled="sels.length === 0" :icon="Delete" v-hasPerm="['sys.permission.delete']"> 批量删除</el-button>
            <el-button type="success" v-on:click="doExport" :icon="Coin" v-hasPerm="['sys.permission.export']"> 导出</el-button>
            <el-button type="danger" v-on:click="doImport" :icon="CirclePlus" v-hasPerm="['sys.permission.import']"> 导入</el-button>
		</el-col>

		<!--新增界面-->
		<create-permission ref="createPermission" v-on:success="getPermissionList"/>

		<!--编辑界面-->
		<edit-permission ref="editPermission" v-on:success="getPermissionList"/>

        <file-upload ref="importData" :title="'导入外部数据'" :fileLabel="'文件'" :fileTips="'只接受*.json格式，最大不要超过2MB'"
                  :accept="'.json'" :maxSize="2097152" :fileType="'text'"
                  :actionUrl="'/sys/permission/import'" v-on:change="onImportPost" />
	</section>
</template>

<script setup>
import CreatePermission from './components/CreatePermission';
import EditPermission from './components/EditPermission';
import {Search, EditPen, MoreFilled, Coin, CirclePlus, Delete} from '@element-plus/icons-vue';

const {proxy} = getCurrentInstance();
const filters = ref({
    keywords: null,
    visible: null
});
const visibleList = ref([
    {
        "val": true,
        "label": "显示"
    },
    {
        "val": false,
        "label": "隐藏"
    }
]);
const permissionList = ref([]);
const listLoading = ref(false);
const sels = ref([]);

function onSelsChanged(v) {
    sels.value = v;
}
function getPermissionList() {
    var params = {
        keywords: filters.value.keywords ? filters.value.keywords : null,
        visible: filters.value.visible != null && filters.value.visible !== '' ? filters.value.visible : null
    };
    listLoading.value = true;
    proxy.request.get('/sys/permission/list', params).then((res) => {
        permissionList.value = res.data.data;
        listLoading.value = false;
    }).catch((err) => {
        permissionList.value = [];
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
        proxy.request.delete('/sys/permission/delete', {ids: ids}).then((res) => {
            proxy.$modal.msgSuccess(res.data.msg);
            proxy.removeDynamicLoaded();
            listLoading.value = false;
            getPermissionList();
        }).catch((err) => {
            listLoading.value = false;
        });
    }).catch((err) => {});
    sels.value = [];
}
function doExport() {
    proxy.$modal.confirm('导出确认！', '提示', {type: 'warning'}).then(() => {
        listLoading.value = true;
        proxy.request.post('/sys/permission/export', null, {responseType: 'blob'}).then((res) => {
            if (res.headers["content-type"].indexOf("application/octet-stream") === 0) {
                const fileName = res.headers["_filename"];
                if (fileName) {
                    proxy.fileDownload(res.data, decodeURIComponent(fileName));
                } else {
                    proxy.$modal.msgError("Error");
                }
            } else if (res.headers["content-type"].indexOf("application/json") === 0) {
                const reader = new FileReader();
                reader.onload = function() {
                    var dt = JSON.parse(reader.result);
                    if (dt.code === proxy.errorCode.success.code) {
                        proxy.$modal.msgSuccess(dt.msg);
                    } else {
                        proxy.$modal.msgError(dt.msg);
                    }
                }
                reader.readAsText(res.data, 'utf-8');
            } else {
                proxy.$modal.msgError("Invalid Content Type!!");
            }
            listLoading.value = false;
        }).catch((err) => {
            listLoading.value = false;
        });
    }).catch((err) => {});
}
function doImport() {
    proxy.$refs.importData.show("hehe");
}
function onImportPost(key, res) {
    proxy.removeDynamicLoaded();
    proxy.$modal.msgSuccess(res.data.msg);
    getPermissionList();
}

onMounted(() => {
    getPermissionList();
})
</script>

<style scoped>

</style>

