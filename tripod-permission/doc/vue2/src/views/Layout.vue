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
					<span class="el-dropdown-link userinfo-inner" @click="refreshSession">
						<img :src="this.sysUserAvatar" @click="showBigAvatar" class="avatar"/>
						{{this.$util.slice(sysUserName, 10)}}
					</span>
					<el-dropdown-menu slot="dropdown" class="header-pop">
                        <el-dropdown-item @click.native="toChangePassword"><span>修改密码</span></el-dropdown-item>
                        <el-dropdown-item divided @click.native="logout"><span>退出登录</span></el-dropdown-item>
					</el-dropdown-menu>
				</el-dropdown>
			</el-col>
		</el-col>
		<el-col :span="24" class="main">
			<aside :class="collapsed ? 'menu-collapsed' : 'menu-expanded'">
				<!--导航菜单(未折叠)-->
				<el-menu :default-active="$route.path" class="el-menu-vertical-demo menu-scroll" @open="handleOpen" @close="handleClose"
				          @select="handleSelect" unique-opened router v-show="!collapsed">
					<template v-for="(item, index) in routeList" v-if="!item.hidden">
						<el-menu-item v-if="item.ifExt" @click="redirectExtUrl(item.path)"><!--外链接-->
                            <svg-icon v-if="item.meta && item.meta.icon" :icon-class="item.meta.icon" />
                            <span v-if="item.meta && item.meta.title">{{item.meta.title}}</span>
                            <span v-else>{{item.name}}</span>
						</el-menu-item>
						<el-submenu :index="index + ''" v-else-if="!item.leaf"><!--非叶子节点-->
							<template slot="title">
                                <svg-icon v-if="item.meta && item.meta.icon" :icon-class="item.meta.icon" />
                                <span v-if="item.meta && item.meta.title">{{item.meta.title}}</span>
                                <span v-else>{{item.name}}</span>
							</template>
                            <el-menu-item v-for="child in item.children" :index="child.ifExt ? null : child.path" :key="child.path"
                                        v-if="!child.hidden" @click="child.ifExt ? redirectExtUrl(child.path) : 'return null'">
                                <svg-icon v-if="child.meta && child.meta.icon" :icon-class="child.meta.icon" />
                                <span v-if="child.meta && child.meta.title">{{child.meta.title}}</span>
                                <span v-else>{{child.name}}</span>
                            </el-menu-item>
						</el-submenu>
						<el-menu-item v-else-if="item.leaf && item.children.length > 0" :index="item.children[0].path"><!--叶子节点-->
                            <svg-icon v-if="item.children[0].meta && item.children[0].meta.icon" :icon-class="item.children[0].meta.icon" />
                            <span v-if="item.children[0].meta && item.children[0].meta.title">{{item.children[0].meta.title}}</span>
                            <span v-else>{{item.children[0].name}}</span>
						</el-menu-item>
					</template>
				</el-menu>
				<!--导航菜单(折叠后)-->
				<ul class="el-menu el-menu-vertical-demo collapsed" v-show="collapsed" ref="menuCollapsed">
					<li v-for="(item, index) in routeList" v-if="!item.hidden" class="el-submenu item">
						<template v-if="item.ifExt"><!--外链接-->
							<li class="el-submenu" @click="redirectExtUrl(item.path)">
								<div class="el-submenu__title el-menu-item" style="padding-left: 20px;height: 56px;line-height: 56px;padding: 0 20px;">
                                    <svg-icon v-if="item.meta && item.meta.icon" :icon-class="item.meta.icon" />
								</div>
							</li>
						</template>
						<template v-else-if="!item.leaf"><!--非叶子节点-->
							<div class="el-submenu__title" style="padding: 0px 0px 0px 20px;" @mouseover="showMenu(index, true)"
                                        @mouseout="showMenu(index, false)">
						        <svg-icon v-if="item.meta && item.meta.icon" :icon-class="item.meta.icon" />
							</div>
							<ul class="el-menu submenu" :class="'submenu-hook-' + index" @mouseover="showMenu(index, true)"
                                        @mouseout="showMenu(index, false)">
								<li v-for="child in item.children" v-if="!child.hidden" :key="child.path" class="el-menu-item"
                                            style="padding: 0px 0px 0px 30px;" :class="$route.path == child.path ? 'is-active' : ''"
                                            @click="child.ifExt ? redirectExtUrl(child.path) : $route.path != child.path && $router.push(child.path)">
                                    <svg-icon v-if="child.meta && child.meta.icon" :icon-class="child.meta.icon" />
                                    <span v-if="child.meta && child.meta.title">{{child.meta.title}}</span>
                                    <span v-else>{{child.name}}</span>
                                </li>
							</ul>
						</template>
						<template v-else><!--叶子节点-->
							<li class="el-submenu">
								<div class="el-submenu__title el-menu-item" style="padding-left: 20px;height: 56px;line-height: 56px;padding: 0 20px;"
								        :class="$route.path == item.children[0].path ? 'is-active' : ''"
								        @click="$route.path != item.children[0].path && $router.push(item.children[0].path)">
                                    <svg-icon v-if="item.children[0].meta && item.children[0].meta.icon" :icon-class="item.children[0].meta.icon" />
								</div>
							</li>
						</template>
					</li>
				</ul>
			</aside>
			<section class="content-container">
				<div class="grid-content bg-purple-light">
					<el-col :span="24" class="breadcrumb-container">
						<el-breadcrumb separator="/" class="breadcrumb-inner">
							<el-breadcrumb-item v-for="item in $route.matched" :key="item.path" v-if="item.meta.title || item.name" class="title">
								{{item.meta.title || item.name}}
							</el-breadcrumb-item>
						</el-breadcrumb>
					</el-col>
					<el-col :span="24" class="content-wrapper">
						<transition name="fade" mode="out-in">
							<router-view></router-view>
						</transition>
					</el-col>
				</div>
				<el-dialog :visible.sync="isAvatarVisible" width="580px" :before-close="closeBigAvatar" v-drag>
					<img :src="this.sysUserAvatar" width="100%" class="avatar">
				</el-dialog>

				<!--修改密码界面-->
				<el-dialog title="修改密码" :visible.sync="pwdFormVisible" width="450px" :close-on-click-modal="true" v-drag>
					<el-form :model="pwdForm" label-width="100px" :rules="pwdFormRules" ref="pwdForm" style="padding-right: 10px;">
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
					<div slot="footer" class="dialog-footer" style="padding-right: 10px;">
						<el-button @click.native="pwdFormVisible = false">取消</el-button>
						<el-button type="primary" @click.native="pwdSubmit" :loading="pwdLoading">提交</el-button>
					</div>
				</el-dialog>
			</section>
		</el-col>
	</el-row>
</template>

<script>
import logoImg from '@/components/img/logo.png';

export default {
    data() {
        var isPasswordEquals = (rule, value, callback) => {
            if (!value) {
                return callback(new Error('请再次输入新密码'));
            }
            setTimeout(() => {
                if (value != this.pwdForm.password) {
                    callback(new Error('两次输入密码不一致'));
                } else {
                    callback();
                }
            }, 0);
        };

        return {
            logoSrc: logoImg,
            collapsed: false,
            sysUserName: '',
            sysUserAvatar: '',
            isAvatarVisible: false,

            pwdFormVisible: false,//修改密码界面是否显示
            pwdLoading: false,
            pwdFormRules: {
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
            },
            //修改密码界面数据
            pwdForm: {
                oldPassword: null,
                password: null,
                rptPassword: null
            }
        }
    },

    computed: {
        //动态加载菜单
        routeList() {
            return this.$store.getters.routes;
        }
    },

    methods: {
        handleOpen() {
            //console.log('handleOpen');
        },
        handleClose() {
            //console.log('handleClose');
        },
        handleSelect: function(a, b) {

        },

        redirectExtUrl(path) {
            window.open(path);
        },

        //显示修改密码界面
        toChangePassword: function() {
            this.pwdFormVisible = true;
            this.pwdForm = {
                password: null,
                rptPassword: null
            };
            this.resetForm("pwdForm");
        },
        //提交修改密码
        pwdSubmit: function() {
            this.$refs.pwdForm.validate((valid) => {
                if (valid) {
                    var params = "oldPassword=" + this.$md5(this.pwdForm.oldPassword) + "&password=" + this.$md5(this.pwdForm.password);
                    this.pwdLoading = true;

                    this.$axios.post(this.$global.baseUrl + '/admin/change-my-password', params).then((res) => {
                        if (res.data.code === '0000') {
                            this.$message({type: "success", message: res.data.msg});
                        } else {
                            this.$message.error(res.data.msg);
                        }
                        this.$refs['pwdForm'].resetFields();
                        this.pwdFormVisible = false;
                        this.pwdLoading = false;
                    }).catch((err) => {
                        this.pwdLoading = false;
                        this.$message.error(err.message);
                    });
                }
            });
        },

        //退出登录
        logout: function() {
            var me = this;
            this.$confirm('确定退出吗?', '提示', {}).then(() => {
                me.$axios.post(me.$global.baseUrl + '/admin/logout', '').then((res) => {
                    me.$session.removeUser();
                    me.$session.removeToken();
                    me.removeDynamicLoaded();
                    me.$router.push({path: '/login'});
                }).catch((err) => {
                    me.$message.error(err.message);
                    me.$router.push({path: '/login'});
                });
            }).catch(() => {

            });
        },
        showMenu(i, status) {
            this.$refs.menuCollapsed.getElementsByClassName('submenu-hook-' + i)[0].style.display = status ? 'block' : 'none';
        },
        showBigAvatar() {
            this.isAvatarVisible = true;
        },
        closeBigAvatar() {
            this.isAvatarVisible = false;
        },
        refreshSession() {
            this.$axios.get(this.$global.baseUrl + '/admin/session', {}).then((res) => {
                if (res.data.code === '0000') {
                    this.$session.putUser(res.data.data);
                    this.$session.putToken(res.headers[this.$global.tokenHeaderName]);
                    this.resolveUser();
                    this.$message({type: "success", message: res.data.msg});
                } else {
                    this.$message.error(res.data.msg);
                }
            }).catch((err) => {
                this.$message.error(err.message);
            });
        },
        resolveUser() {
            var user = this.$session.getUser();
            if (user) {
                this.sysUserName = user.accountNo || '';
                this.sysUserAvatar = user.avatar || require('@/components/img/avatar.png');
            }
        }
    },
    mounted() {
        this.resolveUser();
    }
}
</script>

<style scoped lang="scss">
@import '~scss_vars';

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
        .userinfo {
            text-align: right;
            padding-right: 35px;
            float: right;
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
    .main {
        display: flex;
        // background: #324057;
        position: absolute;
        top: 60px;
        bottom: 0px;
        overflow: hidden;
        aside {
            flex: 0 0 230px;
            width: 230px;
            // position: absolute;
            // top: 0px;
            // bottom: 0px;
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
            // background: #f1f2f7;
            flex: 1;
            // position: absolute;
            // right: 0px;
            // top: 0px;
            // bottom: 0px;
            // left: 230px;
            overflow-y: scroll;
            padding: 20px;
            .breadcrumb-container {
                //margin-bottom: 15px;
                .title {
                    //width: 200px;
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
