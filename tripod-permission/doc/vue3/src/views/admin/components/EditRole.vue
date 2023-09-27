<template>
	<el-dialog title="编辑角色" v-model="formVisible" width="450px" :close-on-click-modal="true" append-to-body draggable>
		<el-form :model="editForm" label-width="90px" :rules="formRules" ref="editFormRef">
			<el-form-item label="角色编码" prop="roleNo">
				<el-input v-model="editForm.roleNo" clearable autocomplete="off" style="width: 100%" />
			</el-form-item>
			<el-form-item label="角色名称" prop="roleName">
				<el-input v-model="editForm.roleName" clearable autocomplete="off" style="width: 100%" />
			</el-form-item>
		</el-form>
		<div slot="footer" class="dialog-footer">
			<el-button @click.native="formVisible = false">取消</el-button>
			<el-button type="primary" @click.native="editSubmit" :loading="loading" v-hasPerm="['sys.role.update']">提交</el-button>
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
const editForm = ref({
    id: null,
    roleNo: null,
    roleName: null
});
const emit = defineEmits(["success"]);


function show(index, row) {
    proxy.resetForm("editFormRef");
    editForm.value.id = row.id;
    editForm.value.roleNo = row.roleNo;
    editForm.value.roleName = row.roleName;
    formVisible.value = true;
}
function editSubmit() {
    proxy.$refs.editFormRef.validate((valid) => {
        if (valid) {
            var params = "_fk=u&id=" + editForm.value.id
                    + (editForm.value.roleNo ? "&roleNo=" + editForm.value.roleNo : "")
                    + (editForm.value.roleName ? "&roleName=" + editForm.value.roleName : "");
            loading.value = true;
            proxy.request.post('/sys/role/update', params).then((res) => {
                proxy.$modal.msgSuccess(res.data.msg);
                proxy.removeDynamicLoaded();
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
defineExpose({
    show
})
</script>

<style scoped>

</style>

