package com.assist4j.schedule.base;


import java.util.List;
import java.util.concurrent.ExecutorService;


/**
 * @author yuwei
 */
public abstract class AbstractThreadPoolPageTask<T> extends AbstractThreadPoolTask<T> {
	private int pageSize = 20;
	
	
	@Override
	protected void executeInThreadPool(ExecutorService executor) {
		while (true) {
			List<T> taskList = queryTaskList(1, pageSize);
			if (taskList == null || taskList.size() <= 0) {
				break;
			}
			executeInThreadPool(executor, taskList);
		}
	}


	@Override
	protected final List<T> queryTaskList() {
		throw new RuntimeException("Not Supported.");
	}
	
	protected abstract List<T> queryTaskList(int pageNo, int pageSize);
}


