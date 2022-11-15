package com.yuweix.tripod.core.io;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;


/**
 * 流处理工具
 * @author yuwei
 */
public abstract class StreamUtil {
	private static final Logger log = LoggerFactory.getLogger(StreamUtil.class);

	public static byte[] read(InputStream in) {
		ByteArrayOutputStream out = null;
		try {
			out = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len;
			while ((len = in.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
			return out.toByteArray();
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void write(InputStream in, OutputStream out) {
		BufferedInputStream bis = null;
		try {
			bis = new BufferedInputStream(in);
			byte[] buffer = new byte[1024];
			int i = bis.read(buffer);
			while (i != -1) {
				out.write(buffer, 0, i);
				i = bis.read(buffer);
			}
			out.flush();
		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException ignored) {
				}
			}
		}
	}
}
