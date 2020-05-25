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
@Table(name="entity_identitygroup")
public class UserGroup {

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
	@Column(name = "identitygroup_id")
	private String identityGroupId;
	@Column(name = "identitygroup_name")
	private String identityGroupName;
	@Column(name = "identitygroup_description")
	private String identityGroupDescription;
	
	public static UserGroup from(Node node) {
	    return new UserGroup(node.get("id").asString(),
	    		node.get("type").asString(),
	    		node.get("startTime").asLong(),
	    		node.get("endTime").asLong(),
	    		node.get("identityGroupId").asString(),
	    		node.get("identityGroupName").asString(),
	    		node.get("identityGroupDescription").asString());
	}
	
	private static StructType structType = DataTypes.createStructType(new StructField[] {
			DataTypes.createStructField("id", DataTypes.StringType, false),
			DataTypes.createStructField("type", DataTypes.StringType, false),
			DataTypes.createStructField("startTime", DataTypes.LongType, false),
			DataTypes.createStructField("endTime", DataTypes.LongType, false),
			DataTypes.createStructField("identityGroupId", DataTypes.StringType, false),
			DataTypes.createStructField("identityGroupName", DataTypes.StringType, false),
			DataTypes.createStructField("identityGroupDescription", DataTypes.StringType, false)});

	public static StructType getStructType() {
		return structType;
	}
}
