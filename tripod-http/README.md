# tripod-http

For example:
------------------------------------------------------------------------------------------------------------------
	private static void textToAudio() {
		long timestamp = new Date().getTime();
		String sign = "**************";
		HttpResponse<byte[]> response = HttpFormRequest.create().url("https://www.××××.com/×××/×××")
																	.method(HttpMethod.GET)
																	.addField("text", text)
																	.addField("lan", lan)
																	.addField("spd", spd)
																	.addField("pit", pit)
																	.addField("vol", vol)
																	.addField("per", per)
																	.addField("key", KEY)
																	.addField("timestamp", "" + timestamp)
																	.addField("sign", sign)
																	.responseType(byte[].class)
																	.execute();
		if(!response.isSuccess()) {
			System.out.println(response.getErrorMessage());
		} else {
			byte[] body = response.getBody();
			System.out.println(body.length);
		}
	}
------------------------------------------------------------------------------------------------------------------
