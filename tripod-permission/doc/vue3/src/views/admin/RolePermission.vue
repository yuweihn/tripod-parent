<template>
    <el-dialog :title="title" v-model="formVisible" width="550px" :close-on-click-modal="true" append-to-body draggable>
        <div class="tree-container" v-loading="loading">
            <el-tree ref="permMenuRef" class="tree-border" :data="permList" show-checkbox node-key="id" :check-strictly="false"
                        empty-text="加载中，请稍后" :props="defaultProps" style="width: 100%;"/>
        </div>
        <div class="dialog-footer">
            <el-button @click.native="formVisible = false">取消</el-button>
            <el-button type="primary" @click.native="submit" :loading="loading" v-hasPerm="['sys.role.permission.save']">提交</el-button>
        </div>
    </el-dialog>
</template>

<script setup>
const {proxy} = getCurrentInstance();
const formVisible = ref(false);
const loading = ref(false);
const roleId = ref(null);
const title = ref(null);
const permList = ref([]);
const defaultProps = ref({
    label: "title",
    children: "children"
});


function show(rId, roleName) {
    roleId.value = rId;
    title.value = roleName == null ? '角色权限' : '角色权限 - ' + roleName;
    loading.value = false;
    formVisible.value = true;
    getPermissionListByRoleId();
}
function getPermissionListByRoleId() {
    var params = {
        roleId: roleId.value
    };
    loading.value = true;
    proxy.request.get('/sys/role/permission/list', params).then((res) => {
        permList.value = res.data.data.permList;
        setChecked(res.data.data.checkedPermIdList);
        loading.value = false;
    }).catch((err) => {
        permList.value = [];
        setChecked([]);
        loading.value = false;
    });
}
function setChecked(cList) {
    proxy.$nextTick(() => {
        cList.forEach((v) => {
            proxy.$nextTick(() => {
                proxy.$refs.permMenuRef.setChecked(v, true, false);
            });
        });
    });
}
function submit() {
    const hkeys = proxy.$refs.permMenuRef.getHalfCheckedKeys();
    const keys = proxy.$refs.permMenuRef.getCheckedKeys();
    hkeys.push(keys);

    var params = "roleId=" + roleId.value + "&permIds=" + hkeys;
    loading.value = true;
    proxy.request.post('/sys/role/permission/save', params).then((res) => {
        proxy.$modal.msgSuccess(res.data.msg);
        proxy.removeDynamicLoaded();
        formVisible.value = false;
        loading.value = false;
    }).catch((err) => {
        loading.value = false;
    });
}
defineExpose({
    show
})
</script>

<style scoped>
.tree-container {
    display: flex;
    padding-left: 10px;
    padding-right: 10px;
    padding-bottom: 25px;
}
.tree-border {
    padding: 5px 0;
    border: 1px solid #e5e6e7;
    background: #fff none;
    border-radius: 4px;
}
</style>

