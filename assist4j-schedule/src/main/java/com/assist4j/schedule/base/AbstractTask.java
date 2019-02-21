package com.assist4j.schedule.base;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author yuwei
 */
public abstract class AbstractTask {
	private static final Logger log = LoggerFactory.getLogger(AbstractTask.class);




	public AbstractTask() {
		
	}

	public void execute() {
		log.info("{} - start...", this.getClass().getName());
		long startTime = System.currentTimeMillis();
		init();
		executeTask();
		destroy();
		log.info("{} - end...", this.getClass().getName());
		long timeCost = System.currentTimeMillis() - startTime;
		log.info("Total-time-cost:{}s", timeCost / 1000.0);
	}

	protected void init() {
		
	}
	protected void destroy() {
		
	}
	protected abstract void executeTask();
}
