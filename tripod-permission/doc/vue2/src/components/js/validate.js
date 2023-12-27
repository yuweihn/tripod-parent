

/* 是否手机号码或者固话*/
export function validatePhone(rule, value, callback) {
	const reg = /^((0\d{2,3}-\d{7,8})|(1[3456789]\d{9}))$/;
	if (!value) {
		callback();
	} else {
		if ((!reg.test(value)) && value != '') {
			callback(new Error('请输入正确的电话号码'));
		} else {
			callback();
		}
	}
}

/* 是否合法IP地址 */
export function validateIP(rule, value, callback) {
	if (!value) {
		callback();
	} else {
		const reg = /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/;
		if ((!reg.test(value)) && value != '') {
			callback(new Error('请输入正确的IP地址'));
		} else {
			callback();
		}
	}
}

/* 是否身份证号码 */
export function validateIdNo(rule, value, callback) {
	const reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
	if (!value) {
		callback();
	} else {
		if ((!reg.test(value)) && value != '') {
			callback(new Error('请输入正确的身份证号码'));
		} else {
			callback();
		}
	}
}
/* 是否邮箱 */
export function validateEMail(rule, value, callback) {
  const reg = /^([a-zA-Z0-9]+[-_\.]?)+@([a-zA-Z0-9]+[-_\.]?)+\.[a-z]+$/;
	if (!value) {
		callback();
	} else {
		if (!reg.test(value)) {
			callback(new Error('请输入正确的邮箱地址'));
		} else {
			callback();
		}
	}
}
/* 合法uri */
export function validateURL(textval) {
	const urlregex = /^(https?|ftp):\/\/([a-zA-Z0-9.-]+(:[a-zA-Z0-9.&%$-]+)*@)*((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]?)(\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])){3}|([a-zA-Z0-9-]+\.)*[a-zA-Z0-9-]+\.(com|edu|gov|int|mil|net|org|biz|arpa|info|name|pro|aero|coop|museum|[a-zA-Z]{2}))(:[0-9]+)*(\/($|[a-zA-Z0-9.,?'\\+&%$#=~_-]+))*$/;
	return urlregex.test(textval);
}

// 验证是否整数
export function isInteger(rule, value, callback) {
	if (!value && value !== 0) {
		return callback(new Error('输入不可以为空'));
	}
	setTimeout(() => {
		var regPos = /^\d+$/; // 非负整数
		var regNeg = /^\-[1-9][0-9]*$/; // 负整数
		var res = regPos.test(value) || regNeg.test(value);
		if (!res) {
			callback(new Error('请输入整数'));
		} else {
			callback();
		}
	}, 0);
}

// 验证是否整数，非必填
export function isIntegerNotMust(rule, value, callback) {
	if (!value && value !== 0) {
		callback();
	}
	setTimeout(() => {
		var regPos = /^\d+$/; // 非负整数
		var regNeg = /^\-[1-9][0-9]*$/; // 负整数
		var res = regPos.test(value) || regNeg.test(value);
		if (!res) {
			callback(new Error('请输入整数'));
		} else {
			callback();
		}
	}, 0);
}

// 验证是否数字
export function isNumber(rule, value, callback) {
	if (!value && value !== 0) {
		return callback(new Error('输入不可以为空'));
	}
	setTimeout(() => {
		var regPos = /^\d+(\.\d+)?$/; //非负浮点数
		var regNeg = /^(-(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1-9][0-9]*)))$/; //负浮点数
		var res = regPos.test(value) || regNeg.test(value);
		if (!res) {
			callback(new Error('请输入数字'));
		} else {
			callback();
		}
	}, 0);
}

// 验证是否数字，非必填
export function isNumberNotMust(rule, value, callback) {
	if (!value && value !== 0) {
		callback();
	}
	setTimeout(() => {
		var regPos = /^\d+(\.\d+)?$/; //非负浮点数
		var regNeg = /^(-(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1-9][0-9]*)))$/; //负浮点数
		var res = regPos.test(value) || regNeg.test(value);
		if (!res) {
			callback(new Error('请输入数字'));
		} else {
			callback();
		}
	}, 0);
}

// 只能是字母或数字，非必填
export function isWordOrNumNotMust(rule, value, callback) {
	if (!value && value !== 0) {
		callback();
	}
	setTimeout(() => {
		const reg = /^[a-zA-Z0-9-_]+$/;
		const rsCheck = reg.test(value);
		if (!rsCheck) {
			callback(new Error('请输入字母、数字、下划线或横线'));
		} else {
			callback();
		}
	}, 1000);
}

