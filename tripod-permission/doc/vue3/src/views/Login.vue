<template>
	<el-form :model="ruleForm2" :rules="rules2" ref="formRef" label-position="left" label-width="0px" class="login-container">
		<h3 class="title">系统登录</h3>
		<el-form-item prop="account">
			<el-input type="text" v-model="ruleForm2.account" clearable autocomplete="off" placeholder="账号"></el-input>
		</el-form-item>
		<el-form-item prop="checkPass">
			<el-input type="password" v-model="ruleForm2.checkPass" clearable show-password autocomplete="off"
						 placeholder="密码" @keyup.enter.native="handleSubmit2"></el-input>
		</el-form-item>
		<el-checkbox v-model="checked" class="remember">记住密码</el-checkbox>
		<el-form-item style="width:100%;">
			<el-button type="primary" style="width:100%;" @click.native.prevent="handleSubmit2" :loading="loading">登录</el-button>
			<!--<el-button @click.native.prevent="handleReset2">重置</el-button>-->
		</el-form-item>
	</el-form>
</template>

<script setup>
//import NProgress from 'nprogress';
import {Base64} from 'js-base64';

const router = useRouter();
const {proxy} = getCurrentInstance();

const ruleForm2 = ref({
    account: "yuwei",
    checkPass: "12345"
});
const loading = ref(false);
const checked = ref(false);

const rules2 = {
    account: [{ required: true, trigger: "blur", message: "请输入账号" }],
    checkPass: [{ required: true, trigger: "blur", message: "请输入密码" }]
};

function init() {
    let account = getLocalStored("account");
    let pwd = getLocalStored("password");
    let password = pwd ? Base64.decode(pwd) : null;
    if (account === "undefined") {
        account = null;
        password = null;
    }
    if (account) {
        ruleForm2.value.account = account;
        ruleForm2.value.checkPass = password;
        checked.value = true;
    }
}
init();

function handleReset2() {
    proxy.$refs.formRef.resetFields();
}

function handleSubmit2() {
    proxy.$refs.formRef.validate(valid => {
        if (valid) {
            loading.value = true;
            //NProgress.start();

            const params = "accountNo=" + ruleForm2.value.account + "&password=" + proxy.$md5(ruleForm2.value.checkPass);
            proxy.request.post('/admin/login', params).then((res) => {
                proxy.session.putUser(res.data.data);
                proxy.session.putToken(res.headers[import.meta.env.VITE_APP_TOKEN_NAME]);
                proxy.$modal.msgSuccess(res.data.msg);
                saveLocalStored();
                router.push({ path: "/" });
            }).catch((err) => {
                loading.value = false;
            });
        } else {
            loading.value = false;
            return false;
        }
    });
}

function saveLocalStored() {
    proxy.cache.local.remove("account");
    proxy.cache.local.remove("password");
    if (checked.value) {
        proxy.cache.local.set("account", ruleForm2.value.account);
        // base64加密密码
        let password = Base64.encode(ruleForm2.value.checkPass);
        proxy.cache.local.set("password", password);
    }
}
function getLocalStored(key) {
    return proxy.cache.local.get(key);
}
</script>

<style lang="scss" scoped>
.login-container {
    /*box-shadow: 0 0px 8px 0 rgba(0, 0, 0, 0.06), 0 1px 0px 0 rgba(0, 0, 0, 0.02);*/
    -webkit-border-radius: 5px;
    border-radius: 5px;
    -moz-border-radius: 5px;
    background-clip: padding-box;
    margin: 180px auto;
    width: 350px;
    padding: 35px 35px 15px 35px;
    background: #fff;
    border: 1px solid #eaeaea;
    box-shadow: 0 0 25px #cac6c6;
    .title {
        margin: 0px auto 40px auto;
        text-align: center;
        color: #505458;
    }
    .remember {
        margin: 0px 0px 35px 0px;
    }
}
</style>
