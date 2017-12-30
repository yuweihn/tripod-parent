package com.assist4j.http.request;


import java.util.ArrayList;
import java.util.List;


/**
 * @author yuwei
 */
public class FileField {
	/**
	 * 服务器端接收文件的参数名
	 */
	private String fieldName;
	private List<FileWrapper> fileList;


	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public List<FileWrapper> getFileList() {
		return fileList;
	}

	public FileField addFile(FileWrapper fw) {
		if(fileList == null) {
			fileList = new ArrayList<FileWrapper>();
		}
		fileList.add(fw);
		return this;
	}
}
