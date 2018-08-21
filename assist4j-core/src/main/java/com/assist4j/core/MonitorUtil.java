package com.assist4j.core;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;


/**
 * @author yuwei
 */
public abstract class MonitorUtil {
	private static final long CPU_SLEEP_TIME = 1000L;
	private static final int FAULT_LENGTH = 10;
	
	

	/**
	 * 获取cpu使用率
	 * @return
	 */
	public static double getCpuUsage() {
		String osName = System.getProperty("os.name");
		if (osName.toLowerCase().contains("windows") || osName.toLowerCase().contains("win")) {
			return getCpuRateForWindows();
		} else {
			return getCpuRateForLinux();
		}
	}
	
	/**
	 * 获取windows环境下cpu的使用率
	 * @return
	 */
	private static double getCpuRateForWindows() {
		try {
			String procCmd = System.getenv("windir") 
					+ "//system32//wbem//wmic.exe process get Caption,CommandLine," 
					+ "KernelModeTime,ReadOperationCount,ThreadCount,UserModeTime,WriteOperationCount";
			// 取进程信息  
			long[] c0 = readCpuForWindows(Runtime.getRuntime().exec(procCmd));
			Thread.sleep(CPU_SLEEP_TIME);
			long[] c1 = readCpuForWindows(Runtime.getRuntime().exec(procCmd));
			if (c0 != null && c1 != null) {
				long idletime = c1[0] - c0[0];
				long busytime = c1[1] - c0[1];
				double cpuRate = MathUtil.div(busytime, busytime + idletime);
				if (cpuRate > 1) {
					cpuRate = 1;
				} else if (cpuRate < 0) {
					cpuRate = 0;
				}
				return cpuRate;
			} else {
				return 0;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	/**
	 * 获取linux环境下cpu的使用率
	 * @return
	 */
	private static double getCpuRateForLinux() {
		try {
			Map<String, String> map1 = readCpuForLinux();
			Thread.sleep(CPU_SLEEP_TIME);
			Map<String, String> map2 = readCpuForLinux();

			long user1 = Long.parseLong(map1.get("user"));
			long nice1 = Long.parseLong(map1.get("nice"));
			long system1 = Long.parseLong(map1.get("system"));
			long idle1 = Long.parseLong(map1.get("idle"));

			long user2 = Long.parseLong(map2.get("user"));
			long nice2 = Long.parseLong(map2.get("nice"));
			long system2 = Long.parseLong(map2.get("system"));
			long idle2 = Long.parseLong(map2.get("idle"));

			long total1 = user1 + system1 + nice1;
			long total2 = user2 + system2 + nice2;
			long total = total2 - total1;

			long totalIdle1 = user1 + nice1 + system1 + idle1;
			long totalIdle2 = user2 + nice2 + system2 + idle2;
			long totalIdle = totalIdle2 - totalIdle1;

			double cpuRate = MathUtil.div(total, totalIdle);
			if (cpuRate > 1) {
				cpuRate = 1;
			} else if (cpuRate < 0) {
				cpuRate = 0;
			}
			return cpuRate;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	private static Map<String, String> readCpuForLinux() {
		InputStreamReader inputs = null;
		BufferedReader buffer = null;
		Map<String, String> map = new HashMap<String, String>();
		try {
			inputs = new InputStreamReader(new FileInputStream("/proc/stat"));
			buffer = new BufferedReader(inputs);
			String line = "";
			while (true) {
				line = buffer.readLine();
				if (line == null) {
					break;
				}
				if (line.startsWith("cpu")) {
					StringTokenizer tokenizer = new StringTokenizer(line);
					List<String> temp = new ArrayList<String>();
					while (tokenizer.hasMoreElements()) {
						String value = tokenizer.nextToken();
						temp.add(value);
					}
					map.put("user", temp.get(1));
					map.put("nice", temp.get(2));
					map.put("system", temp.get(3));
					map.put("idle", temp.get(4));
					map.put("iowait", temp.get(5));
					map.put("irq", temp.get(6));
					map.put("softirq", temp.get(7));
					map.put("stealstolen", temp.get(8));
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				buffer.close();
			} catch (Exception e2) {
			}
			try {
				inputs.close();
			} catch (Exception e2) {
			}
		}
		return map;
	}
	
	/**
	 * window读取cpu相关信息
	 * @param proc
	 * @return
	 */
	private static long[] readCpuForWindows(final Process proc) {
		long[] retn = new long[2];
		try {
			proc.getOutputStream().close();
			InputStreamReader ir = new InputStreamReader(proc.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);
			String line = input.readLine();
			if (line == null || line.length() < FAULT_LENGTH) {
				return null;
			}
			int capidx = line.indexOf("Caption");
			int cmdidx = line.indexOf("CommandLine");
			int rocidx = line.indexOf("ReadOperationCount");
			int umtidx = line.indexOf("UserModeTime");
			int kmtidx = line.indexOf("KernelModeTime");
			int wocidx = line.indexOf("WriteOperationCount");
			// Caption,CommandLine,KernelModeTime,ReadOperationCount,ThreadCount,UserModeTime,WriteOperationCount
			long idletime = 0;
			long kneltime = 0;
			long usertime = 0;
			while ((line = input.readLine()) != null) {
				if (line.length() < wocidx) {
					continue;
				}
				// 字段出现顺序：Caption,CommandLine,KernelModeTime,ReadOperationCount,
				// ThreadCount,UserModeTime,WriteOperation
				String caption = substring(line, capidx, cmdidx - 1).trim();
				String cmd = substring(line, cmdidx, kmtidx - 1).trim();
				if (cmd.indexOf("javaw.exe") >= 0) {
					continue;
				}
				// log.info("line="+line);
				if (caption.equals("System Idle Process") || caption.equals("System")) {
					idletime += Long.valueOf(substring(line, kmtidx, rocidx - 1).trim()).longValue();
					idletime += Long.valueOf(substring(line, umtidx, wocidx - 1).trim()).longValue();
					continue;
				}
				
				kneltime += Long.valueOf(substring(line, kmtidx, rocidx - 1).trim()).longValue();
				usertime += Long.valueOf(substring(line, umtidx, wocidx - 1).trim()).longValue();
			}
			retn[0] = idletime;
			retn[1] = kneltime + usertime;
			return retn;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				proc.getInputStream().close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private static String substring(String src, int start_idx, int end_idx) throws UnsupportedEncodingException {
		byte[] b = src.getBytes("utf-8");
		String tgt = "";
		for (int i = start_idx; i <= end_idx; i++) {
			tgt += (char) b[i];
		}
		return tgt;
	}
}
