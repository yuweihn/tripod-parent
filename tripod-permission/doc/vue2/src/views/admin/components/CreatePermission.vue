<template>
	<el-dialog title="新增权限" :visible.sync="formVisible" width="600px" :close-on-click-modal="true" append-to-body v-drag>
		<el-form :model="addForm" label-width="85px" :rules="formRules" ref="addForm" style="padding-right: 10px;">
            <el-row>
                <el-col :span="24">
                    <el-form-item label="上级权限">
                        <tree-select v-model="addForm.parentId" :options="permOptions" :normalizer="normalizer"
                                :show-count="true" placeholder="选择上级权限"/>
                    </el-form-item>
                </el-col>
                <el-col :span="24">
                    <el-form-item label="权限类型" prop="permType">
                        <el-radio-group v-model="addForm.permType">
                            <el-radio label="D">目录</el-radio>
                            <el-radio label="M">菜单</el-radio>
                            <el-radio label="B">按钮</el-radio>
                        </el-radio-group>
                    </el-form-item>
                </el-col>
                <el-col :span="24">
                    <el-form-item v-if="addForm.permType != 'B'" label="权限图标">
                        <el-popover placement="bottom-start" width="410" trigger="click" @show="$refs['iconSelect'].reset()">
                            <icon-select ref="iconSelect" @selected="onIconSelected" />
                            <el-input slot="reference" v-model="addForm.icon" placeholder="点击选择图标" readonly>
                                <svg-icon v-if="addForm.icon" slot="prefix" :icon-class="addForm.icon" class="el-input__icon"
                                            style="height: 32px; width: 16px;" />
                                <i v-else slot="prefix" class="el-icon-search el-input__icon" />
                                <i slot="suffix" class="el-icon-close el-input__icon" @click="onIconClearClick" style="cursor: pointer;" />
                            </el-input>
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
                <el-col :span="24">
                    <el-form-item v-if="addForm.permType != 'B'" label="是否外链">
                        <el-radio-group v-model="addForm.ifExt">
                            <el-radio v-for="item in ifExtList" :key="item.val" :label="item.val">{{item.label}}</el-radio>
                        </el-radio-group>
                    </el-form-item>
                </el-col>
                <el-col :span="24">
                    <el-form-item v-if="addForm.permType != 'B'" label="路由地址" prop="path">
                        <el-input v-model="addForm.path" placeholder="请输入路由地址" clearable style="width: 100%"  />
                    </el-form-item>
                </el-col>
                <el-col :span="24" v-if="addForm.permType == 'M'">
                    <el-form-item label="组件路径" prop="component">
                        <el-input v-model="addForm.component" placeholder="请输入组件路径" clearable />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item v-if="addForm.permType != 'B'" label="权限状态">
                        <el-radio-group v-model="addForm.visible">
                            <el-radio v-for="item in visibleList" :key="item.val" :label="item.val">{{item.label}}</el-radio>
                        </el-radio-group>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="显示排序" prop="orderNum">
                        <el-input-number v-model="addForm.orderNum" controls-position="right" :min="1" style="width: 100%"  />
                    </el-form-item>
                </el-col>
            </el-row>
		</el-form>
		<div slot="footer" class="dialog-footer" style="padding-right: 10px;">
			<el-button @click.native="formVisible = false">取消</el-button>
			<el-button type="primary" @click.native="addSubmit" :loading="loading" v-hasPerm="['sys.permission.create']">提交</el-button>
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
			formVisible: false,//新增界面是否显示
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

			//新增界面数据
			addForm: {
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
		show: function() {
			this.formVisible = true;
			this.addForm = {
				permNo: null,
				title: null,
				parentId: null,
				orderNum: null,
				path: null,
				component: null,
				ifExt: "false",
				permType: "D",
				visible: "true",
				icon: null,
				descr: null
			};
			this.resetForm("addForm");
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
            this.addForm.icon = name;
        },
        onIconClearClick() {
            this.addForm.icon = null;
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

		//新增
		addSubmit: function() {
			this.$refs.addForm.validate((valid) => {
				if (valid) {
					var params = "_fk=u"
                            + (this.addForm.permNo ? "&permNo=" + this.addForm.permNo : "")
                            + (this.addForm.title ? "&title=" + this.addForm.title : "")
                            + (this.addForm.parentId && this.addForm.parentId != 0 ? "&parentId=" + this.addForm.parentId : "")
                            + (this.addForm.orderNum ? "&orderNum=" + this.addForm.orderNum : "")
                            + (this.addForm.path ? "&path=" + this.addForm.path : "")
                            + (this.addForm.component ? "&component=" + this.addForm.component : "")
                            + (this.addForm.ifExt != null ? "&ifExt=" + this.addForm.ifExt : "")
                            + (this.addForm.permType ? "&permType=" + this.addForm.permType : "")
                            + (this.addForm.visible != null ? "&visible=" + this.addForm.visible : "")
                            + (this.addForm.icon ? "&icon=" + this.addForm.icon : "")
							+ (this.addForm.descr ? "&descr=" + this.addForm.descr : "");
					this.loading = true;
					this.$axios.post(this.$global.baseUrl + '/sys/permission/create', params).then((res) => {
						if (res.data.code === '0000') {
							this.$message({type: "success", message: res.data.msg});
						} else {
							this.$message.error(res.data.msg);
						}
					    this.removeDynamicLoaded();
						this.$refs['addForm'].resetFields();
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

