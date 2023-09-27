<template>
	<el-dialog title="新增角色" :visible.sync="addFormVisible" width="450px" :close-on-click-modal="true" append-to-body v-drag>
		<el-form :model="addForm" label-width="90px" :rules="addFormRules" ref="addForm" style="padding-right: 10px;">
			<el-form-item label="角色编码" prop="roleNo">
				<el-input v-model="addForm.roleNo" clearable autocomplete="off" style="width: 100%" />
			</el-form-item>
			<el-form-item label="角色名称" prop="roleName">
				<el-input v-model="addForm.roleName" clearable autocomplete="off" style="width: 100%"/>
			</el-form-item>
		</el-form>
		<div slot="footer" class="dialog-footer" style="padding-right: 10px;">
			<el-button @click.native="addFormVisible = false">取消</el-button>
			<el-button type="primary" @click.native="addSubmit" :loading="addLoading" v-hasPerm="['sys.role.create']">提交</el-button>
		</div>
	</el-dialog>
</template>

<script>
export default {
    data() {
        return {
            addFormVisible: false,//新增界面是否显示
            addLoading: false,
            addFormRules: {
                roleNo: [
                    {required: true, message: '请输入角色编码', trigger: 'blur'}
                ],
                roleName: [
                    {required: true, message: '请输入角色名称', trigger: 'blur'}
                ]
            },
            //新增界面数据
            addForm: {
                roleNo: null,
                roleName: null
            }
        }
    },
    methods: {
        show: function() {
            this.addFormVisible = true;
            this.addForm = {
                roleNo: null,
                roleName: null
            };
            this.resetForm("addForm");
        },
        //新增
        addSubmit: function() {
            this.$refs.addForm.validate((valid) => {
                if (valid) {
                    var params = "_fk=u"
                             + (this.addForm.roleNo ? "&roleNo=" + this.addForm.roleNo : "")
                             + (this.addForm.roleName ? "&roleName=" + this.addForm.roleName : "");
                    this.addLoading = true;
                    this.$axios.post(this.$global.baseUrl + '/sys/role/create', params).then((res) => {
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
        }
    },
    mounted() {

    }
}
</script>

<style scoped>

</style>

