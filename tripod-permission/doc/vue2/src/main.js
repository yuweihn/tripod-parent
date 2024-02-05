import babelpolyfill from 'babel-polyfill';
import Vue from 'vue';
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import App from './App.vue';
import store from './vuex/store';
import Vuex from 'vuex';
//import NProgress from 'nprogress';
//import 'nprogress/nprogress.css';
import {router} from './basic.routes';
//import Mock from './mock';
import '@/components/css/index.scss';
//Mock.bootstrap();
import 'font-awesome/css/font-awesome.min.css';
import axios from 'axios';
import md5 from 'js-md5';
import global from './components/js/global';
import date from './components/js/date';
import util from './components/js/util';
import cache from './components/js/cache';
import session from './components/js/session';
import './components/js/dialog.drag';
import './components/js/has.permission';
import './components/svg/index.js';
import settings from './settings';
import './permission';
import './interceptor';
import fileDownload from "js-file-download";


//Vue.use(ElementUI);
Vue.use(ElementUI, {size: 'small', zIndex: 1005});
Vue.use(Vuex);
Vue.prototype.settings = settings;
Vue.prototype.$axios = axios;
Vue.prototype.$global = global;
Vue.prototype.$cache = cache;
Vue.prototype.$session = session;
Vue.prototype.$md5 = md5;
Vue.prototype.$date = date;
Vue.prototype.$util = util;
Vue.prototype.resetForm = util.resetForm;
Vue.prototype.$fileDownload = fileDownload;
Vue.prototype.removeDynamicLoaded = () => {
    store.dispatch('RemoveDynamicMenuLoaded');
    store.dispatch('RemoveDynamicButtonLoaded');
}

axios.defaults.withCredentials = true;


new Vue({
	//el: '#app',
	//template: '<App/>',
	router,
	store,
	//components: { App }
	render: h => h(App)
}).$mount('#app');

