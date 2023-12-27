<template>
	<el-dialog title="编辑管理员" :visible.sync="editFormVisible" width="450px" :close-on-click-modal="true" append-to-body v-drag>
		<el-form :model="editForm" label-width="90px" :rules="editFormRules" ref="editForm" style="padding-right: 10px;">
			<el-form-item label="用户名" prop="accountNo">
				<el-input v-model="editForm.accountNo" clearable autocomplete="off" :disabled="true" style="width: 100%"/>
			</el-form-item>
			<el-form-item label="真实姓名" prop="realName">
				<el-input v-model="editForm.realName" clearable autocomplete="off" style="width: 100%"/>
			</el-form-item>
			<el-form-item label="性别" prop="gender">
				<el-select v-model="editForm.gender" placeholder="请选择" clearable filterable style="width: 100px">
					<el-option v-for="item in genderOptions" :key="item.val" :label="item.label" :value="item.val" />
				</el-select>
			</el-form-item>
		</el-form>
		<div slot="footer" class="dialog-footer" style="padding-right: 10px;">
			<el-button @click.native="editFormVisible = false">取消</el-button>
			<el-button type="primary" @click.native="editSubmit" :loading="editLoading" v-hasPerm="['sys.admin.update']">提交</el-button>
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
                realName: [
                    {required: true, message: '请输入真实姓名', trigger: 'blur'}
                ],
                gender: [
                    {required: true, message: '请选择性别', trigger: 'blur'}
                ]
            },

            genderOptions: [],

            //编辑界面数据
            editForm: {
                id: null,
                accountNo: null,
                password: null,
                realName: null,
                gender: null
            }
        }
    },
    methods: {
        show: function(index, row) {
            this.editFormVisible = true;
            this.editForm = Object.assign({}, row);
            this.resetForm("editForm");
        },

        //编辑
        editSubmit: function() {
            this.$refs.editForm.validate((valid) => {
                if (valid) {
                    var params = "accountId=" + this.editForm.id
                            + (this.editForm.realName ? "&realName=" + this.editForm.realName : "")
                            + "&gender=" + (this.editForm.gender || '');
                    this.editLoading = true;
                    this.$axios.post(this.$global.baseUrl + '/sys/admin/update', params).then((res) => {
                        if (res.data.code === '0000') {
                            this.$message({type: "success", message: res.data.msg});
                        } else {
                            this.$message.error(res.data.msg);
                        }
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

        getGenderOptions() {
            let me = this;
            this.$axios.get(this.$global.baseUrl + '/sys/admin/gender/drop-down-list', {}).then((res) => {
                if (res.data.code === '0000') {
                    me.genderOptions = res.data.data.map(item => {
                        return {val: item.id, label: item.name};
                    });
                } else {
                    me.genderOptions = [];
                    me.$message.error(res.data.msg);
                }
            }).catch((err) => {
                me.$message.error(err.message);
            });
        }
    },
    mounted() {
        this.getGenderOptions();
    }
}
</script>

<style scoped>

</style>

