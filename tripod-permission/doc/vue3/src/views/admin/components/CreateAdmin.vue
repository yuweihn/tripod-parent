<template>
	<el-dialog title="新增管理员" v-model="formVisible" width="450px" :close-on-click-modal="true" append-to-body draggable>
		<el-form :model="addForm" label-width="90px" :rules="formRules" ref="addFormRef">
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
		<div slot="footer" class="dialog-footer">
			<el-button @click.native="formVisible = false">取消</el-button>
			<el-button type="primary" @click.native="addSubmit" :loading="loading" v-hasPerm="['sys.admin.create']">提交</el-button>
		</div>
	</el-dialog>
</template>

<script setup>
const {proxy} = getCurrentInstance();
const formVisible = ref(false);
const loading = ref(false);
const formRules = {
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
};
const genderOptions = ref([]);
const addForm = ref({
    accountNo: null,
    password: null,
    realName: null,
    gender: null
});
const emit = defineEmits(["success"]);

function show() {
    proxy.resetForm("addFormRef");
    addForm.value.accountNo = null;
    addForm.value.password = null;
    addForm.value.realName = null;
    addForm.value.gender = null;
    formVisible.value = true;
}
function addSubmit() {
    proxy.$refs.addFormRef.validate((valid) => {
        if (valid) {
            var params = "accountNo=" + addForm.value.accountNo + "&password=" + proxy.$md5(addForm.value.password)
                    + (addForm.value.realName ? "&realName=" + addForm.value.realName : "")
                    + "&gender=" + (addForm.value.gender || '');
            loading.value = true;
            proxy.request.post('/sys/admin/create', params).then((res) => {
                proxy.$modal.msgSuccess(res.data.msg);
                proxy.$refs['addFormRef'].resetFields();
                formVisible.value = false;
                loading.value = false;
                emit("success", 1);
            }).catch((err) => {
                loading.value = false;
            });
        }
    });
}
function getGenderOptions() {
    proxy.request.get('/sys/admin/gender/drop-down-list').then((res) => {
        genderOptions.value = res.data.data.map(item => {
            return {val: item.id, label: item.name};
        });
    }).catch((err) => {
        genderOptions.value = [];
    });
}
getGenderOptions();
defineExpose({
    show
})
</script>

<style scoped>

</style>

