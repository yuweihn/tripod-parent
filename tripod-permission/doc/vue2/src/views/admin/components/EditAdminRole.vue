<template>
	<el-dialog title="编辑" :visible.sync="editFormVisible" width="500px" :close-on-click-modal="true"
					append-to-body v-drag>
		<el-form :model="editForm" label-width="70px" :rules="editFormRules" ref="editForm" style="padding-right: 10px;">
			<el-form-item label="角色" prop="roleId">
				<el-select v-model="editForm.roleId" placeholder="请输入" @change="onRoleChanged($event)"
							:remote-method="initRoleList" clearable filterable remote style="width: 100%">
					<el-option v-for="item in roleSlt.result.list" :key="item.val" :label="item.label" :value="item.val" />
					<el-pagination layout="total, prev, pager, next" hide-on-single-page @current-change="onRolePageChanged"
						:current-page="roleSlt.pageNo" :page-size="roleSlt.pageSize" :total="roleSlt.result.size"/>
				</el-select>
			</el-form-item>
		</el-form>
		<div slot="footer" class="dialog-footer" style="padding-right: 10px;">
			<el-button @click.native="editFormVisible = false">取消</el-button>
			<el-button type="primary" @click.native="editSubmit" :loading="editLoading" v-hasPerm="['sys.admin.role.update']">提交</el-button>
		</div>
	</el-dialog>
</template>

<script>
export default {
    data() {
        return {
            adminId: null,

            editFormVisible: false,//编辑界面是否显示
            editLoading: false,
            editFormRules: {
                roleId: [
                    {required: true, message: '请选择角色', trigger: 'blur'}
                ]
            },
            //编辑界面数据
            editForm: {
                id: null,
                roleId: null
            },

            roleSlt: {
                result: {
                    size: 0,
                    list: []
                },
                keywords: "",
                pageNo: 1,
                pageSize: 10
            }
        }
    },
    methods: {
        show: function(adminId, index, row) {
            this.adminId = adminId;
            this.editFormVisible = true;
            //this.editForm = Object.assign({}, row);
            this.editForm = {
                id: row.id,
                roleId: row.roleId
            };

            this.roleSlt.result.size = 1;
            this.roleSlt.result.list = [
                {
                    val: row.roleId,
                    label: row.roleName
                }
            ];
            this.resetForm("editForm");
        },

        //编辑
        editSubmit: function() {
            this.$refs.editForm.validate((valid) => {
                if (valid) {
                    var params = "id=" + this.editForm.id + "&adminId=" + this.adminId + "&roleId=" + this.editForm.roleId;
                    this.editLoading = true;
                    this.$axios.post(this.$global.baseUrl + '/sys/admin/role/update', params).then((res) => {
                        if (res.data.code === '0000') {
                            this.$message({type: "success", message: res.data.msg});
                        } else {
                            this.$message.error(res.data.msg);
                        }
                        this.removeDynamicLoaded();
                        this.$refs['editForm'].resetFields();
                        this.editFormVisible = false;
                        this.editLoading = false;
                        this.$emit("success", 1);
                    }).catch((err) => {
                        this.editLoading = false;
                        this.$message.error(err.message);
                    });
                }
            });
        },

        initRoleList(keywords) {
            this.roleSlt.keywords = keywords;
            this.onRolePageChanged(1, 10);
        },
        onRolePageChanged(pno, psize) {
            if (pno != null) {
                this.roleSlt.pageNo = pno;
            }
            if (psize != null) {
                this.roleSlt.pageSize = psize;
            }
            var params = {keywords: this.roleSlt.keywords, pageNo: this.roleSlt.pageNo, pageSize: this.roleSlt.pageSize};
            this.$axios.get(this.$global.baseUrl + '/sys/role/list', {params: params}).then((res) => {
                if (res.data.code === '0000') {
                    this.roleSlt.result.size = res.data.data.size;
                    this.roleSlt.result.list = res.data.data.list.map(item => {
                        return {val: item.id, label: item.roleName};
                    });
                } else {
                    this.roleSlt.result.size = 0;
                    this.roleSlt.result.list = [];
                    this.$message.error(res.data.msg);
                }
            }).catch((err) => {
                this.$message.error(err.message);
            });
        },
        onRoleChanged(roleId) {
            if (roleId) {
                return;
            }
            this.initRoleList();
        }
    },
    mounted() {

    }
}
</script>

<style scoped>

</style>

