package org.practice.sparkExecutors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadExecutor {
	
	private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

	public void execute() throws InterruptedException {		
		
		ThreadName threadName = new ThreadName();
		threadName.setName("UserIngestion");
	    SynchronizedWorker worker = new SynchronizedWorker();
	    ExecutorService executor = Executors.newCachedThreadPool();
	    
	    executor.submit(worker.setThread("UserIngestion",lock, condition, "UserIngestion", "AccountIngestion", threadName));
	    executor.submit(worker.setThread("AccountIngestion",lock, condition, "AccountIngestion", "AccessRightIngestion", threadName));
	    executor.submit(worker.setThread("AccessRightIngestion",lock, condition, "AccessRightIngestion", "UserGroupIngestion", threadName));
	    executor.submit(worker.setThread("UserGroupIngestion",lock, condition, "UserGroupIngestion", "ApplicationIngestion", threadName));
	    executor.submit(worker.setThread("ApplicationIngestion",lock, condition, "ApplicationIngestion", "RelationIngestion", threadName));
	    executor.submit(worker.setThread("RelationIngestion",lock, condition, "RelationIngestion", "TakingRest", threadName));
	    executor.submit(worker.setThread("TakingRest",lock, condition, "TakingRest", "UserIngestion", threadName));
	}
	


}
