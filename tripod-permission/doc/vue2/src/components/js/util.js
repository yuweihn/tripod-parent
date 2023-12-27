import md5 from 'js-md5';
import global from './global';

export default {
	// 表单重置
	resetForm: function(formName) {
		if (this.$refs[formName]) {
			this.$refs[formName].resetFields();
		}
	},

	textToAudio: function(content) {
		var options = {
			"speech": true, //通过点击链接播报，还是直接播报
			"lang": "zh", //语言
			"speed": 5, //语速
			"sWidth": 16, //链接按钮的宽度
			"sHeight": 13, //链接按钮的高度
			"bg": "", //链接按钮的背景图片
			"content": content//直接播报内容
		};
		var aurl = global.baseUrl + '/audio/tta?text=' + options.content + '&lan=' + options.lang + '&spd=' + options.speed;
		var audio = new Audio(aurl);
		audio.play();
		return audio;
	},

	textToAudio2: function(content) {
		var key = "fuckyou";
		var secret = "123456789";
		var options = {
			"speech": true, //通过点击链接播报，还是直接播报
			"lang": "zh", //语言
			"speed": 5, //语速
			"sWidth": 16, //链接按钮的宽度
			"sHeight": 13, //链接按钮的高度
			"bg": "", //链接按钮的背景图片
			"content": content//直接播报内容
		};
		var timestamp = new Date().getTime();
		var sign = md5("key=" + key + "&secret=" + secret + "&timestamp=" + timestamp).toLowerCase();
		var aurl = 'https://www.yuweix.com/audio/tta?text=' + options.content + '&lan=' + options.lang + '&spd=' + options.speed + '&key=' + key + '&timestamp=' + timestamp + '&sign=' + sign;
		var audio = new Audio(aurl);
		audio.play();
		return audio;
	},

	add: function(arg1, arg2) {
		if (!arg1 && arg1 !== 0) {
			arg1 = 0;
		}
		if (!arg2 && arg2 !== 0) {
			arg2 = 0;
		}
		var r1, r2, m;
		try {
			r1 = arg1.toString().split(".")[1].length;
		} catch(e) {
			r1 = 0;
		}
		try {
			r2 = arg2.toString().split(".")[1].length;
		} catch(e) {
			r2 = 0;
		}
		m = Math.pow(10, Math.max(r1, r2));
		return (arg1 * m + arg2 * m) / m;
	},
	sub: function(arg1, arg2) {
		if (!arg1 && arg1 !== 0) {
			arg1 = 0;
		}
		if (!arg2 && arg2 !== 0) {
			arg2 = 0;
		}
		var r1, r2, m, n;
		try {
			r1 = arg1.toString().split(".")[1].length;
		} catch(e) {
			r1 = 0;
		}
		try {
			r2 = arg2.toString().split(".")[1].length;
		} catch(e) {
			r2 = 0;
		}
		m = Math.pow(10, Math.max(r1, r2));
		//动态控制精度长度
		n = (r1 >= r2) ? r1 : r2;
		return ((arg1 * m - arg2 * m) / m).toFixed(n);
	},
	mul: function(arg1, arg2) {
		if (!arg1 && arg1 !== 0) {
			arg1 = 0;
		}
		if (!arg2 && arg2 !== 0) {
			arg2 = 0;
		}
		var m = 0, s1 = arg1.toString(), s2 = arg2.toString();
		try {
			m += s1.split(".")[1].length;
		} catch(e) {}
		try {
			 m += s2.split(".")[1].length;
		} catch(e) {}
		return Number(s1.replace(".", "")) * Number(s2.replace(".", "")) / Math.pow(10, m);
	},
	div: function(arg1, arg2) {
		if (!arg1 && arg1 !== 0) {
			arg1 = 0;
		}
		if (!arg2 && arg2 !== 0) {
			arg2 = 0;
		}
		var t1 = 0, t2 = 0, r1, r2;
		try {
			t1 = arg1.toString().split(".")[1].length;
		} catch(e) {}
		try {
			t2 = arg2.toString().split(".")[1].length;
		} catch(e) {}
		r1 = Number(arg1.toString().replace(".", ""));
		r2 = Number(arg2.toString().replace(".", ""));
		return (r1 / r2) * Math.pow(10, t2 - t1);
	},

  slice: function(val, len) {
    if (!len) {
      len = 10;
    }
    if (!val) {
      return '';
    }
    if (val.length > len) {
      return val.slice(0, len) + '...';
    }
    return val;
  }
}
