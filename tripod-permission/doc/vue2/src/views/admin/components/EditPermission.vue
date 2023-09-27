<template>
	<el-dialog title="编辑权限" :visible.sync="formVisible" width="600px" :close-on-click-modal="true" append-to-body v-drag>
		<el-form :model="editForm" label-width="85px" :rules="formRules" ref="editForm" style="padding-right: 10px;">
            <el-row>
                <el-col :span="24">
                    <el-form-item label="上级权限">
                        <tree-select v-model="editForm.parentId" :options="permOptions" :normalizer="normalizer"
                                    :show-count="true" placeholder="选择上级权限"/>
                    </el-form-item>
                </el-col>
                <el-col :span="24">
                    <el-form-item label="权限类型" prop="permType">
                        <el-radio-group v-model="editForm.permType">
                            <el-radio label="D">目录</el-radio>
                            <el-radio label="M">菜单</el-radio>
                            <el-radio label="B">按钮</el-radio>
                        </el-radio-group>
                    </el-form-item>
                </el-col>
                <el-col :span="24">
                    <el-form-item v-if="editForm.permType != 'B'" label="权限图标">
                        <el-popover placement="bottom-start" width="410" trigger="click" @show="$refs['iconSelect'].reset()">
                            <icon-select ref="iconSelect" @selected="onIconSelected" />
                            <el-input slot="reference" v-model="editForm.icon" placeholder="点击选择图标" readonly>
                                <svg-icon v-if="editForm.icon" slot="prefix" :icon-class="editForm.icon" class="el-input__icon"
                                            style="height: 32px; width: 16px;" />
                                <i v-else slot="prefix" class="el-icon-search el-input__icon" />
                                <i slot="suffix" class="el-icon-close el-input__icon" @click="onIconClearClick" style="cursor: pointer;"/>
                            </el-input>
                        </el-popover>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="权限编码" prop="permNo">
                        <el-input v-model="editForm.permNo" placeholder="请输入权限编码" clearable />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="权限标题" prop="title">
                        <el-input v-model="editForm.title" placeholder="请输入权限标题" clearable />
                    </el-form-item>
                </el-col>
                <el-col :span="24">
                    <el-form-item v-if="editForm.permType != 'B'" label="是否外链">
                        <el-radio-group v-model="editForm.ifExt">
                            <el-radio v-for="item in ifExtList" :key="item.val" :label="item.val">{{item.label}}</el-radio>
                        </el-radio-group>
                    </el-form-item>
                </el-col>
                <el-col :span="24">
                    <el-form-item v-if="editForm.permType != 'B'" label="路由地址" prop="path">
                        <el-input v-model="editForm.path" placeholder="请输入路由地址" clearable style="width: 100%"  />
                    </el-form-item>
                </el-col>
                <el-col :span="24" v-if="editForm.permType == 'M'">
                    <el-form-item label="组件路径" prop="component">
                        <el-input v-model="editForm.component" placeholder="请输入组件路径" clearable />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item v-if="editForm.permType != 'B'" label="权限状态">
                        <el-radio-group v-model="editForm.visible">
                            <el-radio v-for="item in visibleList" :key="item.val" :label="item.val">{{item.label}}</el-radio>
                        </el-radio-group>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="显示排序" prop="orderNum">
                        <el-input-number v-model="editForm.orderNum" controls-position="right" :min="1" style="width: 100%"  />
                    </el-form-item>
                </el-col>
            </el-row>
		</el-form>
		<div slot="footer" class="dialog-footer" style="padding-right: 10px;">
			<el-button @click.native="formVisible = false">取消</el-button>
			<el-button type="primary" @click.native="editSubmit" :loading="loading" v-hasPerm="['sys.permission.update']">提交</el-button>
		</div>
	</el-dialog>
</template>

<script>
import TreeSelect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import IconSelect from "@/components/svg/select";

export default {
    components: {
        'tree-select': TreeSelect,
        'icon-select': IconSelect
    },

    data() {
        return {
            formVisible: false,//编辑界面是否显示
            loading: false,
            formRules: {
				permType: [
					{required: true, message: '请选择权限类型', trigger: 'blur'}
				],
				permNo: [
					{required: true, message: '请输入权限编码', trigger: 'blur'}
				],
				orderNum: [
                    {required: true, message: "顺序不能为空", trigger: "blur"}
                ]
            },

            // 权限树选项
            permOptions: [],

            visibleList: [
                {
                    "val": "true",
                    "label": "显示"
                },
                {
                    "val": "false",
                    "label": "隐藏"
                }
            ],
            ifExtList: [
                {
                    "val": "true",
                    "label": "是"
                },
                {
                    "val": "false",
                    "label": "否"
                }
            ],

            //编辑界面数据
            editForm: {
                id: null,
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
            }
        }
    },
    methods: {
        show: function(index, row) {
            this.formVisible = true;
            //this.editForm = Object.assign({}, row);
            this.editForm = {
                id: row.id,
                permNo: row.permNo,
                title: row.title,
                parentId: row.parentId,
                orderNum: row.orderNum,
                path: row.path,
                component: row.component,
                ifExt: row.ifExt == null ? null : row.ifExt.toString(),
                permType: row.permType,
                visible: row.visible == null ? null : row.visible.toString(),
                icon: row.icon,
                descr: row.descr
            };
            this.resetForm("editForm");
            this.getPermTreeSelect();
        },

        /** 查询权限下拉树结构 */
        getPermTreeSelect() {
            this.$axios.get(this.$global.baseUrl + '/sys/permission/list', {}).then((res) => {
                if (res.data.code === '0000') {
                    this.permOptions = [];
                    const rootPerm = {id: 0, title: '根权限', children: []};
                    rootPerm.children = res.data.data;
                    this.permOptions.push(rootPerm);
                } else {
                    this.permOptions = [];
                    this.$message.error(res.data.msg);
                }
            }).catch((err) => {
                this.$message.error(err.message);
            });
        },

        // 选择图标
        onIconSelected(name) {
            this.editForm.icon = name;
        },
        onIconClearClick() {
            this.editForm.icon = null;
        },

        /** 转换权限数据结构 */
        normalizer(node) {
            if (node.children && !node.children.length) {
                delete node.children;
            }
            return {
                id: node.id,
                label: node.title,
                children: node.children
            };
        },

        //编辑
        editSubmit: function() {
            this.$refs.editForm.validate((valid) => {
                if (valid) {
                    var params = "_fk=u" + "&id=" + this.editForm.id
                                    + (this.editForm.permNo ? "&permNo=" + this.editForm.permNo : "")
                                    + (this.editForm.title ? "&title=" + this.editForm.title : "")
                                    + (this.editForm.parentId && this.editForm.parentId != 0 ? "&parentId=" + this.editForm.parentId : "")
                                    + (this.editForm.orderNum ? "&orderNum=" + this.editForm.orderNum : "")
                                    + (this.editForm.path ? "&path=" + this.editForm.path : "")
                                    + (this.editForm.component ? "&component=" + this.editForm.component : "")
                                    + (this.editForm.ifExt != null ? "&ifExt=" + this.editForm.ifExt : "")
                                    + (this.editForm.permType ? "&permType=" + this.editForm.permType : "")
                                    + (this.editForm.visible != null ? "&visible=" + this.editForm.visible : "")
                                    + (this.editForm.icon ? "&icon=" + this.editForm.icon : "")
                                    + (this.editForm.descr ? "&descr=" + this.editForm.descr : "");
                    this.loading = true;
                    this.$axios.post(this.$global.baseUrl + '/sys/permission/update', params).then((res) => {
                        if (res.data.code === '0000') {
                            this.$message({type: "success", message: res.data.msg});
                        } else {
                            this.$message.error(res.data.msg);
                        }
                        this.removeDynamicLoaded();
                        this.$refs['editForm'].resetFields();
                        this.formVisible = false;
                        this.loading = false;
                        this.$emit("success", 1);
                    }).catch((err) => {
                        this.loading = false;
                        this.$message.error(err.message);
                    });
                }
            });
        }
    },
    mounted() {

    }
}
</script>

<style scoped>

</style>

