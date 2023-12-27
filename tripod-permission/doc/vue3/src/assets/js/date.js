

export default {
	formatDate(date, fmt) {
		if (date == null) {
			return "";
		}
		try {
			date = new Date(date);
		} catch(e) {
			return "";
		}
		if (/(y+)/.test(fmt)) {
			fmt = fmt.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length));
		}
		var o = {
				'M+': date.getMonth() + 1,
				'd+': date.getDate(),
				'H+': date.getHours(),
				'm+': date.getMinutes(),
				's+': date.getSeconds()
		}
		for (var k in o) {
			if (new RegExp("("+ k +")").test(fmt)) {
				var str = o[k] + '';
				fmt = fmt.replace(RegExp.$1, RegExp.$1.length === 1 ? str : padLeftZero(str));
			}
		}
		return fmt;
	}
}

function padLeftZero (str) {
	return ('00' + str).substr(str.length);
}
