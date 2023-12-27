<template>
	<el-dialog title="编辑" v-model="formVisible" width="500px" :close-on-click-modal="true" append-to-body draggable>
		<el-form :model="editForm" label-width="70px" :rules="formRules" ref="editFormRef">
			<el-form-item label="角色" prop="roleId">
				<el-select v-model="editForm.roleId" placeholder="请输入" @change="onRoleChanged($event)"
							:remote-method="initRoleList" clearable filterable remote style="width: 100%">
					<el-option v-for="item in roleSlt.result.list" :key="item.val" :label="item.label" :value="item.val">
						<span class="slt-option-left">{{item.label}}</span>
						<span class="slt-option-right">{{item.roleNo}}</span>
					</el-option>
					<el-pagination layout="total, prev, pager, next" hide-on-single-page @current-change="onRolePageChanged"
						        :current-page="roleSlt.pageNo" :page-size="roleSlt.pageSize" :total="roleSlt.result.size"/>
				</el-select>
			</el-form-item>
		</el-form>
		<div slot="footer" class="dialog-footer">
			<el-button @click.native="formVisible = false">取消</el-button>
			<el-button type="primary" @click.native="editSubmit" :loading="loading" v-hasPerm="['sys.admin.role.update']">提交</el-button>
		</div>
	</el-dialog>
</template>

<script setup>
const {proxy} = getCurrentInstance();
const adminId = ref(null);
const formVisible = ref(false);
const loading = ref(false);
const formRules = {
    roleId: [
        {required: true, message: '请选择角色', trigger: 'blur'}
    ]
};
const editForm = ref({
    id: null,
    roleId: null
});
const roleSlt = ref({
    result: {
        size: 0,
        list: []
    },
    keywords: "",
    pageNo: 1,
    pageSize: 10
});
const emit = defineEmits(["success"]);

function show(aid, index, row) {
    proxy.resetForm("editFormRef");
    adminId.value = aid;
    //editForm.value = Object.assign({}, row);
    editForm.value.id = row.id;
    editForm.value.roleId = row.roleId;

    roleSlt.value.result.size = 1;
    roleSlt.value.result.list = [
        {
            val: row.roleId,
            label: row.roleName,
            roleNo: row.roleNo
        }
    ];
    formVisible.value = true;
}
function editSubmit() {
    proxy.$refs.editFormRef.validate((valid) => {
        if (valid) {
            var params = "id=" + editForm.value.id + "&adminId=" + adminId.value + "&roleId=" + editForm.value.roleId;
            loading.value = true;
            proxy.request.post('/sys/admin/role/update', params).then((res) => {
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
function initRoleList(keywords) {
    roleSlt.value.keywords = keywords;
    onRolePageChanged(1, 10);
}
function onRolePageChanged(pno, psize) {
    if (pno != null) {
        roleSlt.value.pageNo = pno;
    }
    if (psize != null) {
        roleSlt.value.pageSize = psize;
    }
    var params = {keywords: roleSlt.value.keywords, pageNo: roleSlt.value.pageNo, pageSize: roleSlt.value.pageSize};
    proxy.request.get('/sys/role/list', params).then((res) => {
        roleSlt.value.result.size = res.data.data.size;
        roleSlt.value.result.list = res.data.data.list.map(item => {
            return {val: item.id, label: item.roleName, roleNo: item.roleNo};
        });
    }).catch((err) => {
        roleSlt.value.result.size = 0;
        roleSlt.value.result.list = [];
    });
}
function onRoleChanged(roleId) {
    if (roleId) {
        return;
    }
    initRoleList();
}
defineExpose({
    show
})
</script>

<style scoped>

</style>

