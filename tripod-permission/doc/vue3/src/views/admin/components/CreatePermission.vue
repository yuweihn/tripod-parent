<template>
	<el-dialog title="新增权限" v-model="formVisible" width="600px" :close-on-click-modal="true" append-to-body draggable>
		<el-form :model="addForm" label-width="85px" :rules="formRules" ref="addFormRef">
            <el-row>
                <el-col :span="24">
                    <el-form-item label="上级权限">
                        <el-tree-select v-model="addForm.parentId" :data="permOptions" style="width: 100%"
                                :props="{value: 'id', label: 'title', children: 'children'}" clearable
                                placeholder="选择上级权限" :render-after-expand="false" :check-strictly="true" />
                    </el-form-item>
                </el-col>
                <el-col :span="24">
                    <el-radio-group v-model="addForm.permType">
                        <el-form-item label="权限类型" prop="permType">
                            <el-radio label="D">目录</el-radio>
                            <el-radio label="M">菜单</el-radio>
                            <el-radio label="B">按钮</el-radio>
                        </el-form-item>
                    </el-radio-group>
                </el-col>
                <el-col :span="24" v-if="addForm.permType != 'B'">
                    <el-form-item label="权限图标">
                        <el-popover placement="bottom-start" width="430px" trigger="click" @show="$refs['iconSelect'].reset()">
                            <template #reference>
                                <el-input v-model="addForm.icon" placeholder="点击选择图标" readonly>
                                    <template #prefix>
                                        <svg-icon v-if="addForm.icon" :icon-class="addForm.icon" />
                                        <el-icon v-else><search /></el-icon>
                                    </template>
                                    <template #suffix>
                                        <el-icon @click="onIconClearClick" style="cursor: pointer;" ><close /></el-icon>
                                    </template>
                                </el-input>
                            </template>
                            <icon-select ref="iconSelect" @selected="onIconSelected" />
                        </el-popover>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="权限编码" prop="permNo">
                        <el-input v-model="addForm.permNo" placeholder="请输入权限编码" clearable />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="权限标题" prop="title">
                        <el-input v-model="addForm.title" placeholder="请输入权限标题" clearable />
                    </el-form-item>
                </el-col>
                <el-col :span="24" v-if="addForm.permType != 'B'">
                    <el-radio-group v-model="addForm.ifExt">
                        <el-form-item label="是否外链">
                            <el-radio v-for="item in ifExtList" :key="item.val" :label="item.val">{{item.label}}</el-radio>
                        </el-form-item>
                    </el-radio-group>
                </el-col>
                <el-col :span="24" v-if="addForm.permType != 'B'">
                    <el-form-item label="路由地址" prop="path">
                        <el-input v-model="addForm.path" placeholder="请输入路由地址" clearable style="width: 100%" />
                    </el-form-item>
                </el-col>
                <el-col :span="24" v-if="addForm.permType == 'M'">
                    <el-form-item label="组件路径" prop="component">
                        <el-input v-model="addForm.component" placeholder="请输入组件路径" clearable />
                    </el-form-item>
                </el-col>
                <el-col :span="12" v-if="addForm.permType != 'B'">
                    <el-radio-group v-model="addForm.visible">
                        <el-form-item label="权限状态" prop="visible">
                            <el-radio v-for="item in visibleList" :key="item.val" :label="item.val">{{item.label}}</el-radio>
                        </el-form-item>
                    </el-radio-group>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="显示排序" prop="orderNum">
                        <el-input-number v-model="addForm.orderNum" controls-position="right" :min="1" style="width: 100%"  />
                    </el-form-item>
                </el-col>
            </el-row>
		</el-form>
		<div slot="footer" class="dialog-footer">
			<el-button @click.native="formVisible = false">取消</el-button>
			<el-button type="primary" @click.native="addSubmit" :loading="loading" v-hasPerm="['sys.permission.create']">提交</el-button>
		</div>
	</el-dialog>
</template>

<script setup>
import IconSelect from "@/components/IconSelect";

const {proxy} = getCurrentInstance();
const formVisible = ref(false);
const loading = ref(false);
const formRules = {
    permType: [
        {required: true, message: '请选择权限类型', trigger: 'blur'}
    ],
    permNo: [
        {required: true, message: '请输入权限编码', trigger: 'blur'}
    ],
    orderNum: [
        {required: true, message: "顺序不能为空", trigger: "blur"}
    ]
};
const permOptions = ref([]);    //权限树选项
const visibleList = ref([
    {
        "val": "true",
        "label": "显示"
    },
    {
        "val": "false",
        "label": "隐藏"
    }
]);
const ifExtList = ref([
    {
        "val": "true",
        "label": "是"
    },
    {
        "val": "false",
        "label": "否"
    }
]);
const addForm = ref({
    permNo: null,
    title: null,
    parentId: null,
    orderNum: null,
    path: null,
    component: null,
    ifExt: null,
    permType: null,
    visible: null,
    icon: null,
    descr: null
});
const emit = defineEmits(["success"]);


function show() {
    proxy.resetForm("addFormRef");
    addForm.value.permNo = null;
    addForm.value.title = null;
    addForm.value.parentId = null;
    addForm.value.orderNum = 1;
    addForm.value.path = null;
    addForm.value.component = null;
    addForm.value.ifExt = "false";
    addForm.value.permType = "D";
    addForm.value.visible = "true";
    addForm.value.icon = null;
    addForm.value.descr = null;
    getPermTreeSelect();
    formVisible.value = true;
}
/** 查询权限下拉树结构 */
function getPermTreeSelect() {
    proxy.request.get('/sys/permission/list').then((res) => {
        permOptions.value = [];
        const rootPerm = {id: 0, title: '根权限', children: []};
        rootPerm.children = res.data.data;
        permOptions.value.push(rootPerm);
    }).catch((err) => {
        permOptions.value = [];
    });
}
function onIconSelected(name) {
    addForm.value.icon = name;
}
function onIconClearClick() {
    addForm.value.icon = null;
}
function addSubmit() {
    proxy.$refs.addFormRef.validate((valid) => {
        if (valid) {
            var params = "_fk=u"
                    + (addForm.value.permNo ? "&permNo=" + addForm.value.permNo : "")
                    + (addForm.value.title ? "&title=" + addForm.value.title : "")
                    + (addForm.value.parentId && addForm.value.parentId != 0 ? "&parentId=" + addForm.value.parentId : "")
                    + (addForm.value.orderNum ? "&orderNum=" + addForm.value.orderNum : "")
                    + (addForm.value.path ? "&path=" + addForm.value.path : "")
                    + (addForm.value.component ? "&component=" + addForm.value.component : "")
                    + (addForm.value.ifExt != null ? "&ifExt=" + addForm.value.ifExt : "")
                    + (addForm.value.permType ? "&permType=" + addForm.value.permType : "")
                    + (addForm.value.visible != null ? "&visible=" + addForm.value.visible : "")
                    + (addForm.value.icon ? "&icon=" + addForm.value.icon : "")
                    + (addForm.value.descr ? "&descr=" + addForm.value.descr : "");
            loading.value = true;
            proxy.request.post('/sys/permission/create', params).then((res) => {
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

