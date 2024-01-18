import axios from 'axios';
import md5 from 'js-md5';
import errorCode from './errorCode';
import util from './util';
import modal from './modal';
import session from './session';
import cache from './cache';


//axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8';
axios.defaults.headers['Content-Type'] = 'application/x-www-form-urlencoded;charset=utf-8';

// 创建axios实例
const service = axios.create({
    // axios中请求配置有baseURL选项，表示请求URL公共部分
    baseURL: import.meta.env.VITE_APP_BASE_URL,
    // 超时
    timeout: 10000
})

const sign = {
    key: import.meta.env.VITE_APP_SIGN_KEY,
    secret: import.meta.env.VITE_APP_SIGN_SECRET,
    tokenName: import.meta.env.VITE_APP_TOKEN_NAME
};

// request拦截器
service.interceptors.request.use(config => {
    // get请求映射params参数
//    if (config.method === 'get' && config.params) {
//        let url = config.url + '?' + util.tansParams(config.params);
//        url = url.slice(0, -1);
//        config.params = {};
//        config.url = url;
//    }

    //将header中的responseType提取出来单独提交
    var rtype = (config.headers || {}).responseType;
    delete (config.headers || {}).responseType;
    (rtype != null) && (config.responseType = rtype);

    //防刷
    const doNotCheckRepeatSubmit = (config.headers || {}).rejectRepeat === false; // 是否需要防止数据重复提交
    if (!doNotCheckRepeatSubmit && (config.method === 'post' || config.method === 'put' || config.method === 'delete')) {
        const requestObj = {
            url: config.url,
            data: typeof config.data === 'object' ? JSON.stringify(config.data) : config.data,
            time: new Date().getTime()
        }
        const preRequestObj = cache.session.getJSON('pre_request');
        if (preRequestObj === undefined || preRequestObj === null || preRequestObj === '') {
            cache.session.setJSON('pre_request', requestObj);
        } else {
            const s_url = preRequestObj.url;                // 请求地址
            const s_data = preRequestObj.data;              // 请求数据
            const s_time = preRequestObj.time;              // 请求时间
            const interval = 2000;                       // 间隔时间(ms)，小于此时间视为重复提交
            if (s_data === requestObj.data && requestObj.time - s_time < interval && s_url === requestObj.url) {
                const message = '数据正在处理，请勿重复提交';
                return Promise.reject(new Error(message));
            } else {
                cache.session.setJSON('pre_request', requestObj);
            }
        }
    }

    //加签
    const timestamp = new Date().getTime();
    const signVal = md5("key=" + sign.key + "&secret=" + sign.secret + "&timestamp=" + timestamp);
    const tokenV = session.getToken();
    if (tokenV != null && tokenV != "undefined") {
        config.headers[sign.tokenName] = tokenV;
    }
    config.headers['key'] = sign.key;
    config.headers['timestamp'] = timestamp;
    config.headers['sign'] = signVal;
    return config;
}, error => {
    console.log('Error: ' + error);
    Promise.reject(error);
});

// 响应拦截器
service.interceptors.response.use(res => {
    //非json数据直接返回
    if (res.headers["content-type"].indexOf("application/json") !== 0) {
        return Promise.resolve(res);
    }

    //响应状态码
    const responseObj = res.data || errorCode.unknown;
    if (responseObj.code === errorCode.unLogin.code) {
        session.removeUser();
        session.removeToken();
        modal.msgError(responseObj.msg);
        location.href = './';
        return Promise.reject(new Error(responseObj.msg));
    } else if (responseObj.code !== errorCode.success.code) {
        modal.msgError(responseObj.msg);
        return Promise.reject(new Error(responseObj.msg));
    } else {
        return Promise.resolve(res);
    }
},
error => {
    console.log('Error: ' + error);
    let { message } = error;
    modal.msgError(message);
    return Promise.reject(error);
});

export default {
    get: function(url, params, headers) {
        return service({
            url: url,
            method: 'get',
            headers: headers ? headers : {},
            params: params ? params : null
        });
    },
    post: function(url, params, headers) {
        return service({
            url: url,
            method: 'post',
            headers: headers ? headers : {},
            data: params ? params : null
        });
    },
    put: function(url, params, headers) {
        return service({
            url: url,
            method: 'put',
            headers: headers ? headers : {},
            data: params ? params : null
        });
    },
    delete: function(url, params, headers) {
        return service({
            url: url,
            method: 'delete',
            headers: headers ? headers : {},
            params: params ? params : null
        });
    }
}

