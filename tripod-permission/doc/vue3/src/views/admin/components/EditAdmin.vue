<template>
	<el-dialog title="编辑管理员" v-model="formVisible" width="450px" :close-on-click-modal="true" append-to-body draggable>
		<el-form :model="editForm" label-width="90px" :rules="formRules" ref="editFormRef">
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
		<div slot="footer" class="dialog-footer">
			<el-button @click.native="formVisible = false">取消</el-button>
			<el-button type="primary" @click.native="editSubmit" :loading="loading" v-hasPerm="['sys.admin.update']">提交</el-button>
		</div>
	</el-dialog>
</template>

<script setup>
const {proxy} = getCurrentInstance();
const formVisible = ref(false);
const loading = ref(false);
const formRules = {
    realName: [
        {required: true, message: '请输入真实姓名', trigger: 'blur'}
    ],
    gender: [
        {required: true, message: '请选择性别', trigger: 'blur'}
    ]
};
const genderOptions = ref([]);
const editForm = ref({
    id: null,
    accountNo: null,
    password: null,
    realName: null,
    gender: null
});
const emit = defineEmits(["success"]);

function show(index, row) {
    proxy.resetForm("editFormRef");
    editForm.value = Object.assign({}, row);
    formVisible.value = true;
}
function editSubmit() {
    proxy.$refs.editFormRef.validate((valid) => {
        if (valid) {
            var params = "accountId=" + editForm.value.id
                    + (editForm.value.realName ? "&realName=" + editForm.value.realName : "")
                    + "&gender=" + (editForm.value.gender || '');
            loading.value = true;
            proxy.request.post('/sys/admin/update', params).then((res) => {
                proxy.$modal.msgSuccess(res.data.msg);
                proxy.$refs['editFormRef'].resetFields();
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

