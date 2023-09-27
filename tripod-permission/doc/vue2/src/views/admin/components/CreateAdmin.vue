<template>
	<el-dialog title="新增管理员" :visible.sync="addFormVisible" width="450px" :close-on-click-modal="true" append-to-body v-drag>
		<el-form :model="addForm" label-width="90px" :rules="addFormRules" ref="addForm" style="padding-right: 10px;">
			<el-form-item label="用户名" prop="accountNo">
				<el-input v-model="addForm.accountNo" clearable autocomplete="off" style="width: 100%"/>
			</el-form-item>
			<el-form-item label="密码" prop="password">
				<el-input v-model="addForm.password" clearable autocomplete="new-password" show-password style="width: 100%"/>
			</el-form-item>
			<el-form-item label="真实姓名" prop="realName">
				<el-input v-model="addForm.realName" clearable autocomplete="off" style="width: 100%" />
			</el-form-item>
			<el-form-item label="性别" prop="gender">
				<el-select v-model="addForm.gender" placeholder="请选择" clearable filterable style="width: 100px">
					<el-option v-for="item in genderOptions" :key="item.val" :label="item.label" :value="item.val" />
				</el-select>
			</el-form-item>
		</el-form>
		<div slot="footer" class="dialog-footer" style="padding-right: 10px;">
			<el-button @click.native="addFormVisible = false">取消</el-button>
			<el-button type="primary" @click.native="addSubmit" :loading="addLoading" v-hasPerm="['sys.admin.create']">提交</el-button>
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
                accountNo: [
                    {required: true, message: '请输入用户名', trigger: 'blur'}
                ],
                password: [
                    {required: true, message: '请输入密码', trigger: 'blur'}
                ],
                realName: [
                    {required: true, message: '请输入真实姓名', trigger: 'blur'}
                ],
                gender: [
                    {required: true, message: '请选择性别', trigger: 'blur'}
                ]
            },

            genderOptions: [],

            //新增界面数据
            addForm: {
                accountNo: null,
                password: null,
                realName: null,
                gender: null
            }
        }
    },
    methods: {
        show: function() {
            this.addFormVisible = true;
            this.addForm = {
                accountNo: null,
                password: null,
                realName: null,
                gender: null
            };
            this.resetForm("addForm");
        },
        //新增
        addSubmit: function() {
            this.$refs.addForm.validate((valid) => {
                if (valid) {
                    var params = "accountNo=" + this.addForm.accountNo + "&password=" + this.$md5(this.addForm.password)
                            + (this.addForm.realName ? "&realName=" + this.addForm.realName : "")
                            + "&gender=" + (this.addForm.gender || '');
                    this.addLoading = true;
                    this.$axios.post(this.$global.baseUrl + '/sys/admin/create', params).then((res) => {
                        if (res.data.code === '0000') {
                            this.$message({type: "success", message: res.data.msg});
                        } else {
                            this.$message.error(res.data.msg);
                        }
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
                this.$message.error(err.message);
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

