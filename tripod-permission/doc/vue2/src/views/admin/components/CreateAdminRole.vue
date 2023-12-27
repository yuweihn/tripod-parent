<template>
	<el-dialog title="新增" :visible.sync="addFormVisible" width="500px" :close-on-click-modal="true"
					append-to-body v-drag>
		<el-form :model="addForm" label-width="70px" :rules="addFormRules" ref="addForm" style="padding-right: 10px;">
			<el-form-item label="角色" prop="roleId">
				<el-select v-model="addForm.roleId" placeholder="请输入" @change="onRoleChanged($event)"
							:remote-method="initRoleList" clearable filterable remote style="width: 100%">
					<el-option v-for="item in roleSlt.result.list" :key="item.val" :label="item.label" :value="item.val" />
					<el-pagination layout="total, prev, pager, next" hide-on-single-page @current-change="onRolePageChanged"
						:current-page="roleSlt.pageNo" :page-size="roleSlt.pageSize" :total="roleSlt.result.size"/>
				</el-select>
			</el-form-item>
		</el-form>
		<div slot="footer" class="dialog-footer" style="padding-right: 10px;">
			<el-button @click.native="addFormVisible = false">取消</el-button>
			<el-button type="primary" @click.native="addSubmit" :loading="addLoading" v-hasPerm="['sys.admin.role.create']">提交</el-button>
		</div>
	</el-dialog>
</template>

<script>
export default {
    data() {
        return {
            adminId: null,

            addFormVisible: false,//新增界面是否显示
            addLoading: false,
            addFormRules: {
                roleId: [
                    {required: true, message: '请选择角色', trigger: 'blur'}
                ]
            },
            //新增界面数据
            addForm: {
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
        show: function(adminId) {
            this.adminId = adminId;
            this.addFormVisible = true;
            this.addForm = {
                roleId: null
            };
            this.roleSlt.result.size = 0;
            this.roleSlt.result.list = [];
            this.initRoleList();
            this.resetForm("addForm");
        },
        //新增
        addSubmit: function() {
            this.$refs.addForm.validate((valid) => {
                if (valid) {
                    var params = "adminId=" + this.adminId + "&roleId=" + this.addForm.roleId;
                    this.addLoading = true;
                    this.$axios.post(this.$global.baseUrl + '/sys/admin/role/add', params).then((res) => {
                        if (res.data.code === '0000') {
                            this.$message({type: "success", message: res.data.msg});
                        } else {
                            this.$message.error(res.data.msg);
                        }
                        this.removeDynamicLoaded();
                        this.$refs['addForm'].resetFields();
                        this.addFormVisible = false;
                        this.addLoading = false;
                        this.$emit("success", 1);
                    }).catch((err) => {
                        this.addLoading = false;
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

