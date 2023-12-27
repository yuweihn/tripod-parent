<template>
	<section>
		<!--工具条-->
		<el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
			<el-form :inline="true">
				<el-form-item label="关键字：">
					<el-input v-model="filters.keywords" clearable placeholder="" style="width: 255px;" />
				</el-form-item>
				<el-form-item>
					<el-button type="primary" v-on:click="getRoleList(1)" class="el-icon-search" v-hasPerm="['sys.role.list']"> 查询</el-button>
				</el-form-item>
				<el-form-item>
					<el-button type="primary" @click="$refs.createRole.show()" class="el-icon-edit" v-hasPerm="['sys.role.create']"> 新增</el-button>
				</el-form-item>
			</el-form>
		</el-col>

		<!--列表-->
		<el-table :data="roleList" highlight-current-row v-loading="listLoading" @selection-change="onSelsChanged" style="width: 100%;">
			<el-table-column type="expand" width="18">
				<template slot-scope="scope">
					<el-form label-position="left" inline class="table-expand">
						<el-form-item label="ID：">
							<span>{{scope.row.id}}</span>
						</el-form-item>
						<el-form-item label="角色编码：">
							<span>{{scope.row.roleNo}}</span>
						</el-form-item>
						<el-form-item label="角色名称：">
							<span>{{scope.row.roleName}}</span>
						</el-form-item>
						<el-form-item label="创建人：">
							<span>{{scope.row.creator}}</span>
						</el-form-item>
						<el-form-item label="创建时间：">
							<span>{{scope.row.createTime}}</span>
						</el-form-item>
						<el-form-item label="修改人：">
							<span>{{scope.row.modifier}}</span>
						</el-form-item>
						<el-form-item label="修改时间：">
							<span>{{scope.row.modifyTime}}</span>
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
				<template slot-scope="scope">
					<el-tooltip placement="top">
						<div slot="content">编辑</div>
						<el-button type="text" circle class="el-icon-edit" @click="$refs.editRole.show(scope.$index, scope.row)" v-hasPerm="['sys.role.update']" />
					</el-tooltip>
					<el-dropdown trigger="click" v-hasPerm="['sys.role.permission.list', 'sys.role.delete']">
						<el-tooltip placement="top">
							<div slot="content">更多</div>
							<el-button type="text" circle class="el-icon-more" />
						</el-tooltip>
						<el-dropdown-menu slot="dropdown">
							<div @click="$refs.rolePermission.show(scope.row.id, scope.row.roleName)">
							<el-dropdown-item>
								<el-tooltip placement="top">
									<div slot="content">角色权限</div>
									<el-button type="text" circle class="el-icon-wallet" v-hasPerm="['sys.role.permission.list']"> 角色权限</el-button>
								</el-tooltip>
							</el-dropdown-item>
							</div>
							<div @click="handleDel(scope.$index, scope.row)" v-hasPerm="['sys.role.delete']">
							<el-dropdown-item divided>
								<el-tooltip placement="top">
									<div slot="content">删除</div>
									<el-button type="text" circle class="el-icon-delete" v-hasPerm="['sys.role.delete']"> 删除</el-button>
								</el-tooltip>
							</el-dropdown-item>
							</div>
						</el-dropdown-menu>
					</el-dropdown>
				</template>
			</el-table-column>
			<el-table-column label="" />
		</el-table>

		<!--工具条-->
		<el-col :span="24" class="toolbar2">
			<el-button type="danger" @click="batchRemove" :disabled="this.sels.length === 0" class="el-icon-delete" v-hasPerm="['sys.role.delete']"> 批量删除</el-button>
			<el-pagination layout="total, sizes, prev, pager, next, jumper" background
						@size-change="handleSizeChange" @current-change="handleCurrentChange"
						:page-sizes="[10,20,50,100]" :current-page="pageNo" :page-size="pageSize" :total="total" style="float: right;" />
		</el-col>

		<!--新增界面-->
		<create-role ref="createRole" v-on:success="getRoleList"/>

		<!--编辑界面-->
		<edit-role ref="editRole" v-on:success="getRoleList"/>

		<role-permission ref="rolePermission"/>
	</section>
</template>

<script>
import CreateRole from './components/CreateRole';
import EditRole from './components/EditRole';
import RolePermission from "./RolePermission";

export default {
    components: {
        'create-role': CreateRole,
        'edit-role': EditRole,
        'role-permission': RolePermission
    },

    data() {
        return {
            filters: {
                keywords: null
            },

            total: 0,
            roleList: [],
            pageNo: 1,
            pageSize: 10,
            listLoading: false,
            sels: []    //列表选中列
        }
    },
    methods: {
        formatUpdater: function(row, column) {
            return row.updater || row.creator;
        },
        formatUpdateTime: function(row, column) {
            return this.$date.formatDate(row.updateTime || row.createTime, 'yyyy-MM-dd HH:mm:ss');
        },
        handleSizeChange(psize) {
            this.getRoleList(1, psize);
        },
        handleCurrentChange(pno) {
            this.getRoleList(pno, null);
        },
        onSelsChanged: function(sels) {
            this.sels = sels;
        },
        getRoleList(pno, psize) {
            if (pno != null) {
                this.pageNo = pno;
            }
            if (psize != null) {
                this.pageSize = psize;
            }
            var params = {keywords: this.filters.keywords, pageNo: this.pageNo, pageSize: this.pageSize};
            this.listLoading = true;
            this.$axios.get(this.$global.baseUrl + '/sys/role/list', {params: params}).then((res) => {
                if (res.data.code === '0000') {
                    this.total = res.data.data.size;
                    this.roleList = res.data.data.list;
                } else {
                    this.total = 0;
                    this.roleList = [];
                    this.$message.error(res.data.msg);
                }
                this.listLoading = false;
            }).catch((err) => {
                this.listLoading = false;
                this.$message.error(err.message);
            });
        },

        //删除
        handleDel: function(index, row) {
            this.sels = [];
            this.sels.push(row);
            this.batchRemove();
        },
        //批量删除
        batchRemove: function() {
            var ids = this.sels.map(item => item.id).toString();
            this.$confirm('确定删除选中记录吗？', '提示', {type: 'warning'}).then(() => {
                var params = {ids: ids};
                this.listLoading = true;
                this.$axios.delete(this.$global.baseUrl + '/sys/role/delete', {params: params}).then((res) => {
                    if (res.data.code === '0000') {
                        this.$message({type: "success", message: res.data.msg});
                    } else {
                        this.$message.error(res.data.msg);
                    }
                    this.removeDynamicLoaded();
                    this.listLoading = false;
                    this.getRoleList();
                }).catch((err) => {
                    this.listLoading = false;
                    this.$message.error(err.message);
                });
            }).catch(() => {

            });
            this.sels = [];
        }
    },
    mounted() {
        this.getRoleList();
    }
}
</script>

<style scoped>
.table-expand {
    font-size: 0;
}
.table-expand >>> label {
    color: #99a9bf;
}
.table-expand .el-form-item {
    margin-right: 0;
    margin-bottom: 0;
    width: 90%;
}
</style>

