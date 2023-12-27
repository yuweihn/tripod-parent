import {router} from '@/basic.routes';
import { ElMessage } from 'element-plus';
import NProgress from 'nprogress';
import 'nprogress/nprogress.css';
import session from '@/assets/js/session';
import util from '@/assets/js/util';
import settings from './settings';
import request from '@/assets/js/request';
import dynamicRouteStore from '@/dynamic.routes';
import dynamicButtonStore from '@/dynamic.buttons';

//NProgress.configure({ showSpinner: false });


const defaultTitle = document.title;
router.beforeEach((to, from, next) => {
    NProgress.start();
    document.title = (defaultTitle && to.meta.title) ? (defaultTitle + " - " + to.meta.title)
                            : (defaultTitle || to.meta.title || "");

	const user = session.getUser();
    const drs = dynamicRouteStore();
    const dbs = dynamicButtonStore();
	if (!user) {
        if (to.path === '/login') {
            next();
        } else {
            request.post("/admin/logout", null, {rejectRepeat: false}).then((res) => {}).catch((err) => {});
            next({path: '/login'});
        }
	} else {
        if (!drs.isDynamicMenuLoaded) {
            drs.generateMenus().then(res => {
                drs.addRoutes.forEach(rt => {
                    if (!util.isHttp(rt.path)) {
                        router.addRoute(rt);
                    }
                });
                next({...to, replace: true});
            }).catch(err => {
                next({path: '/'});
            });
            return;
        }
        if (!dbs.isDynamicButtonLoaded) {
            dbs.generateButtons().then(res => {
                next();
            }).catch(err => {
                next({path: '/'});
            });
            return;
        }
        next();
    }
});

router.afterEach(() => {
    NProgress.done();
});
