<template>
	<!--文件上传界面-->
	<el-dialog :title="title" v-model="formVisible" :close-on-click-modal="modal" :width="width" append-to-body draggable>
		<el-form :model="frm" label-width="80px" ref="frmRef">
			<el-form-item :label="fileLabel">
				<el-upload :action="actionUrl" :list-type="fileType" :http-request="uploadFile" :file-list="fileList"
				            :before-upload="preUpload" :accept="accept">
					<el-button type="primary">点击上传</el-button>
                    <template #tip>
                        <div class="el-upload-tip2">{{fileTips}}</div>
                    </template>
				</el-upload>
			</el-form-item>
		</el-form>
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
const frm = ref({});
const extParams = ref({});     //调接口时需要的额外的参数
const fileList = ref([]);

const emit = defineEmits(["change"]);

function show(k, eparams) {
    proxy.resetForm("frmRef");
    fileList.value = [];
    key.value = k;
    if (eparams) {
        extParams.value = eparams;
    }
    formVisible.value = true;
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
    proxy.request.post(req.action, formData).then((res) => {
        proxy.$refs['frmRef'].resetFields();
        fileList.value = [];
        formVisible.value = false;
        emit("change", key.value, res);
    }).catch((err) => {});
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
    show
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
父组件可以自定义change事件，用于解析文件上传的后台响应数据。
eg.      <file-upload ref="fileUpload" :title="'上传文件'" :fileLabel="'文件'" :fileTips="'请选择文件，文件不要超过2MB'"
                     :accept="''" :maxSize="2097152" :fileType="'text'"
                     :actionUrl="'/file/upload'" v-on:change="onUploadChanged" />
-->
