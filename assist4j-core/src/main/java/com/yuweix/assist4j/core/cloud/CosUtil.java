package com.yuweix.assist4j.core.cloud;


import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import com.yuweix.assist4j.core.io.StreamUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * 腾讯云COS文件工具类
 * @author yuwei
 */
public class CosUtil {
	private static final Logger log = LoggerFactory.getLogger(CosUtil.class);
	private String endpoint;
	private String bucketName;

	private COSClient cosClient = null;


	public CosUtil(String secretId, String secretKey, String region, String bucketName) {
		this(secretId, secretKey, region, null, bucketName);
	}
	public CosUtil(String secretId, String secretKey, String region, String protocol, String bucketName) {
		if (protocol == null || "".equals(protocol)) {
			protocol = "http";
		}
		this.endpoint = protocol + "://" + bucketName + ".cos." + region + ".myqcloud.com";
		this.bucketName = bucketName;

		COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
		ClientConfig clientConfig = new ClientConfig(new Region(region));
		cosClient = new COSClient(cred, clientConfig);
		try {
			if (!cosClient.doesBucketExist(bucketName)) {
				CreateBucketRequest bucketRequest = new CreateBucketRequest(bucketName);
				bucketRequest.setCannedAcl(CannedAccessControlList.PublicRead);
				cosClient.createBucket(bucketRequest);
			}
		} catch (Exception e) {
			log.warn(e.getMessage());
		}
	}

	/**
	 * 删除一个Bucket和其中的文件
	 */
	public void deleteBucket() {
		if (!cosClient.doesBucketExist(bucketName)) {
			return;
		}

		synchronized (this) {
			List<String> keyList = queryBucketKeyList();
			if (keyList != null && keyList.size() > 0) {
				for (String key: keyList) {
					//先删除bucket下的文件
					cosClient.deleteObject(bucketName, key);
				}
			}
			cosClient.deleteBucket(bucketName);
		}
	}

	/**
	 * 查询Bucket下所有的key
	 */
	public List<String> queryBucketKeyList() {
		List<String> list = new ArrayList<String>();
		if (!cosClient.doesBucketExist(bucketName)) {
			return list;
		}

		ObjectListing objListing = cosClient.listObjects(bucketName);
		List<COSObjectSummary> summaryList = objListing.getObjectSummaries();
		if (summaryList == null || summaryList.size() <= 0) {
			return list;
		}

		for (COSObjectSummary summary: summaryList) {
			list.add(summary.getKey());
		}
		return list;
	}

	/**
	 * 上传文件至COS
	 * @param content
	 * @param key                 如：prd/readme.txt
	 */
	public String uploadFile(byte[] content, String key) {
		log.info("COS upload file: key[{}]", key);
		ByteArrayInputStream bis = new ByteArrayInputStream(content);
		ObjectMetadata objMeta = new ObjectMetadata();
		objMeta.setContentLength(bis.available());
		cosClient.putObject(bucketName, key, bis, objMeta);
		String url = endpoint + "/" + key;
		log.info("URL: {}", url);
		try {
			bis.close();
		} catch (IOException e) {
			log.error("", e);
		}
		return url;
	}

	/**
	 * 从COS下载文件
	 * @param key
	 */
	public byte[] downloadFile(String key) {
		COSObject cosObj = cosClient.getObject(new GetObjectRequest(bucketName, key));
		InputStream in;
		if (cosObj == null || (in = cosObj.getObjectContent()) == null) {
			return null;
		}
		byte[] bytes = StreamUtil.read(in);
		try {
			cosObj.close();
		} catch (IOException e) {
			log.warn(e.getMessage());
		}
		return bytes;
	}
	public void downloadFile(String key, OutputStream out) {
		COSObject cosObj = cosClient.getObject(new GetObjectRequest(bucketName, key));
		InputStream in;
		if (cosObj == null || (in = cosObj.getObjectContent()) == null) {
			return;
		}
		StreamUtil.write(in, out);
		try {
			cosObj.close();
		} catch (IOException e) {
			log.warn(e.getMessage());
		}
	}

	/**
	 * 从COS删除文件
	 * @param key
	 */
	public void deleteFile(String key) {
		cosClient.deleteObject(bucketName, key);
	}
}
