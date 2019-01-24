package com.assist4j.schedule.base;


import java.util.List;
import java.util.concurrent.ExecutorService;


/**
 * @author yuwei
 */
public abstract class AbstractThreadPoolPageTask<T> extends AbstractThreadPoolTask<T> {
	@Override
	protected void executeInThreadPool(ExecutorService executor) {
		while (true) {
			List<T> taskList = queryTaskList();
			if (taskList == null || taskList.size() <= 0) {
				break;
			}
			executeInThreadPool(executor, taskList);
		}
	}
}


