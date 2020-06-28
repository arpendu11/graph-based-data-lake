package org.practice.sparkExecutors;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import org.apache.spark.sql.streaming.StreamingQueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SynchronizedWorker {
	
	Logger logger = LoggerFactory.getLogger(SynchronizedWorker.class);

	public Thread setThread(final String name,
			final Lock lock,
			final Condition condition,
			String actualThreadName,
			String nextThreadName,
	        ThreadName threadName) {
	    Thread thread = new Thread() {
	        @Override
	        public void run() {
	            while (true) {
	                lock.lock();
	                try {
	                    while (threadName.getName() != actualThreadName) {
	                        try {
	                            condition.await();
	                        } catch (InterruptedException e) {
	                            e.printStackTrace();
	                        }
	                    }
	                    Thread.sleep(10000);
	                    logger.info("=================================================================");
	                    logger.info("Starting " + actualThreadName + " !!");
	                    SparkJobService sparkJob = new SparkJobService();
	                    switch (actualThreadName) {
						case "UserIngestion":
							sparkJob.startUserIngestion();
							break;
						
						case "AccountIngestion":
							sparkJob.startAccountIngestion();
							break;
							
						case "AccessRightIngestion":
							sparkJob.startAccessRightIngestion();
							break;
						
						case "UserGroupIngestion":
							sparkJob.startUserGroupIngestion();
							break;
							
						case "ApplicationIngestion":
							sparkJob.startApplicationIngestion();
							break;
							
						case "RelationIngestion":
							sparkJob.startRelationIngestion();
							break;
							
						default:
							logger.info("Nothing to do... Taking some rest...!!");
							Thread.sleep(120000);
							logger.info("Wake up... Time to start working again...!!");
							break;
						}
	                    threadName.setName(nextThreadName);
	                    logger.info("=================================================================");
	                    condition.signalAll();
	                } catch (InterruptedException | StreamingQueryException e) {
						e.printStackTrace();
					} finally {
	                    lock.unlock();
	                }
	            }
	        }
	    };
	    return thread;
	}
}
