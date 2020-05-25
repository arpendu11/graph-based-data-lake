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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@IdClass(CommonId.class)
@Table(name="entity_access_right")
public class AccessRight {

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
	@Column(name = "entitlement_id")
	private String entitlementId;
	@Column(name = "entitlement_name")
	private String entitlementName;
	@Column(name = "entitlement_description")
	private String entitlementDescription;
	
	public static AccessRight from(Node node) {
	    return new AccessRight(node.get("id").asString(),
	    		node.get("type").asString(),
	    		node.get("startTime").asLong(),
	    		node.get("endTime").asLong(),
	    		node.get("entitlementId").asString(),
	    		node.get("entitlementName").asString(),
	    		node.get("entitlementDescription").asString());
	}
	
	private static StructType structType = DataTypes.createStructType(new StructField[] {
			DataTypes.createStructField("id", DataTypes.StringType, false),
			DataTypes.createStructField("type", DataTypes.StringType, false),
			DataTypes.createStructField("startTime", DataTypes.LongType, false),
			DataTypes.createStructField("endTime", DataTypes.LongType, false),
			DataTypes.createStructField("entitlementId", DataTypes.StringType, false),
			DataTypes.createStructField("entitlementName", DataTypes.StringType, false),
			DataTypes.createStructField("entitlementDescription", DataTypes.StringType, false)});

	public static StructType getStructType() {
		return structType;
	}
}
