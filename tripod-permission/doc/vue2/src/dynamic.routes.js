import Layout from '@/views/Layout.vue';
import {Message} from 'element-ui';
import {basicRoutes} from '@/basic.routes';
import axios from 'axios';
import global from '@/components/js/global';


const dynamicRoutes = {
    state: {
        routes: [],
        addRoutes: [],
        isDynamicMenuLoaded: false
    },
    mutations: {
        SET_ROUTES: (state, routes) => {
            state.addRoutes = routes;
            state.routes = basicRoutes.concat(routes);
            state.isDynamicMenuLoaded = true;
        },
        REMOVE_DYNAMIC_LOADED: (state) => {
            state.isDynamicMenuLoaded = false;
        }
    },
    actions: {
        GenerateMenus({commit}) {
            return new Promise(resolve => {
                // 向后端请求权限菜单数据
                axios.get(global.baseUrl + '/sys/admin/permission/menu/list', {}).then((res) => {
                    if (res.data.code === '0000') {
                        Message({type: "success", message: res.data.msg});
                        var menus = filterAsyncRouters(res.data.data);
                        postRouters(commit, resolve, menus);
                    } else {
                        Message.error(res.data.msg);
                        postRouters(commit, resolve);
                    }
                }).catch((err) => {
                    Message.error(err.message);
                    postRouters(commit, resolve);
                });
            });
        },
        RemoveDynamicMenuLoaded({commit}) {
            return new Promise(resolve => {
                commit('REMOVE_DYNAMIC_LOADED');
                resolve();
            });
        }
    }
}
function postRouters(commit, resolve, routers) {
    routers = routers == null ? [] : routers;
    routers.push({ path: '*', redirect: '/404', hidden: true });
    commit('SET_ROUTES', routers);
    resolve(routers);
}
function filterAsyncRouters(routers) {
    return routers.filter(router => {
        if (router.ifExt) {
            return true;
        }
        if (router.children && router.children.length) {
            router.children = filterAsyncRouters(router.children);
        }
        if (router.permType === "D") {
            if (!router.children || !router.children.length) {
                return false;
            }
            router.component = Layout;
        } else if (router.permType === "M") {
            if (!isFileExist(router.component)) {
                return false;
            }
            router.component = loadView(router.component);
        }
        return true;
    });
}

function isFileExist(view) {
    try {
        require(`@/views/${view}`);
        return true;
    } catch (e) {
        return false;
    }
}
function loadView(view) {
    return (resolve) => require([`@/views/${view}`], resolve);
}

export default dynamicRoutes;
