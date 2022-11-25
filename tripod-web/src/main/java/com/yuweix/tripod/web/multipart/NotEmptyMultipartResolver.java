package com.yuweix.tripod.web.multipart;


import org.springframework.web.multipart.support.StandardServletMultipartResolver;


/**
 * @author yuwei
 */
public class NotEmptyMultipartResolver extends StandardServletMultipartResolver {
//	public NotEmptyMultipartResolver() {
//		super();
//	}
//
//	/**
//	 * 刨去size为0的文件
//	 * 注意：fileItem.isFormField()为true的数据是普通的表单类型，不是文件类型。
//	 */
//	@Override
//	protected MultipartParsingResult parseFileItems(List<FileItem> fileItems, String encoding) {
//		if (fileItems.size() <= 0) {
//			/**
//			 * do nothing.
//			 */
//		} else {
//			List<FileItem> tempList = new ArrayList<>();
//			for (FileItem fileItem: fileItems) {
//				if (fileItem == null || fileItem.isFormField() || fileItem.getSize() > 0) {
//					tempList.add(fileItem);
//				}
//			}
//			fileItems.clear();
//			fileItems.addAll(tempList);
//		}
//		return super.parseFileItems(fileItems, encoding);
//	}
}
