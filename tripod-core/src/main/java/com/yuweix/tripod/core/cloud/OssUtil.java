package com.yuweix.tripod.core.cloud;


import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.model.*;
import com.yuweix.tripod.core.io.StreamUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 阿里云OSS文件工具类
 * @author yuwei
 */
public class OssUtil {
	private static final Logger log = LoggerFactory.getLogger(OssUtil.class);
	private String endpoint;
	private String accessKey;
	private String accessSecret;
	private String bucketName;

	private volatile OSSClient ossClient = null;
	private final ReentrantLock ossClientLock = new ReentrantLock();


	public OssUtil(String endpoint, String accessKey, String accessSecret, String bucketName) {
		this.endpoint = endpoint;
		this.accessKey = accessKey;
		this.accessSecret = accessSecret;
		this.bucketName = bucketName;
	}

	private OSSClient getOssClient() {
		if (ossClient == null) {
			ossClientLock.lock();
			try {
				if (ossClient == null) {
					ossClient = new OSSClient(this.endpoint
							, new DefaultCredentialProvider(accessKey, accessSecret), null);

					if (!ossClient.doesBucketExist(bucketName)) {
						CreateBucketRequest bucketRequest = new CreateBucketRequest(bucketName);
						bucketRequest.setCannedACL(CannedAccessControlList.PublicRead);
						ossClient.createBucket(bucketRequest);
					}
				}
			} catch (Exception e) {
				log.error("Error on getOssClient: {}", e.getMessage());
			} finally {
				ossClientLock.unlock();
			}
		}
		return ossClient;
	}

	/**
	 * 删除一个Bucket和其中的文件
	 */
	public void deleteBucket() {
		if (!getOssClient().doesBucketExist(bucketName)) {
			return;
		}

		synchronized (this) {
			List<String> keyList = queryBucketKeyList();
			if (keyList != null && keyList.size() > 0) {
				for (String key: keyList) {
					//先删除bucket下的文件
					getOssClient().deleteObject(bucketName, key);
				}
			}
			getOssClient().deleteBucket(bucketName);
		}
	}

	/**
	 * 查询Bucket下所有的key
	 */
	public List<String> queryBucketKeyList() {
		List<String> list = new ArrayList<String>();
		if (!getOssClient().doesBucketExist(bucketName)) {
			return list;
		}

		ObjectListing objListing = getOssClient().listObjects(bucketName);
		List<OSSObjectSummary> summaryList = objListing.getObjectSummaries();
		if (summaryList == null || summaryList.size() <= 0) {
			return list;
		}

		for (OSSObjectSummary summary: summaryList) {
			list.add(summary.getKey());
		}
		return list;
	}

	/**
	 * 上传文件至OSS
	 * @param content
	 * @param key                 如：prd/readme.txt
	 */
	public String uploadFile(byte[] content, String key) {
		log.info("OSS upload file: key[{}]", key);
		String protocol = getProtocol(endpoint);
		ByteArrayInputStream bis = new ByteArrayInputStream(content);

		ObjectMetadata objMeta = new ObjectMetadata();
		objMeta.setContentLength(bis.available());
		getOssClient().putObject(bucketName, key, bis, objMeta);
		String url = protocol + bucketName + "." + endpoint.substring(protocol.length()) + "/" + key;
		log.info("URL: {}", url);
		try {
			bis.close();
		} catch (IOException e) {
			log.error("", e);
		}
		return url;
	}
	private String getProtocol(String endpoint) {
		if (endpoint != null && endpoint.startsWith("https://")) {
			return "https://";
		} else {
			return "http://";
		}
	}

	/**
	 * 从OSS下载文件
	 * @param key
	 */
	public byte[] downloadFile(String key) {
		OSSObject ossObj = getOssClient().getObject(new GetObjectRequest(bucketName, key));
		InputStream in;
		if (ossObj == null || (in = ossObj.getObjectContent()) == null) {
			return null;
		}
		byte[] bytes = StreamUtil.read(in);
		try {
			ossObj.close();
		} catch (IOException e) {
			log.warn(e.getMessage());
		}
		return bytes;
	}
	public void downloadFile(String key, OutputStream out) {
		OSSObject ossObj = getOssClient().getObject(new GetObjectRequest(bucketName, key));
		InputStream in;
		if (ossObj == null || (in = ossObj.getObjectContent()) == null) {
			return;
		}
		StreamUtil.write(in, out);
		try {
			ossObj.close();
		} catch (IOException e) {
			log.warn(e.getMessage());
		}
	}

	/**
	 * 从OSS删除文件
	 * @param key
	 */
	public void deleteFile(String key) {
		getOssClient().deleteObject(bucketName, key);
	}
}
