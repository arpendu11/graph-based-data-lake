package org.practice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.neo4j.driver.types.Node;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@IdClass(CommonId.class)
@Table(name="entity_application")
public class Application {

	@Id
	@Column(name = "entity_unique_id")
	private String id;
	@Column(name = "entity_class_type")
	private String type;
	@Id
	@Column(name = "entity_begin_effective_time")
	private long startTime;
	@Id
	@Column(name = "entity_end_effective_time")
	private long endTime;
	@Column(name = "application_name")
	private String applicationName;
	@Column(name = "application_description")
	private String applicationDescription;
	@Column(name = "application_type")
	private String applicationType;
	@Column(name = "application_driver_id")
	private String applicationDriver;
	
	public static Application from(Node node) {
	    return new Application(node.get("id").asString(),
	    		node.get("type").asString(),
	    		node.get("startTime").asLong(),
	    		node.get("endTime").asLong(),
	    		node.get("applicationName").asString(),
	    		node.get("applicationDescription").asString(),
	    		node.get("applicationType").asString(),
	    		node.get("applicationDriver").asString());
	}
	
	private static StructType structType = DataTypes.createStructType(new StructField[] {
			DataTypes.createStructField("id", DataTypes.StringType, false),
			DataTypes.createStructField("type", DataTypes.StringType, false),
			DataTypes.createStructField("startTime", DataTypes.LongType, false),
			DataTypes.createStructField("endTime", DataTypes.LongType, false),
			DataTypes.createStructField("applicationName", DataTypes.StringType, false),
			DataTypes.createStructField("applicationDescription", DataTypes.StringType, false),
			DataTypes.createStructField("applicationType", DataTypes.StringType, false),
			DataTypes.createStructField("applicationDriver", DataTypes.StringType, false)});

	public static StructType getStructType() {
		return structType;
	}
}
