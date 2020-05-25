package org.apache;

import org.apache.sparkExecutors.ThreadExecutor;
import org.practice.services.AccessRightService;
import org.practice.services.AccountService;
import org.practice.services.RelationService;
import org.practice.services.UserGroupService;
import org.practice.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class Main {

	public static void main(String[] args) {
		Quarkus.run(StartSpark.class, args);
	}
	
	public static class StartSpark implements QuarkusApplication {
		
		Logger logger = LoggerFactory.getLogger(Main.class);

		@Override
		public int run(String... args) throws Exception {
			logger.info("Kafka emission started !!");
			UserService userService = new UserService();
			AccountService accountService = new AccountService();
			RelationService relationService = new RelationService();
			UserGroupService userGroupService = new UserGroupService();
			AccessRightService accessRightService = new AccessRightService();
			userService.stream();
			accountService.stream();
			relationService.stream();
			userGroupService.stream();
			accessRightService.stream();
			logger.info("Threads started !!");
			logger.info("Spark job started !!");
			ThreadExecutor executor = new ThreadExecutor();
			executor.execute();	
		   
            Quarkus.waitForExit();
            return 0;
		}
		
	}
}
