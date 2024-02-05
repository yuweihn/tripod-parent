<template>
	<el-form :model="ruleForm2" :rules="rules2" ref="ruleForm2" label-position="left" label-width="0px" class="login-container">
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

<script>
//import NProgress from 'nprogress'
const Base64 = require('js-base64').Base64;

export default {
    data() {
        return {
            loading: false,
            ruleForm2: {
                account: null,
                checkPass: null
            },
            rules2: {
                account: [
                    { required: true, message: '请输入账号', trigger: 'blur' }
                ],
                checkPass: [
                    { required: true, message: '请输入密码', trigger: 'blur' }
                ]
            },
            checked: false
        };
    },
    mounted() {
        //页面加载时从cookie获取登录信息
        let account = this.getLocalStored("account");
        let password = Base64.decode(this.getLocalStored("password"));
        if (account === "undefined") {
            account = null;
            password = null;
        }
        if (account) {
            this.ruleForm2.account = account;
            this.ruleForm2.checkPass = password;
            this.checked = true;
        }
    },
    methods: {
        handleReset2() {
            this.$refs.ruleForm2.resetFields();
        },
        handleSubmit2(ev) {
            var this0 = this;
            this0.$refs.ruleForm2.validate((valid) => {
                if (valid) {
                    this0.loading = true;
                    //NProgress.start();
                    var loginParams = "accountNo=" + this0.ruleForm2.account + "&password=" + this0.$md5(this0.ruleForm2.checkPass);
                    this0.$axios.post(this0.$global.baseUrl + '/admin/login', loginParams).then((res) => {
                        if (res.data.code === '0000') {
                            this0.$session.putUser(res.data.data);
                            this0.$session.putToken(res.headers[this0.$global.tokenHeaderName]);
                            this0.$message({type: "success", message: res.data.msg});
                            this0.$router.push({path: '/'}, onComplete => {}, onAbort => {});
                            this0.saveLocalStored();
                        } else {
                            this0.$message.error(res.data.msg);
                        }
                        this0.loading = false;
                    }).catch((err) => {
                        this0.loading = false;
                        this0.$message.error(err.message);
                    });
                } else {
                    this0.loading = false;
                    return false;
                }
            });
        },

        saveLocalStored() {
            this.$cache.local.remove("account");
            this.$cache.local.remove("password");
            if (this.checked) {
                this.$cache.local.set("account", this.ruleForm2.account);
                // base64加密密码
                let password = Base64.encode(this.ruleForm2.checkPass);
                this.$cache.local.set("password", password);
            }
        },
        getLocalStored(key) {
            return this.$cache.local.get(key);
        }
    }
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
