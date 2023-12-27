import axios from 'axios';
import md5 from 'js-md5';
import global from './components/js/global';
import session from './components/js/session';


axios.interceptors.request.use(
    function(config) {
        var key = global.signKey;
        var secret = global.signSecret;
        var timestamp = new Date().getTime();
        var sign = md5("key=" + key + "&secret=" + secret + "&timestamp=" + timestamp);

        var token = session.getToken();
        if (token != null && token != "undefined") {
            config.headers.common[global.tokenHeaderName] = token;
        }
        config.headers.common['key'] = key;
        config.headers.common['timestamp'] = timestamp;
        config.headers.common['sign'] = sign;
        return config;
    },
    function(error) {
        return Promise.reject(error);
    }
);

axios.interceptors.response.use(
	response => {
		if (response.data.code == '1001') {
			session.removeUser();
			session.removeToken();
			location.href = './';
		}
		return response;
	},
	error => {
		return Promise.reject(error);
	}
);

