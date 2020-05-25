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

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Data
@RegisterForReflection
@Entity
@IdClass(CommonId.class)
@Table(name = "entity_identity")
public class User {

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
	@Column(name = "identity_name_given")
	private String firstName;
	@Column(name = "identity_name_middle")
	private String middleName;
	@Column(name = "identity_name_family")
	private String lastName;
	@Column(name = "identity_phone_home")
	private String homePhone;
	@Column(name = "identity_phone_mobile")
	private String mobilePhone;
	@Column(name = "identity_phone_office")
	private String officePhone;
	@Column(name = "identity_notes")
	private String notes;
	@Column(name = "identity_location")
	private String location;
	@Column(name = "identity_email")
	private String email;
	@Column(name = "identity_photo")
	private String photo;
	@Column(name = "persona_id")
	private String employeeId;
	@Column(name = "persona_title")
	private String employeeTitle;
	@Column(name = "persona_type")
	private String employeeType;
	@Column(name = "persona_status")
	private String employeeStatus;
	@Column(name = "persona_organization")
	private String employeeOrganization;
	
	public static User from(Node node) {
	    return new User(node.get("id").asString(),
	    		node.get("type").asString(),
	    		node.get("startTime").asLong(),
	    		node.get("endTime").asLong(),
	    		node.get("firstName").asString(),
	    		node.get("middleName").asString(),
	    		node.get("lastName").asString(),
	    		node.get("homePhone").asString(),
	    		node.get("mobilePhone").asString(),
	    		node.get("officePhone").asString(),
	    		node.get("notes").asString(),
	    		node.get("location").asString(),
	    		node.get("email").asString(),
	    		node.get("photo").asString(),
	    		node.get("employeeId").asString(),
	    		node.get("employeeTitle").asString(),
	    		node.get("employeeType").asString(),
	    		node.get("employeeStatus").asString(),
	    		node.get("employeeOrganization").asString());
	}
	
	private static StructType structType = DataTypes.createStructType(new StructField[] {
			DataTypes.createStructField("id", DataTypes.StringType, false),
			DataTypes.createStructField("type", DataTypes.StringType, false),
			DataTypes.createStructField("startTime", DataTypes.LongType, false),
			DataTypes.createStructField("endTime", DataTypes.LongType, false),
			DataTypes.createStructField("firstName", DataTypes.StringType, false),
			DataTypes.createStructField("middleName", DataTypes.StringType, false),
			DataTypes.createStructField("lastName", DataTypes.StringType, false),
			DataTypes.createStructField("homePhone", DataTypes.StringType, false),
			DataTypes.createStructField("mobilePhone", DataTypes.StringType, false),
			DataTypes.createStructField("officePhone", DataTypes.StringType, false),
			DataTypes.createStructField("notes", DataTypes.StringType, false),
			DataTypes.createStructField("location", DataTypes.StringType, false),
			DataTypes.createStructField("email", DataTypes.StringType, false),
			DataTypes.createStructField("photo", DataTypes.StringType, false),
			DataTypes.createStructField("employeeId", DataTypes.StringType, false),
			DataTypes.createStructField("employeeTitle", DataTypes.StringType, false),
			DataTypes.createStructField("employeeType", DataTypes.StringType, false),
			DataTypes.createStructField("employeeStatus", DataTypes.StringType, false),
			DataTypes.createStructField("employeeOrganization", DataTypes.StringType, false)});
	
	public static StructType getStructType() {
		return structType;
	}
		
}
