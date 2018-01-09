package com.assist4j.http.request;


import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import com.assist4j.http.HttpConstant;
import com.assist4j.http.response.HttpResponse;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


/**
 * 上传文件
 * @author yuwei
 */
public class HttpFileRequest extends AbstractHttpRequest<HttpFileRequest> {
	private List<FileField> fileFieldList;
	private List<FormField> formFieldList;


	protected HttpFileRequest() {
		super();
		fileFieldList = new ArrayList<FileField>();
		formFieldList = new ArrayList<FormField>();
	}
	public static HttpFileRequest create() {
		return new HttpFileRequest();
	}


	public HttpFileRequest addFile(String fieldName, byte[] content, String fileName) {
		FileWrapper fw = new FileWrapper();
		fw.setContent(content);
		fw.setFileName(fileName);

		for(FileField ff: fileFieldList) {
			if(fieldName.equals(ff.getFieldName())) {
				ff.addFile(fw);
				return this;
			}
		}

		FileField ff = new FileField();
		ff.setFieldName(fieldName);
		ff.addFile(fw);
		fileFieldList.add(ff);
		return this;
	}

	public HttpFileRequest addFormField(String key, String value) {
		if(StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
			return this;
		}

		FormField ff = new FormField();
		ff.setKey(key);
		ff.setValue(value);
		formFieldList.add(ff);
		return this;
	}

	@Override
	public <B>HttpResponse<B> execute() {
		String charset = getCharset();
		charset = charset != null ? charset : HttpConstant.ENCODING_UTF_8;
		
		ContentType textContentType = ContentType.create(ContentType.TEXT_PLAIN.getMimeType(), charset);
		ContentType multipartContentType = ContentType.create(ContentType.MULTIPART_FORM_DATA.getMimeType(), charset);

		MultipartEntityBuilder builder = MultipartEntityBuilder.create()
															.setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
															.setCharset(Charset.forName(charset));
		if(!CollectionUtils.isEmpty(fileFieldList)) {
			for(FileField fileField: fileFieldList) {
				List<FileWrapper> fileList = fileField.getFileList();
				if(!CollectionUtils.isEmpty(fileList)) {
					for(FileWrapper fw: fileList) {
						builder.addBinaryBody(fileField.getFieldName(), fw.getContent(), multipartContentType, fw.getFileName());
					}
				}
			}
		}

		if(!CollectionUtils.isEmpty(formFieldList)) {
			for (FormField ff: formFieldList) {
				String k = ff.getKey();
				String v = ff.getValue();
				if(StringUtils.isEmpty(k) || StringUtils.isEmpty(v)) {
					continue;
				}
				builder.addTextBody(k, v, textContentType);
			}
		}

		HttpEntity entity = builder.build();
		HttpPost post = new HttpPost(this.getUrl());
		post.addHeader("Content-Disposition", "attachment");
		post.setEntity(entity);
		this.setHttpUriRequest(post);
		return execute0();
	}
}
