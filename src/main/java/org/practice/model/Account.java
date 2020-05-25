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

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@IdClass(CommonId.class)
@Table(name="entity_account")
public class Account {

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
	@Column(name = "external_id_authority")
	private String fqdn;
	@Column(name = "external_id_value")
	private String accountName;
	@Column(name = "account_domain")
	private String accountDomain;
	@Column(name = "account_status")
	private String accountStatus;
	
	public static Account from(Node node) {
	    return new Account(node.get("id").asString(),
	    		node.get("type").asString(),
	    		node.get("startTime").asLong(),
	    		node.get("endTime").asLong(),
	    		node.get("fqdn").asString(),
	    		node.get("accountName").asString(),
	    		node.get("accountDomain").asString(),
	    		node.get("accountStatus").asString());
	}
	
	private static StructType structType = DataTypes.createStructType(new StructField[] {
			DataTypes.createStructField("id", DataTypes.StringType, false),
			DataTypes.createStructField("type", DataTypes.StringType, false),
			DataTypes.createStructField("startTime", DataTypes.LongType, false),
			DataTypes.createStructField("endTime", DataTypes.LongType, false),
			DataTypes.createStructField("fqdn", DataTypes.StringType, false),
			DataTypes.createStructField("accountName", DataTypes.StringType, false),
			DataTypes.createStructField("accountDomain", DataTypes.StringType, false),
			DataTypes.createStructField("accountStatus", DataTypes.StringType, false)});
	
	public String getId() {
		return id;
	}
	public String getType() {
		return type;
	}
	public long getStartTime() {
		return startTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public String getFqdn() {
		return fqdn;
	}
	public String getAccountName() {
		return accountName;
	}
	public String getAccountDomain() {
		return accountDomain;
	}
	public String getAccountStatus() {
		return accountStatus;
	}
	public static StructType getStructType() {
		return structType;
	}
	
}
