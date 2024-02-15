import {createRouter, createWebHistory, createWebHashHistory} from 'vue-router';
import {shallowRef} from 'vue';
import Layout from '@/views/Layout';
//import Layout from '@/layout';


export const basicRoutes = [
	{
        path: '/login',
        name: '',
        component: () => import('@/views/Login'),
        hidden: true
	},
	{
        path: '/404',
        name: '',
        component: () => import('@/views/404'),
        hidden: true
	},
//	{
//		path: '/',
//		redirect: '/home',
//		hidden: true
//	},
	{
		path: '/shit',//没意义，凑数的
		name: '',
		component: Layout,
		leaf: true,
		children: [
			{
                path: '/',
                name: 'home',
                component: () => import('@/views/Home'),
                meta: {title: '首页', icon: 'home'}
			}
		]
	}
];

export const createBasicRouter = () => createRouter({
    //history: createWebHistory(),
    history: createWebHashHistory(),
    routes: basicRoutes,
    scrollBehavior(to, from, savedPosition) {
        if (savedPosition) {
            return savedPosition;
        } else {
            return {top: 0};
        }
    }
});
export const router = createBasicRouter();


