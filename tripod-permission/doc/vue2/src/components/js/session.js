
var userKey = "x-ai-user";
var tokenKey = "x-ai-token";

export default {
	putUser: function(user) {
		if (user == null) {
			return;
		}
		try {
			localStorage.setItem(userKey, JSON.stringify(user));
		} catch (e) {
			console.log(e);
		}
	},
	getUser: function() {
		var str = localStorage.getItem(userKey);
		if (str == null) {
			return null;
		}
		try {
			return JSON.parse(str);
		} catch (e) {
			console.log(e);
			return null;
		}
	},
	removeUser: function() {
		localStorage.removeItem(userKey);
	},


	putToken: function(token) {
		if (token != null) {
			localStorage.setItem(tokenKey, token);
		}
	},
	getToken: function() {
		var token = localStorage.getItem(tokenKey);
		return token;
	},
	removeToken: function() {
		localStorage.removeItem(tokenKey);
	}
}
