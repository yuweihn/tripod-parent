import axios from 'axios';
import store from '@/vuex/store';
import {Message} from 'element-ui';
import session from '@/components/js/session';
import {router, createBasicRouter} from '@/basic.routes';
import global from '@/components/js/global';
import NProgress from 'nprogress';

//NProgress.configure({showSpinner: false});

const defaultTitle = document.title;
router.beforeEach((to, from, next) => {
    NProgress.start();
    document.title = defaultTitle + (to.meta.title ? " - " + to.meta.title : "");

	let user = session.getUser();
	if (!user) {
        if (to.path === '/login') {
            next();
        } else {
            axios.post(global.baseUrl + '/admin/logout', '').then((res) => {});
            next({path: '/login'});
        }
	} else {
        //加载动态菜单
        if (!store.getters.isDynamicMenuLoaded) {
            store.dispatch('GenerateMenus').then(aRoutes => {
//                const newRouter = createBasicRouter();
//                router.matcher = newRouter.matcher;
                aRoutes.forEach(rt => {
                    router.addRoute(rt);
                });
                next({...to, replace: true});
            }).catch(err => {
                Message.error(err.message);
                next({path: '/'});
            });
        }
        //加载动态按钮
        if (!store.getters.isDynamicButtonLoaded) {
            store.dispatch('GenerateButtons').then(buttons => {
                next();
            }).catch(err => {
                Message.error(err.message);
                next({path: '/'});
            });
        }
        next();
	}
});

router.afterEach(() => {
    NProgress.done();
})
