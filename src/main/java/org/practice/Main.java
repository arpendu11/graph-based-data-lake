package org.practice;

import org.practice.services.AccessRightService;
import org.practice.services.AccountService;
import org.practice.services.ApplicationService;
import org.practice.services.RelationService;
import org.practice.services.UserGroupService;
import org.practice.services.UserService;
import org.practice.sparkExecutors.ThreadExecutor;
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
			
			System.setProperty("hadoop.home.dir", "D:\\winutils");
			
			logger.info("Kafka emission started !!");
			UserService userService = new UserService();
			AccountService accountService = new AccountService();
			RelationService relationService = new RelationService();
			UserGroupService userGroupService = new UserGroupService();
			AccessRightService accessRightService = new AccessRightService();
			ApplicationService applicationService = new ApplicationService();
			userService.stream();
			accountService.stream();
			relationService.stream();
			userGroupService.stream();
			accessRightService.stream();
			applicationService.stream();
			
			logger.info("Threads started !!");
			logger.info("Spark job started !!");
			ThreadExecutor executor = new ThreadExecutor();
			executor.execute();
			
            Quarkus.waitForExit();
            return 0;
		}
		
	}
}
