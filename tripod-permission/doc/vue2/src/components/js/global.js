const env = process.env.ENV;

var baseUrl = null;
var signKey = null;
var signSecret = null;
var tokenHeaderName = "x-token";

if (env == "dev") {
	baseUrl = 'https://convert-api-test.agilenaas.net';
	signKey = 'ags';
	signSecret = 'test';
} else if (env == "qa") {
    baseUrl = 'https://convert-api-test.agilenaas.net';
	signKey = 'ags';
	signSecret = 'test';
} else if (env == "prd") {
    baseUrl = 'https://convert-api-test.agilenaas.net';
    signKey = 'ags';
    signSecret = 'test';
}

export default {
	baseUrl,
	signKey,
	signSecret,
	tokenHeaderName
}

