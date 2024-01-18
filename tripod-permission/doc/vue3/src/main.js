import { createApp } from 'vue';

import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import locale from 'element-plus/dist/locale/zh-cn.mjs';

import '@/assets/styles/index.scss';
import 'font-awesome/css/font-awesome.min.css';

import '@/assets/js/browser.patch';

import App from './App';


// svg图标
import 'virtual:svg-icons-register';
import SvgIcon from '@/components/SvgIcon';
import elementIcons from '@/components/SvgIcon/svgicon';

import settings from './settings';
import request from '@/assets/js/request';
import date from '@/assets/js/date';
import util from '@/assets/js/util';
import md5 from 'js-md5';
import modal from '@/assets/js/modal';
import cache from '@/assets/js/cache';
import session from '@/assets/js/session';
import errorCode from '@/assets/js/errorCode';
import store from './store'
import {router} from './basic.routes';
import directive from './directive';
import fileDownload from "js-file-download";
import dynamicRouteStore from '@/dynamic.routes';
import dynamicButtonStore from '@/dynamic.buttons';

import './permission';

// 富文本组件
import Editor from "@/components/Editor";
import FileUpload from '@/components/FileUpload';
import SingleFileUpload from '@/components/SingleFileUpload';

const app = createApp(App);

// 全局方法挂载
app.config.globalProperties.settings = settings;
app.config.globalProperties.request = request;
app.config.globalProperties.$util = util;
app.config.globalProperties.$date = date;
app.config.globalProperties.$md5 = md5;
app.config.globalProperties.$modal = modal;
app.config.globalProperties.resetForm = util.resetForm;
app.config.globalProperties.cache = cache;
app.config.globalProperties.session = session;
app.config.globalProperties.errorCode = errorCode;
app.config.globalProperties.fileDownload = fileDownload;
app.config.globalProperties.removeDynamicLoaded = () => {
    dynamicRouteStore().resetDynamicMenuLoaded();
    dynamicButtonStore().resetDynamicButtonLoaded();
}

// 全局组件挂载
app.component('rt-editor', Editor);
app.component('file-upload', SingleFileUpload);
app.component('files-upload', FileUpload);

app.use(router);
app.use(store);
app.use(elementIcons);
app.component('svg-icon', SvgIcon);

directive(app);

app.use(ElementPlus, {locale: locale});
app.mount('#app');
