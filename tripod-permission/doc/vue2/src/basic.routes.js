import Vue from 'vue';
import VueRouter from 'vue-router';

import Layout from '@/views/Layout.vue';

Vue.use(VueRouter);


export const basicRoutes = [
	{
        path: '/login',
        name: '',
        component: (resolve) => require(['@/views/Login'], resolve),
        hidden: true
	},
	{
        path: '/404',
        name: '',
        component: (resolve) => require(['@/views/404'], resolve),
        hidden: true
	},
//	{
//		path: '/',
//		redirect: '/home',
//		hidden: true
//	},
	{
		path: '',
		name: '',
		component: Layout,
		leaf: true,
		children: [
			{
                path: '/',
                name: 'home',
                component: (resolve) => require(['@/views/Home'], resolve),
                meta: {title: '首页', icon: 'home'}
			}
		]
	}
];

export const createBasicRouter = () => new VueRouter({
    //mode: 'history', // 去掉url中的#
    scrollBehavior: () => ({y: 0}),
    routes: basicRoutes
});
export const router = createBasicRouter();

//修改原型对象中的push方法
const originalPush = VueRouter.prototype.push;
VueRouter.prototype.push = function push(location) {
    return originalPush.call(this, location).catch(err => err);
}
