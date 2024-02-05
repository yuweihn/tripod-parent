<template>
	<!--文件上传界面-->
	<el-dialog :title="title" v-model="formVisible" :close-on-click-modal="modal" :width="width" append-to-body draggable>
		<el-form :model="frm" label-width="80px" :rules="formRules" ref="frmRef">
			<el-form-item :label="fileLabel" prop="done">
				<el-upload :action="actionUrl" :list-type="fileType" :http-request="uploadFile"
                            :on-change="handleUploadChange" :on-remove="handleUploadRemove" :before-upload="preUpload"
                            :file-list="fileList" :accept="accept">
					<el-button type="primary">点击上传</el-button>
                    <template #tip>
                        <div class="el-upload-tip2">{{fileTips}}</div>
                    </template>
				</el-upload>
			</el-form-item>
		</el-form>
		<div slot="footer" class="dialog-footer">
			<el-button @click.native="formVisible = false">取消</el-button>
			<el-button type="primary" @click.native="completeUpload" :loading="loading">完成</el-button>
		</div>
	</el-dialog>
</template>

<script setup>
const props = defineProps({
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
});

const {proxy} = getCurrentInstance();

const key = ref(null);
const formVisible = ref(false);
const loading = ref(false);
const formRules = {
    done: [
        {required: true, message: proxy.fileErr, trigger: 'blur'}
    ]
};
const frm = ref({
    done: null
});
const extParams = ref({});     //调接口时需要的额外的参数
const fileList = ref([]);      //要上传的文件列表
const resp = ref(null);        //最终返回值

watch(fileList, (val, oldValue) => {
    frm.value.done = val && (val instanceof Array) && val.length > 0 ? "ok" : null;
});

const emit = defineEmits(["change", "complete"]);

function show(k, eparams) {
    proxy.resetForm("frmRef");
    key.value = k;
    frm.value.done = null;
    if (eparams) {
        extParams.value = eparams;
    }
    fileList.value = [];
    resp.value = null;
    formVisible.value = true;
}
function close() {
    proxy.$refs['frmRef'].resetFields();
    formVisible.value = false;
    loading.value = false;
}
function handleUploadChange(file, fList) {
    fileList.value = [file];
}
function handleUploadRemove(file, fList) {
    fileList.value = [];
}
function preUpload(file) {
    if (file.size > proxy.maxSize) {
        proxy.$modal.msgError('单个文件不能超过' + div(proxy.maxSize, 1048576) + 'MB');
        return false;
    }
    return true;
}
function uploadFile(req) {
    var formData = new FormData();
    //文件参数
    formData.append(proxy.mfileName, req.file);
    //其它参数
    if (extParams.value) {
        for (let key in extParams.value) {
            formData.append(key, extParams.value[key]);
        }
    }
    resp.value = null;
    proxy.request.post(req.action, formData).then((res) => {
        let resData = null;
        emit("change", key.value, res, data => {
            resData = data;
        });

        if (resData != 'undefined' && resData != null) {
            resp.value = resData;
        }
        proxy.$refs.frmRef.validate((valid) => {});
    }).catch((err) => {

    });
}
//完成并返回
function completeUpload() {
    proxy.$refs.frmRef.validate((valid) => {
        if (valid) {
            loading.value = true;
            emit("complete", key.value, resp.value);
            proxy.$refs['frmRef'].resetFields();
            formVisible.value = false;
            loading.value = false;
        }
    });
}
//数字除法
function div(arg1, arg2) {
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
defineExpose({
    show,
    close
})
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


<!--
父组件可以自定义change和complete这两个事件。
1、change用于解析文件上传的后台响应数据，返回任意值；
2、complete用于处理组件结束之后的善后操作，参数为change返回值。

eg.      <file-upload ref="fileUpload" :title="'上传文件'" :fileLabel="'文件'" :fileTips="'请选择文件，文件不要超过2MB'"
                     :accept="''" :maxSize="2097152" :fileErr="'请选择文件'" :fileType="'text'"
                     :actionUrl="'/file/upload'" v-on:change="onUploadChanged" v-on:complete="onUploadCompleted" />
-->
