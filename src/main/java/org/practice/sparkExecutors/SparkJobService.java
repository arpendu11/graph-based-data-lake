package org.practice.sparkExecutors;

import javax.enterprise.context.ApplicationScoped;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.eclipse.microprofile.config.ConfigProvider;
import org.practice.model.AccessRight;
import org.practice.model.Account;
import org.practice.model.Application;
import org.practice.model.Relation;
import org.practice.model.User;
import org.practice.model.UserGroup;

@ApplicationScoped
public class SparkJobService {
	SparkConf conf = new SparkConf()
			.setAppName("Reactive Streaming  of User to Kafka Data Ingestion Spark Job")
			.setMaster("local[3]");
	
	SparkSession kafkaSession = SparkSession
			.builder()
			.config(conf)
			.getOrCreate();

	public void startUserIngestion() throws StreamingQueryException, InterruptedException {
		String kafkaBS = ConfigProvider.getConfig().getValue("mp.messaging.outgoing.entity-identity.bootstrap.servers", String.class);
		String userTopic = ConfigProvider.getConfig().getValue("mp.messaging.outgoing.entity-identity.topic", String.class);
		String userDeltaTopic = ConfigProvider.getConfig().getValue("mp.messaging.incoming.entity-identity-delta.topic", String.class);
		
		kafkaSession.sparkContext().setLocalProperty("spark.scheduler.mode", "FAIR");
		kafkaSession.sparkContext().setLocalProperty("spark.scheduler.pool", "pool1");
		kafkaSession.sparkContext().setLocalProperty("spark.streaming.stopGracefullyOnShutdown","true");
		
		Dataset<Row> users = kafkaSession.readStream()
				.format("kafka")
				.option("kafka.bootstrap.servers", kafkaBS)
				.option("subscribe", userTopic)
				.option("startingOffsets", "earliest")
				.option("failOnDataLoss", "false")
				.load();
		
		Dataset<User> usersDS = users.selectExpr("CAST(value AS STRING) as message")
				.select(functions.from_json(functions.col("message"), User.getStructType()).as("json"))
				.select("json.*")
				.as(Encoders.bean(User.class));

		StreamingQuery query =  usersDS
				.selectExpr("CAST(startTime AS STRING) AS key", "to_json(struct(*)) AS value")
				.writeStream()
				.format("kafka")
				.option("kafka.bootstrap.servers", kafkaBS)
				.option("topic", userDeltaTopic)
				.option("startingOffsets", "latest")
				.option("endingOffsets", "latest")
				.option("failOnDataLoss", "false")
				.option("checkpointLocation", "./etl-from-json/user")
				.outputMode("update")
				.start();
		
		query.awaitTermination(300000);	
		kafkaSession.stop();
	}
	
	public void startAccountIngestion() throws StreamingQueryException, InterruptedException {
		String kafkaBS = ConfigProvider.getConfig().getValue("mp.messaging.outgoing.entity-identity.bootstrap.servers", String.class);
		String accountTopic = ConfigProvider.getConfig().getValue("mp.messaging.outgoing.entity-account.topic", String.class);
		String accountDeltaTopic = ConfigProvider.getConfig().getValue("mp.messaging.incoming.entity-account-delta.topic", String.class);
		kafkaSession.sparkContext().setLocalProperty("spark.scheduler.mode", "FAIR");
		kafkaSession.sparkContext().setLocalProperty("spark.scheduler.pool", "pool2");
		kafkaSession.sparkContext().setLocalProperty("spark.streaming.stopGracefullyOnShutdown","true");
		
		Dataset<Row> accounts = kafkaSession.readStream()
				.format("kafka")
				.option("kafka.bootstrap.servers", kafkaBS)
				.option("subscribe", accountTopic)
				.option("startingOffsets", "earliest")
				.option("failOnDataLoss", "false")
				.load();
		
		Dataset<Account> accountsDS = accounts.selectExpr("CAST(value AS STRING) as message")
				.select(functions.from_json(functions.col("message"), Account.getStructType()).as("json"))
				.select("json.*")
				.as(Encoders.bean(Account.class));

		StreamingQuery query = accountsDS
				.selectExpr("CAST(startTime AS STRING) AS key", "to_json(struct(*)) AS value")
				.writeStream()
				.format("kafka")
				.option("kafka.bootstrap.servers", kafkaBS)
				.option("topic", accountDeltaTopic)
				.option("startingOffsets", "latest")
				.option("endingOffsets", "latest")
				.option("failOnDataLoss", "false")
				.option("checkpointLocation", "./etl-from-json/account")
				.outputMode("update")
				.start();
		
		query.awaitTermination(300000);
		kafkaSession.stop();
	}
	
	public void startAccessRightIngestion() throws StreamingQueryException, InterruptedException {
		String kafkaBS = ConfigProvider.getConfig().getValue("mp.messaging.outgoing.entity-access-right.bootstrap.servers", String.class);
		String accessRightTopic = ConfigProvider.getConfig().getValue("mp.messaging.outgoing.entity-access-right.topic", String.class);
		String accessRightDeltaTopic = ConfigProvider.getConfig().getValue("mp.messaging.incoming.entity-access-right-delta.topic", String.class);
		
		kafkaSession.sparkContext().setLocalProperty("spark.scheduler.mode", "FAIR");
		kafkaSession.sparkContext().setLocalProperty("spark.scheduler.pool", "pool3");
		kafkaSession.sparkContext().setLocalProperty("spark.streaming.stopGracefullyOnShutdown","true");
		
		Dataset<Row> accessRights = kafkaSession.readStream()
				.format("kafka")
				.option("kafka.bootstrap.servers", kafkaBS)
				.option("subscribe", accessRightTopic)
				.option("startingOffsets", "earliest")
				.option("failOnDataLoss", "false")
				.load();
		
		Dataset<AccessRight> accessRightsDS = accessRights.selectExpr("CAST(value AS STRING) as message")
				.select(functions.from_json(functions.col("message"), AccessRight.getStructType()).as("json"))
				.select("json.*")
				.as(Encoders.bean(AccessRight.class));

		StreamingQuery query = accessRightsDS
				.selectExpr("CAST(startTime AS STRING) AS key", "to_json(struct(*)) AS value")
				.writeStream()
				.format("kafka")
				.option("kafka.bootstrap.servers", kafkaBS)
				.option("topic", accessRightDeltaTopic)
				.option("startingOffsets", "latest")
				.option("endingOffsets", "latest")
				.option("failOnDataLoss", "false")
				.option("checkpointLocation", "./etl-from-json/accessRight")
				.outputMode("update")
				.start();
		
		query.awaitTermination(300000);		
		kafkaSession.stop();
	}
	
	public void startUserGroupIngestion() throws StreamingQueryException, InterruptedException {
		String kafkaBS = ConfigProvider.getConfig().getValue("mp.messaging.outgoing.entity-user-group.bootstrap.servers", String.class);
		String userGroupTopic = ConfigProvider.getConfig().getValue("mp.messaging.outgoing.entity-user-group.topic", String.class);
		String userGroupDeltaTopic = ConfigProvider.getConfig().getValue("mp.messaging.incoming.entity-user-group-delta.topic", String.class);
		
		kafkaSession.sparkContext().setLocalProperty("spark.scheduler.mode", "FAIR");
		kafkaSession.sparkContext().setLocalProperty("spark.scheduler.pool", "pool4");
		kafkaSession.sparkContext().setLocalProperty("spark.streaming.stopGracefullyOnShutdown","true");
		
		Dataset<Row> userGroups = kafkaSession.readStream()
				.format("kafka")
				.option("kafka.bootstrap.servers", kafkaBS)
				.option("subscribe", userGroupTopic)
				.option("startingOffsets", "earliest")
				.option("failOnDataLoss", "false")
				.load();
		
		Dataset<UserGroup> userGroupsDS = userGroups.selectExpr("CAST(value AS STRING) as message")
				.select(functions.from_json(functions.col("message"), UserGroup.getStructType()).as("json"))
				.select("json.*")
				.as(Encoders.bean(UserGroup.class));

		StreamingQuery query = userGroupsDS
				.selectExpr("CAST(startTime AS STRING) AS key", "to_json(struct(*)) AS value")
				.writeStream()
				.format("kafka")
				.option("kafka.bootstrap.servers", kafkaBS)
				.option("topic", userGroupDeltaTopic)
				.option("startingOffsets", "latest")
				.option("endingOffsets", "latest")
				.option("failOnDataLoss", "false")
				.option("checkpointLocation", "./etl-from-json/userGroup")
				.outputMode("update")
				.start();
		
		query.awaitTermination(300000);		
		kafkaSession.stop();
	}
	
	public void startRelationIngestion() throws StreamingQueryException, InterruptedException {
		String kafkaBS = ConfigProvider.getConfig().getValue("mp.messaging.outgoing.entity-identity.bootstrap.servers", String.class);
		String relationTopic = ConfigProvider.getConfig().getValue("mp.messaging.outgoing.entity-relation.topic", String.class);
		String relationDeltaTopic = ConfigProvider.getConfig().getValue("mp.messaging.incoming.entity-relation-delta.topic", String.class);
		
		kafkaSession.sparkContext().setLocalProperty("spark.scheduler.mode", "FAIR");
		kafkaSession.sparkContext().setLocalProperty("spark.scheduler.pool", "pool5");
		kafkaSession.sparkContext().setLocalProperty("spark.streaming.stopGracefullyOnShutdown","true");
		
		Dataset<Row> relations = kafkaSession.readStream()
				.format("kafka")
				.option("kafka.bootstrap.servers", kafkaBS)
				.option("subscribe", relationTopic)
				.option("startingOffsets", "earliest")
				.option("failOnDataLoss", "false")
				.load();
		
		Dataset<Relation> relationsDS = relations.selectExpr("CAST(value AS STRING) as message")
				.select(functions.from_json(functions.col("message"), Relation.getStructType()).as("json"))
				.select("json.*")
				.as(Encoders.bean(Relation.class));

		StreamingQuery query = relationsDS
				.selectExpr("CAST(startTime AS STRING) AS key", "to_json(struct(*)) AS value")
				.writeStream()
				.format("kafka")
				.option("kafka.bootstrap.servers", kafkaBS)
				.option("topic", relationDeltaTopic)
				.option("startingOffsets", "latest")
				.option("endingOffsets", "latest")
				.option("failOnDataLoss", "false")
				.option("checkpointLocation", "./etl-from-json/relation")
				.outputMode("update")
				.start();
		
		query.awaitTermination(300000);		
		kafkaSession.stop();
	}
	
	public void startApplicationIngestion() throws StreamingQueryException, InterruptedException {
		String kafkaBS = ConfigProvider.getConfig().getValue("mp.messaging.outgoing.entity-application.bootstrap.servers", String.class);
		String applicationTopic = ConfigProvider.getConfig().getValue("mp.messaging.outgoing.entity-application.topic", String.class);
		String applicationDeltaTopic = ConfigProvider.getConfig().getValue("mp.messaging.incoming.entity-application-delta.topic", String.class);
		
		kafkaSession.sparkContext().setLocalProperty("spark.scheduler.mode", "FAIR");
		kafkaSession.sparkContext().setLocalProperty("spark.scheduler.pool", "pool6");
		kafkaSession.sparkContext().setLocalProperty("spark.streaming.stopGracefullyOnShutdown","true");
		
		Dataset<Row> applications = kafkaSession.readStream()
				.format("kafka")
				.option("kafka.bootstrap.servers", kafkaBS)
				.option("subscribe", applicationTopic)
				.option("startingOffsets", "earliest")
				.option("failOnDataLoss", "false")
				.load();
		
		Dataset<Application> applicationsDS = applications.selectExpr("CAST(value AS STRING) as message")
				.select(functions.from_json(functions.col("message"), Application.getStructType()).as("json"))
				.select("json.*")
				.as(Encoders.bean(Application.class));

		StreamingQuery query = applicationsDS
				.selectExpr("CAST(startTime AS STRING) AS key", "to_json(struct(*)) AS value")
				.writeStream()
				.format("kafka")
				.option("kafka.bootstrap.servers", kafkaBS)
				.option("topic", applicationDeltaTopic)
				.option("startingOffsets", "latest")
				.option("endingOffsets", "latest")
				.option("failOnDataLoss", "false")
				.option("checkpointLocation", "./etl-from-json/application")
				.outputMode("update")
				.start();
		
		query.awaitTermination(300000);		
		kafkaSession.stop();
	}
}
