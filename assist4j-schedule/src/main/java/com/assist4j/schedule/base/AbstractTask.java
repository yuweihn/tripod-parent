package com.assist4j.schedule.base;


import com.assist4j.schedule.LeaderElector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author yuwei
 */
public abstract class AbstractTask {
	private static final Logger log = LoggerFactory.getLogger(AbstractTask.class);
	private LeaderElector leaderElector;
	private boolean release = true;



	public AbstractTask() {
		
	}

	public void setLeaderElector(LeaderElector leaderElector) {
		this.leaderElector = leaderElector;
	}

	public void execute() {
		String lockName = getLockName();
		if (leaderElector.acquire(lockName)) {
			log.info("{} - start...", this.getClass().getName());
			long startTime = System.currentTimeMillis();
			String localNode = leaderElector.getLocalNode(lockName);
			before();
			executeTask();
			after();
			if (release) {
				leaderElector.release(lockName);
			}
			log.info("{} - end...", this.getClass().getName());
			long timeCost = System.currentTimeMillis() - startTime;
			log.info("Job executed here, {}, timeCost: {}s", localNode, timeCost / 1000.0);
		} else {
			String leaderNode = leaderElector.getLeaderNode(lockName);
			if (leaderNode == null) {
				log.info("Not leader, job didn't execute!");
			} else {
				log.info("Not leader, job didn't execute! Leader: {}", leaderNode);
			}
		}
	}

	protected void before() {
		if (leaderElector == null) {
			leaderElector = new LeaderElector() {
				@Override
				public boolean acquire(String lock) {
					return true;
				}

				@Override
				public void release(String lock) {

				}

				@Override
				public String getLocalNode(String lock) {
					return "Local";
				}

				@Override
				public String getLeaderNode(String lock) {
					return "Local";
				}
			};
		}
	}
	protected void after() {
		
	}
	protected abstract void executeTask();
	protected String getLockName() {
		return this.getClass().getName();
	}
	public void setRelease(boolean release) {
		this.release = release;
	}
}
