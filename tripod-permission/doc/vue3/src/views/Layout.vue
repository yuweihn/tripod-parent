<template>
	<el-row class="container">
		<el-col :span="24" class="header">
			<el-col :span="10" class="logo" :class="collapsed ? 'logo-collapse-width' : 'logo-width'">
				<img :src="logoSrc" class="sidebar-logo" /><span class="sidebar-word" v-if="!collapsed">{{settings.title}}</span>
			</el-col>
			<el-col :span="10">
				<div class="tools" @click.prevent="collapsed = !collapsed">
					<i :class="collapsed ? 'fa fa-indent' : 'fa fa-outdent'"></i>
				</div>
			</el-col>
			<el-col :span="4" class="userinfo">
				<el-dropdown trigger="hover">
					<span class="userinfo-inner" @click="refreshSession">
						<img :src="sysUserAvatar" @click="showBigAvatar" class="avatar"/>
						{{$util.slice(sysUserName, 10)}}
					</span>
					<template #dropdown>
                        <el-dropdown-menu slot="dropdown" class="header-pop">
                            <el-dropdown-item @click.native="toChangePassword"><span>修改密码</span></el-dropdown-item>
                            <el-dropdown-item divided @click.native="logout"><span>退出登录</span></el-dropdown-item>
                        </el-dropdown-menu>
					</template>
				</el-dropdown>
			</el-col>
		</el-col>
		<el-col :span="24" class="main">
			<aside :class="collapsed ? 'menu-collapsed' : 'menu-expanded'">
				<!--导航菜单(未折叠)-->
				<el-menu :default-active="route.path" class="menu-scroll" @open="handleOpen" @close="handleClose"
				          @select="handleSelect" unique-opened router v-show="!collapsed">
					<template v-for="(item, idx) in routeList">
                        <div v-if="!item.hidden">
                            <el-menu-item v-if="item.ifExt" :key="'a' + idx" @click="redirectExtUrl(item.path)" :index="''"><!--外链接-->
                                <svg-icon v-if="item.meta && item.meta.icon" :icon-class="item.meta.icon" />
                                <span v-if="item.meta && item.meta.title">{{item.meta.title}}</span>
                                <span v-else>{{item.name}}</span>
                            </el-menu-item>

                            <el-sub-menu v-else-if="!item.leaf" :index="item.path"><!--非叶子节点-->
                                <template #title>
                                    <svg-icon v-if="item.meta && item.meta.icon" :icon-class="item.meta.icon" />
                                    <span v-if="item.meta && item.meta.title">{{item.meta.title}}</span>
                                    <span v-else>{{item.name}}</span>
                                </template>
                                <el-menu-item v-for="(child, cidx) in item.children" :index="child.ifExt ? '' : child.path" :key="'a' + idx + '-' + cidx"
                                             @click="child.ifExt ? redirectExtUrl(child.path) : 'return null'">
                                    <div v-if="!child.hidden">
                                        <svg-icon v-if="child.meta && child.meta.icon" :icon-class="child.meta.icon" />
                                        <span v-if="child.meta && child.meta.title">{{child.meta.title}}</span>
                                        <span v-else>{{child.name}}</span>
                                    </div>
                                </el-menu-item>
                            </el-sub-menu>

                            <el-menu-item v-else-if="item.leaf && item.children.length > 0" :index="item.children[0].path" :key="'a' + idx"><!--叶子节点-->
                                <svg-icon v-if="item.children[0].meta && item.children[0].meta.icon" :icon-class="item.children[0].meta.icon" />
                                <span v-if="item.children[0].meta && item.children[0].meta.title">{{item.children[0].meta.title}}</span>
                                <span v-else>{{item.children[0].name}}</span>
                            </el-menu-item>
                        </div>
					</template>
				</el-menu>
				<!--导航菜单(折叠后)-->
				<ul class="el-menu collapsed" v-show="collapsed" ref="menuCollapsed">
					<li v-for="(item, idx) in routeList" class="el-submenu item">
					    <div v-if="!item.hidden">
						<template v-if="item.ifExt"><!--外链接-->
							<li class="el-submenu" @click="redirectExtUrl(item.path)">
								<div class="el-submenu__title el-menu-item" style="padding-left: 20px;height: 56px;line-height: 56px;padding: 0 20px;">
						            <svg-icon v-if="item.meta && item.meta.icon" :icon-class="item.meta.icon" />
								</div>
							</li>
						</template>
						<template v-else-if="!item.leaf"><!--非叶子节点-->
							<div class="el-submenu__title" style="padding-left: 20px;height: 56px;line-height: 56px;padding: 0 20px;" @mouseover="showMenu(idx, true)"
							            @mouseout="showMenu(idx, false)">
						        <svg-icon v-if="item.meta && item.meta.icon" :icon-class="item.meta.icon" />
							</div>
							<ul class="el-menu submenu" :class="'submenu-hook-' + idx" @mouseover="showMenu(idx, true)"
							            @mouseout="showMenu(idx, false)">
								<li v-for="child in item.children" :key="child.path" class="el-menu-item"
                                            style="padding: 0px 0px 0px 30px;" :class="route.path == child.path ? 'is-active' : ''"
                                            @click="child.ifExt ? redirectExtUrl(child.path) : route.path != child.path && $router.push(child.path)">
								    <div v-if="!child.hidden">
                                        <svg-icon v-if="child.meta && child.meta.icon" :icon-class="child.meta.icon" />
                                        <span v-if="child.meta && child.meta.title">{{child.meta.title}}</span>
                                        <span v-else>{{child.name}}</span>
                                    </div>
                                </li>
							</ul>
						</template>
						<template v-else><!--叶子节点-->
							<li class="el-submenu">
								<div class="el-submenu__title el-menu-item" style="padding-left: 20px;height: 56px;line-height: 56px;padding: 0 20px;"
                                            :class="route.path == item.children[0].path ? 'is-active' : ''"
                                            @click="route.path != item.children[0].path && $router.push(item.children[0].path)">
                                    <svg-icon v-if="item.children[0].meta && item.children[0].meta.icon" :icon-class="item.children[0].meta.icon" />
								</div>
							</li>
						</template>
						</div>
					</li>
				</ul>
			</aside>
			<section class="content-container">
				<div class="grid-content bg-purple-light">
					<el-col :span="24" class="breadcrumb-container">
						<el-breadcrumb separator="/" :separator-icon="ArrowRight" class="breadcrumb-inner">
							<el-breadcrumb-item v-for="item in route.matched" :key="item.path" class="title">
								<span v-if="item.meta.title || item.name">{{item.meta.title || item.name}}</span>
							</el-breadcrumb-item>
						</el-breadcrumb>
					</el-col>
					<br/>
					<el-col :span="24" class="content-wrapper">
                        <router-view v-slot="{ Component }">
                            <transition>
                                <component :is="Component" />
                            </transition>
                        </router-view>
					</el-col>
				</div>

				<el-dialog v-model="isAvatarVisible" width="580px" :before-close="closeBigAvatar" draggable>
					<img :src="sysUserAvatar" class="avatar big-avatar" />
				</el-dialog>

				<!--修改密码界面-->
				<el-dialog title="修改密码" v-model="pwdFormVisible" width="450px" :close-on-click-modal="true" draggable>
					<el-form :model="pwdForm" label-width="100px" :rules="pwdFormRules" ref="pwdFormRef">
						<el-form-item label="原密码" prop="oldPassword">
							<el-input v-model="pwdForm.oldPassword" clearable autocomplete="off" show-password style="width: 100%" />
						</el-form-item>
						<el-form-item label="新密码" prop="password">
							<el-input v-model="pwdForm.password" clearable autocomplete="off" show-password style="width: 100%" />
						</el-form-item>
						<el-form-item label="重复新密码" prop="rptPassword">
							<el-input v-model="pwdForm.rptPassword" clearable autocomplete="off" show-password style="width: 100%" />
						</el-form-item>
					</el-form>
					<div slot="footer" class="dialog-footer">
						<el-button @click.native="pwdFormVisible = false">取消</el-button>
						<el-button type="primary" @click.native="pwdSubmit" :loading="pwdLoading">提交</el-button>
					</div>
				</el-dialog>
			</section>
		</el-col>
	</el-row>
</template>

<script setup>
import {ArrowRight} from '@element-plus/icons-vue';
import dynamicRouteStore from '@/dynamic.routes';
import dynamicButtonStore from '@/dynamic.buttons';
import logoImg from '@/assets/img/logo.png';
import avatarImg from '@/assets/img/avatar.png';

const route = useRoute();
const router = useRouter();
const drs = dynamicRouteStore();
const dbs = dynamicButtonStore();
const {proxy} = getCurrentInstance();

var isPasswordEquals = (rule, value, callback) => {
    if (!value) {
        return callback(new Error('请再次输入新密码'));
    }
    setTimeout(() => {
        if (value != pwdForm.value.password) {
            callback(new Error('两次输入密码不一致'));
        } else {
            callback();
        }
    }, 0);
};

const logoSrc = ref(logoImg);
const avatarSrc = ref(avatarImg);
const collapsed = ref(false);
const sysUserName = ref('');
const sysUserAvatar = ref('');
const isAvatarVisible = ref(false);
const pwdFormVisible = ref(false);
const pwdLoading = ref(false);
const pwdForm = ref({
    oldPassword: null,
    password: null,
    rptPassword: null
});

const pwdFormRules = {
    oldPassword: [
        {required: true, message: '请输入原密码', trigger: 'blur'}
    ],
    password: [
        {required: true, message: '请输入新密码', trigger: 'blur'}
    ],
    rptPassword: [
        {required: true, message: '请再次输入新密码', trigger: 'blur'}
                , {validator: isPasswordEquals, trigger: 'blur'}
    ]
};

const routeList =  computed(() => drs.routes);

function handleOpen() {
    //console.log('handleOpen');
}
function handleClose() {
    //console.log('handleClose');
}
function handleSelect(a, b) {

}
function redirectExtUrl(path) {
    window.open(path);
}
function toChangePassword() {
    pwdFormVisible.value = true;
    pwdForm.value.password = null;
    pwdForm.value.rptPassword = null;
    proxy.resetForm("pwdFormRef");
}
//提交修改密码
function pwdSubmit() {
    proxy.$refs.pwdFormRef.validate((valid) => {
        if (valid) {
            pwdLoading.value = true;

            const params = "oldPassword=" + proxy.$md5(pwdForm.value.oldPassword) + "&password=" + proxy.$md5(pwdForm.value.password);
            proxy.request.post('/admin/change-my-password', params).then((res) => {
                proxy.$modal.msgSuccess(res.data.msg);
                proxy.$refs['pwdFormRef'].resetFields();
                pwdFormVisible.value = false;
                pwdLoading.value = false;
            }).catch((err) => {
                pwdLoading.value = false;
            });
        }
    });
}
//退出登录
function logout() {
    proxy.$modal.confirm('确定退出吗?', '提示', {}).then(function() {
        proxy.request.post("/admin/logout", null, {rejectRepeat: false}).then((res) => {
            proxy.session.removeUser();
            proxy.session.removeToken();
            proxy.removeDynamicLoaded();
        }).then(() => {
            router.push({path: '/login'});
        }).catch((err) => {
        });
    }).catch((err) => {});
}
function showMenu(i, status) {
    proxy.$refs.menuCollapsed.getElementsByClassName('submenu-hook-' + i)[0].style.display = status ? 'block' : 'none';
}
function showBigAvatar() {
    isAvatarVisible.value = true;
}
function closeBigAvatar() {
    isAvatarVisible.value = false;
}
function refreshSession() {
    proxy.request.get('/admin/session').then((res) => {
        proxy.session.putUser(res.data.data);
        proxy.session.putToken(res.headers[import.meta.env.VITE_APP_TOKEN_NAME]);
        proxy.removeDynamicLoaded();
        proxy.$modal.msgSuccess(res.data.msg);
        resolveUser();
    }).catch((err) => {

    });
}
function resolveUser() {
    var user = proxy.session.getUser();
    if (user) {
        sysUserName.value = user.accountNo || '';
        sysUserAvatar.value = user.avatar || avatarSrc.value;
    }
}
resolveUser();
</script>

<style scoped lang="scss">
@import '@/assets/styles/vars.scss';

.container {
    position: absolute;
    top: 0px;
    bottom: 0px;
    width: 100%;
    .header {
        height: 60px;
        line-height: 60px;
        background: $color-primary;
        vertical-align: middle;
        color: #fff;
        .el-col {
            float: left;
        }
        .userinfo {
            text-align: right;
            padding-right: 35px;
            float: right;
            .el-dropdown {
                line-height: 60px;
            }
            .userinfo-inner {
                cursor: pointer;
                color: #fff;
                .avatar {
                    width: 40px;
                    height: 40px;
                    border-radius: 20px;
                    margin: 10px 0px 10px 10px;
                    float: right;
                }
            }
        }
        .logo {
            height: 60px;
            font-size: 22px;
            padding: 0 10px;
            border-color: rgba(238, 241, 146, 0.3);
            border-right-width: 1px;
            border-right-style: solid;
            display: flex;
            align-items: center;
        }
        .logo-width {
            width: 260px;
        }
        .logo-collapse-width {
            width: 60px
        }
        .sidebar-logo {
            width: 40px;
            height: 40px;
            vertical-align: middle;
            border-radius: 20px;
        }
        .sidebar-word {
            margin-left: 10px;
        }
        .tools {
            padding: 0px 23px;
            width: 14px;
            height: 60px;
            line-height: 60px;
            cursor: pointer;
        }
    }
    .big-avatar {
        width: 100%;
    }
    .main {
        display: flex;
        position: absolute;
        top: 60px;
        bottom: 0px;
        overflow: hidden;
        width: 100%;
        aside {
            flex: 0 0 230px;
            width: 230px;
            .el-menu {
                height: 100%;
                width: auto !important;
            }
            .collapsed {
                width: 60px;
                .item {
                    position: relative;
                }
                .submenu {
                    position: absolute;
                    top: 0px;
                    left: 60px;
                    z-index: 99999;
                    height: auto;
                    display: none;
                }
            }
        }
        .menu-collapsed {
            flex: 0 0 60px;
            width: 60px;
        }
        .menu-expanded {
            flex: 0 0 260px;
            width: 260px;
        }
        .content-container {
            flex: 1;
            overflow-y: scroll;
            padding: 20px;
            .breadcrumb-container {
                //margin-bottom: 15px;
                .title {
                    float: left;
                    color: #8492a6;
                    font-size: 14px;
                    font-weight: bold;
                }
                .breadcrumb-inner {
                    float: left;
                }
            }
            .content-wrapper {
                margin-top: 15px;
                background-color: #fff;
                box-sizing: border-box;
            }
        }
        .menu-scroll {
            overflow-x: hidden;
            overflow-y: scroll !important;
        }
    }
}
.header-pop span {
    margin: 2px 5px;
    text-align: center;
}
</style>
