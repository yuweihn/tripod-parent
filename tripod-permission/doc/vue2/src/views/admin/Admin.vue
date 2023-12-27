<template>
	<section>
		<!--工具条-->
		<el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
			<el-form :inline="true" :model="filters">
				<el-form-item label="用户名：">
					<el-input v-model="filters.keywords" clearable placeholder="" style="width: 255px;"></el-input>
				</el-form-item>
				<el-form-item>
					<el-button type="primary" v-on:click="getAdminList(1)" class="el-icon-search" v-hasPerm="['sys.admin.list']"> 查询</el-button>
				</el-form-item>
				<el-form-item>
					<el-button type="primary" @click="$refs.createAdmin.show()" class="el-icon-edit" v-hasPerm="['sys.admin.create']"> 新增</el-button>
				</el-form-item>
			</el-form>
		</el-col>

		<!--列表-->
		<el-table :data="adminList" highlight-current-row v-loading="listLoading" @selection-change="onSelsChanged" style="width: 100%;">
			<el-table-column type="expand" width="18">
				<template slot-scope="scope">
					<el-form label-position="left" inline class="table-expand">
						<el-form-item label="ID：">
							<span>{{scope.row.id}}</span>
						</el-form-item>
						<el-form-item label="用户名：">
							<span>{{scope.row.accountNo}}</span>
						</el-form-item>
						<el-form-item label="真实姓名：">
							<span>{{scope.row.realName}}</span>
						</el-form-item>
						<el-form-item label="性别：">
							<span>{{scope.row.genderName}}</span>
						</el-form-item>
						<el-form-item label="最近登录时间：">
							<span>{{scope.row.lastLoginTime}}</span>
						</el-form-item>
						<el-form-item label="头像：">
							<el-image :src="scope.row.avatar" style="width: 80px; height: 80px"
								:preview-src-list="preview.showList" @click="showBigPict(scope.row.avatar)"/>
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
				<template slot-scope="scope">
					<el-tooltip placement="top">
						<div slot="content">编辑</div>
						<el-button type="text" circle class="el-icon-edit" @click="$refs.editAdmin.show(scope.$index, scope.row)" v-hasPerm="['sys.admin.update']" />
					</el-tooltip>
					<el-dropdown trigger="click" v-hasPerm="['sys.admin.change.password', 'sys.admin.role.list', 'sys.admin.delete']">
						<el-tooltip placement="top">
							<div slot="content">更多</div>
							<el-button type="text" circle class="el-icon-more" />
						</el-tooltip>
						<el-dropdown-menu slot="dropdown">
							<div @click="handlePassword(scope.$index, scope.row)">
							<el-dropdown-item>
								<el-tooltip placement="top">
									<div slot="content">修改密码</div>
									<el-button type="text" circle class="el-icon-lock" v-hasPerm="['sys.admin.change.password']"> 修改密码</el-button>
								</el-tooltip>
							</el-dropdown-item>
							</div>
							<div @click="$refs.adminRole.show(scope.row.id, scope.row.accountNo)">
							<el-dropdown-item>
								<el-tooltip placement="top">
									<div slot="content">管理员角色</div>
									<el-button type="text" circle class="el-icon-user" v-hasPerm="['sys.admin.role.list']"> 管理员角色</el-button>
								</el-tooltip>
							</el-dropdown-item>
							</div>
							<div @click="handleDel(scope.$index, scope.row)" v-hasPerm="['sys.admin.delete']">
							<el-dropdown-item divided>
								<el-tooltip placement="top">
									<div slot="content">删除</div>
									<el-button type="text" circle class="el-icon-delete" v-hasPerm="['sys.admin.delete']"> 删除</el-button>
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
			<el-button type="danger" @click="batchRemove" :disabled="this.sels.length===0" class="el-icon-delete" v-hasPerm="['sys.admin.delete']"> 批量删除</el-button>
			<el-pagination layout="total, sizes, prev, pager, next, jumper" background
						@size-change="handleSizeChange" @current-change="handleCurrentChange"
						:page-sizes="[10,20,50,100]" :current-page="pageNo" :page-size="pageSize" :total="total" style="float: right;" />
		</el-col>

		<!--新增界面-->
		<create-admin ref="createAdmin" v-on:success="getAdminList"/>

		<!--编辑界面-->
		<edit-admin ref="editAdmin" v-on:success="getAdminList"/>

		<admin-role ref="adminRole"/>

		<!--修改密码界面-->
		<el-dialog title="修改密码" :visible.sync="pwdFormVisible" width="450px" :close-on-click-modal="true" v-drag>
			<el-form :model="pwdForm" label-width="75px" ref="pwdForm" style="padding-right: 10px;">
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

<script>
	import CreateAdmin from './components/CreateAdmin';
	import EditAdmin from './components/EditAdmin';
	import AdminRole from "./AdminRole";
	export default {
		components: {
			'create-admin': CreateAdmin,
			'edit-admin': EditAdmin,
			'admin-role': AdminRole
		},

		data() {
			return {
				filters: {
					keywords: null
				},
				total: 0,
				adminList: [],
				pageNo: 1,
				pageSize: 10,
				listLoading: false,
				sels: [],    //列表选中列

				preview: {
					initList: [],
					showList: []
				},

				pwdFormVisible: false,//修改密码界面是否显示
				pwdLoading: false,
				//修改密码界面数据
				pwdForm: {
					id: null,
					password: null
				}
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
				this.getAdminList(1, psize);
			},
			handleCurrentChange(pno) {
				this.getAdminList(pno, null);
			},
			onSelsChanged: function(sels) {
				this.sels = sels;
			},
			getAdminList(pno, psize) {
				if (pno != null) {
					this.pageNo = pno;
				}
				if (psize != null) {
					this.pageSize = psize;
				}
				var params = {keywords: this.filters.keywords, pageNo: this.pageNo, pageSize: this.pageSize};
				this.listLoading = true;
				let me = this;
				this.$axios.get(this.$global.baseUrl + '/sys/admin/list', {params: params}).then((res) => {
					if (res.data.code === '0000') {
						me.total = res.data.data.size;
						me.adminList = res.data.data.list;
						me.preview.initList = [];
						me.adminList.forEach(function(m) {
							if (m.avatar) {
								me.preview.initList.push(m.avatar);
							}
						});
					} else {
						me.total = 0;
						me.adminList = [];
						me.preview.initList = [];
						me.$message.error(res.data.msg);
					}
					me.listLoading = false;
				}).catch((err) => {
					me.listLoading = false;
					me.$message.error(err.message);
				});
			},

			//显示修改密码界面
			handlePassword: function(index, row) {
				this.pwdFormVisible = true;
				this.pwdForm = {
					id: row.id,
					password: null
				};
				this.resetForm("pwdForm");
			},
			//提交修改密码
			pwdSubmit: function() {
				this.$refs.pwdForm.validate((valid) => {
					if (valid) {
						var params = "accountId=" + this.pwdForm.id + "&password=" + this.$md5(this.pwdForm.password);
						this.pwdLoading = true;
						this.$axios.post(this.$global.baseUrl + '/sys/admin/change-password', params).then((res) => {
							if (res.data.code === '0000') {
								this.$message({type: "success", message: res.data.msg});
							} else {
								this.$message.error(res.data.msg);
							}
							this.$refs['pwdForm'].resetFields();
							this.pwdFormVisible = false;
							this.pwdLoading = false;
							this.getAdminList();
						}).catch((err) => {
							this.pwdLoading = false;
							this.$message.error(err.message);
						});
					}
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
					this.$axios.delete(this.$global.baseUrl + '/sys/admin/delete', {params: params}).then((res) => {
						if (res.data.code === '0000') {
							this.$message({type: "success", message: res.data.msg});
						} else {
							this.$message.error(res.data.msg);
						}
						this.listLoading = false;
						this.getAdminList();
					}).catch((err) => {
						this.listLoading = false;
						this.$message.error(err.message);
					});
				}).catch(() => {

				});
				this.sels = [];
			},
			//显示管理员角色
			showAdminRoles: function(adminId) {
				//this.$router.push({path: '/admin/' + adminId + '/roles'}, onComplete => {}, onAbort => {});
				var {href} = this.$router.resolve({path: '/admin/' + adminId + '/roles'});
				window.open(href, '_blank');
			},
			showBigPict(img) {
				var idx = this.preview.initList.indexOf(img);
				if (idx <= 0) {
					this.preview.showList = this.preview.initList;
					return;
				}
				this.preview.showList = this.preview.initList.slice(idx, this.preview.initList.length).concat(this.preview.initList.slice(0, idx));
			}
		},
		mounted() {
			this.getAdminList();
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

