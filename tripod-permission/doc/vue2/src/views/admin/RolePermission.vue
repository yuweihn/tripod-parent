<template>
    <el-dialog :title="title" :visible.sync="formVisible" width="550px" :close-on-click-modal="true" append-to-body v-drag>
        <div class="tree-container" v-loading="loading">
            <el-tree ref="permMenu" class="tree-border" :data="permList" show-checkbox node-key="id" :check-strictly="false"
                        empty-text="加载中，请稍后" :props="defaultProps" style="width: 100%;"/>
        </div>
        <div class="dialog-footer">
            <el-button @click.native="formVisible = false">取消</el-button>
            <el-button type="primary" @click.native="submit" :loading="loading" v-hasPerm="['sys.role.permission.save']">提交</el-button>
        </div>
    </el-dialog>
</template>

<script>
export default {
    data() {
        return {
            formVisible: false,
            roleId: null,
            title: null,

            defaultProps: {
                label: "title",
                children: "children"
            },

            loading: false,
            permList: []
        }
    },
    methods: {
        show: function(roleId, roleName) {
            this.roleId = roleId;
            this.title = roleName == null ? '角色权限' : '角色权限 - ' + roleName;
            this.loading = false;
            this.formVisible = true;
            this.getPermissionListByRoleId();
        },
        getPermissionListByRoleId() {
            var params = {
                roleId: this.roleId
            };
            this.$axios.get(this.$global.baseUrl + '/sys/role/permission/list', {params: params}).then((res) => {
                var checkedPermIdList = [];
                if (res.data.code === '0000') {
                    this.permList = res.data.data.permList;
                    checkedPermIdList = res.data.data.checkedPermIdList;
                } else {
                    this.permList = [];
                    checkedPermIdList = [];
                    this.$message.error(res.data.msg);
                }
                this.$nextTick(() => {
                    checkedPermIdList.forEach((v) => {
                        this.$nextTick(() => {
                            this.$refs.permMenu.setChecked(v, true ,false);
                        });
                    });
                });
            }).catch((err) => {
                this.$message.error(err.message);
            });
        },
        submit: function() {
            const hkeys = this.$refs.permMenu.getHalfCheckedKeys();
            const keys = this.$refs.permMenu.getCheckedKeys();
            hkeys.push(keys);

            var params = "roleId=" + this.roleId + "&permIds=" + hkeys;
            this.loading = true;
            this.$axios.post(this.$global.baseUrl + '/sys/role/permission/save', params).then((res) => {
                if (res.data.code === '0000') {
                    this.$message({type: "success", message: res.data.msg});
                } else {
                    this.$message.error(res.data.msg);
                }
                this.loading = false;
                this.removeDynamicLoaded();
                this.formVisible = false;
            }).catch((err) => {
                this.loading = false;
                this.$message.error(err.message);
            });
        }
    },
    mounted() {

    }
}
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

