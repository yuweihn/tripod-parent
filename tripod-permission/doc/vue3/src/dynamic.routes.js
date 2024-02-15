import Layout from '@/views/Layout';
//import Layout from '@/layout';
import {basicRoutes} from '@/basic.routes';
import modal from '@/assets/js/modal';
import request from '@/assets/js/request';
import {shallowRef} from 'vue';


// 匹配views里面所有的.vue文件
const modules = import.meta.glob('./views/**/*.vue');

const dynamicRouteStore = defineStore(
    'dynamicRoute',
    {
        state: () => ({
            routes: [],
            addRoutes: [],
            isDynamicMenuLoaded: false
        }),
        actions: {
            generateMenus() {
                return new Promise((resolve, reject) => {
                    request.get('/sys/admin/permission/menu/list').then(res => {
                        modal.msgSuccess(res.data.msg);
                        var menus = filterAsyncRouters(res.data.data);
                        this.addRoutes = postRouters(menus);
                        this.routes = basicRoutes.concat(menus);
                        this.isDynamicMenuLoaded = true;
                        resolve(res);
                    }).catch(error => {
                        this.addRoutes = [];
                        this.routes = basicRoutes;
                        this.isDynamicMenuLoaded = true;
                        modal.msgError(error.message);
                        reject(error);
                    })
                });
            },
            resetDynamicMenuLoaded() {
                this.isDynamicMenuLoaded = false;
            }
        }
    }
)

function postRouters(routers) {
    routers = routers == null ? [] : routers;
    //routers.push({ path: '*', redirect: '/404', hidden: true });
    routers.push({ path: '/:pathMatch(.*)', redirect: '/404', hidden: true });
    return routers;
}
function filterAsyncRouters(routers) {
    return routers.filter(router => {
        if (router.path == null || router.path == '' || router.path == '/') {
            router.path = "/" + router.name + router.id;
        }
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
            router.component = shallowRef(Layout);
        } else if (router.permType === "M") {
            try {
                const viewRes = loadView(router.component);
                if (!viewRes) {
                    return false;
                } else {
                    router.component = viewRes;
                    return true;
                }
            } catch (e) {
                return false;
            }
        }
        return true;
    });
}

function loadView(view) {
    let res;
    for (const path in modules) {
        const dir = path.split('views/')[1].split('.vue')[0];
        if (dir === view) {
            res = () => modules[path]();
        }
    }
    return res;
}

export default dynamicRouteStore;
