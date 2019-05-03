package com.assist4j.schedule.base;


import java.util.List;
import java.util.concurrent.ExecutorService;


/**
 * @author yuwei
 */
public abstract class AbstractThreadPoolPageTask<T> extends AbstractThreadPoolTask<T> {
	private static final int DEFAULT_MAX_TIMES = -1;


	@Override
	protected void executeInThreadPool(ExecutorService executor) {
		int times = 0;
		int maxTimex = getMaxTimes();

		while (true) {
			boolean stop = maxTimex != DEFAULT_MAX_TIMES && times > maxTimex;
			if (stop) {
				break;
			}

			List<T> taskList = queryTaskList();
			if (taskList == null || taskList.size() <= 0) {
				break;
			}
			executeInThreadPool(executor, taskList);
			times++;
		}
	}

	protected int getMaxTimes() {
		return DEFAULT_MAX_TIMES;
	}
}


