<template>
	<el-dialog title="新增角色" v-model="formVisible" width="450px" :close-on-click-modal="true" append-to-body draggable>
		<el-form :model="addForm" label-width="90px" :rules="formRules" ref="addFormRef">
			<el-form-item label="角色编码" prop="roleNo">
				<el-input v-model="addForm.roleNo" clearable autocomplete="off" style="width: 100%" />
			</el-form-item>
			<el-form-item label="角色名称" prop="roleName">
				<el-input v-model="addForm.roleName" clearable autocomplete="off" style="width: 100%" />
			</el-form-item>
		</el-form>
		<div slot="footer" class="dialog-footer">
			<el-button @click.native="formVisible = false">取消</el-button>
			<el-button type="primary" @click.native="addSubmit" :loading="loading" v-hasPerm="['sys.role.create']">提交</el-button>
		</div>
	</el-dialog>
</template>

<script setup>
const {proxy} = getCurrentInstance();
const formVisible = ref(false);
const loading = ref(false);
const formRules = {
    roleNo: [
        {required: true, message: '请输入角色编码', trigger: 'blur'}
    ],
    roleName: [
        {required: true, message: '请输入角色名称', trigger: 'blur'}
    ]
};
const addForm = ref({
    roleNo: null,
    roleName: null
});
const emit = defineEmits(["success"]);


function show() {
    proxy.resetForm("addFormRef");
    addForm.value.roleNo = null;
    addForm.value.roleName = null;
    formVisible.value = true;
}
function addSubmit() {
    proxy.$refs.addFormRef.validate((valid) => {
        if (valid) {
            var params = "_fk=u"
                    + (addForm.value.roleNo ? "&roleNo=" + addForm.value.roleNo : "")
                    + (addForm.value.roleName ? "&roleName=" + addForm.value.roleName : "");
            loading.value = true;
            proxy.request.post('/sys/role/create', params).then((res) => {
                proxy.$modal.msgSuccess(res.data.msg);
                proxy.removeDynamicLoaded();
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
defineExpose({
    show
})
</script>

<style scoped>

</style>

