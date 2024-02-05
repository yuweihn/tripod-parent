<template>
	<el-dialog title="编辑角色" :visible.sync="editFormVisible" width="450px" :close-on-click-modal="true" append-to-body v-drag>
		<el-form :model="editForm" label-width="90px" :rules="editFormRules" ref="editForm" style="padding-right: 10px;">
			<el-form-item label="角色编码" prop="roleNo">
				<el-input v-model="editForm.roleNo" clearable autocomplete="off" style="width: 100%" />
			</el-form-item>
			<el-form-item label="角色名称" prop="roleName">
				<el-input v-model="editForm.roleName" clearable autocomplete="off" style="width: 100%" />
			</el-form-item>
		</el-form>
		<div slot="footer" class="dialog-footer" style="padding-right: 10px;">
			<el-button @click.native="editFormVisible = false">取消</el-button>
			<el-button type="primary" @click.native="editSubmit" :loading="editLoading" v-hasPerm="['sys.role.update']">提交</el-button>
		</div>
	</el-dialog>
</template>

<script>
export default {
    data() {
        return {
            editFormVisible: false,//编辑界面是否显示
            editLoading: false,
            editFormRules: {
                roleNo: [
                    {required: true, message: '请输入角色编码', trigger: 'blur'}
                ],
                roleName: [
                    {required: true, message: '请输入角色名称', trigger: 'blur'}
                ]
            },
            //编辑界面数据
            editForm: {
                id: null,
                roleNo: null,
                roleName: null
            }
        }
    },
    methods: {
        show: function(index, row) {
            this.editFormVisible = true;
            //this.editForm = Object.assign({}, row);
            this.editForm = {
                id: row.id,
                roleNo: row.roleNo,
                roleName: row.roleName
            };
            this.resetForm("editForm");
        },

        //编辑
        editSubmit: function() {
            this.$refs.editForm.validate((valid) => {
                if (valid) {
                    var params = "_fk=u" + "&id=" + this.editForm.id
                             + (this.editForm.roleNo ? "&roleNo=" + this.editForm.roleNo : "")
                             + (this.editForm.roleName ? "&roleName=" + this.editForm.roleName : "");
                    this.editLoading = true;
                    this.$axios.post(this.$global.baseUrl + '/sys/role/update', params).then((res) => {
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
        }
    },
    mounted() {

    }
}
</script>

<style scoped>

</style>

