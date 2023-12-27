<template>
	<!--文件上传界面-->
	<el-dialog :title="title" :visible.sync="formVisible" :close-on-click-modal="modal" :width="width" append-to-body v-drag>
		<el-form :model="frm" label-width="80px" :rules="formRules" ref="frm">
			<el-form-item :label="fileLabel" prop="done">
				<el-upload :action="actionUrl" :list-type="fileType" :http-request="uploadFile"
						:on-change="handleUploadChange" :on-remove="handleUploadRemove" :before-upload="preUpload"
						:file-list="fileList" :accept="accept">
					<el-button type="primary">点击上传</el-button>
					<div slot="tip" class="el-upload-tip2">{{fileTips}}</div>
				</el-upload>
			</el-form-item>
		</el-form>
		<div slot="footer" class="dialog-footer">
			<el-button @click.native="formVisible = false">取消</el-button>
			<el-button type="primary" @click.native="completeUpload" :loading="loading">完成</el-button>
		</div>
	</el-dialog>
</template>

<script>
export default {
    props: {
        modal: {
            type: Boolean,
            default: true
        },
        width: {
            type: String,
            default: "600px"
        },
        title: {
            type: String,
            default: "上传文件"
        },
        fileLabel: {
            type: String,
            default: "文件"
        },
        fileTips: {
            type: String,
            default: "只能上传jpg/png文件，且不超过1MB"
        },
        fileErr: {
            type: String,
            default: "请上传文件"
        },
        accept: {
            type: String,
            default: ".jpg,.png"
        },
        maxSize: {
            type: Number,
            default: 1048576//最大不能超过1MB
        },
        fileType: {
            type: String,
            default: "picture" //text/picture/picture-card
        },
        mfileName: {
            type: String,
            default: "file" //后端接收文件的参数名
        },
        actionUrl: String  //上传文件的地址
    },
    data() {
        return {
            key: null,

            formVisible: false,
            loading: false,
            formRules: {
                done: [
                    {required: true, message: this.fileErr, trigger: 'blur'}
                ]
            },
            frm: {
                done: null
            },
            extParams: {},     //调接口时需要的额外的参数
            fileList: [],      //要上传的文件列表
            resp: null         //最终返回值
        }
    },
    watch: {
        fileList: function(val, oldVal) {
            this.frm.done = val && (val instanceof Array) && val.length > 0 ? "ok" : null;
        }
    },
    methods: {
        show(key, extParams) {
            this.key = key;
            this.formVisible = true;
            this.frm = {
                done: null
            };
            if (extParams) {
              this.extParams = extParams;
            }
            this.fileList = [];
            this.resp = null;
            this.resetForm("frm");
        },
        close() {
            this.$refs['frm'].resetFields();
            this.formVisible = false;
            this.loading = false;
        },

        handleUploadChange(file, fList) {
            this.fileList = [file];
        },
        handleUploadRemove(file, fList) {
            this.fileList = [];
        },
        preUpload(file) {
            if (file.size > this.maxSize) {
                this.$message.error('单个文件不能超过' + this.div(this.maxSize, 1048576) + 'MB');
                return false;
            }
            return true;
        },
        uploadFile(req) {
            var formData = new FormData();
            //文件参数
            formData.append(this.mfileName, req.file);
            //其它参数
            if (this.extParams) {
                for (let key in this.extParams) {
                    formData.append(key, this.extParams[key]);
                }
            }
            var that = this;
            that.resp = null;
            that.$axios.post(req.action, formData).then((res) => {
                var resData;
                that.$emit("change", this.key, res, data => {
                    resData = data;
                });

                if (resData != 'undefined' && resData != null) {
                    that.resp = resData;
                }
                that.$refs.frm.validate((valid) => {});
            }).catch((err) => {
                that.$message.error(err.message);
            });
        },

        //完成并返回
        completeUpload() {
            this.$refs.frm.validate((valid) => {
                if (valid) {
                    this.loading = true;
                    this.$emit("complete", this.key, this.resp);
                    this.$refs['frm'].resetFields();
                    this.formVisible = false;
                    this.loading = false;
                }
            });
        },

        //数字除法
        div: function(arg1, arg2) {
            if (!arg1 && arg1 !== 0) {
                arg1 = 0;
            }
            if (!arg2 && arg2 !== 0) {
                arg2 = 0;
            }
            var t1 = 0, t2 = 0, r1, r2;
            try {
                t1 = arg1.toString().split(".")[1].length;
            } catch(e) {}
            try {
                t2 = arg2.toString().split(".")[1].length;
            } catch(e) {}
            r1 = Number(arg1.toString().replace(".", ""));
            r2 = Number(arg2.toString().replace(".", ""));
            return (r1 / r2) * Math.pow(10, t2 - t1);
        }
    },
    mounted() {

    }
}
</script>

<style scoped>
.el-upload-tip2 {
    line-height: 10px;
}
.el-upload-tip2 {
    font-size: 12px;
    color: #606266;
    margin-top: 7px;
}
</style>


/**
 * 父组件可以自定义change和complete这两个事件。
 * 1、change用于解析文件上传的后台响应数据，返回任意值；
 * 2、complete用于处理组件结束之后的善后操作，参数为change返回值。
 *
 * eg.      <file-upload ref="fileUpload" :title="'上传文件'" :fileLabel="'文件'" :fileTips="'请选择文件，文件不要超过2MB'"
 *                      :accept="''" :maxSize="2097152" :fileErr="'请选择文件'" :fileType="'text'"
 *                      :actionUrl="this.$global.baseUrl + '/file/upload'" v-on:change="onUploadChanged" v-on:complete="onUploadCompleted" />
 **/

