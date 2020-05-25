package org.practice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.neo4j.driver.types.Relationship;

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
@Table(name="entity_relation")
public class Relation {

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
	@Column(name = "rel_lhs_id")
	private String lhsMappingId;
	@Column(name = "rel_lhs_type")
	private String lhsMappingType;
	@Column(name = "rel_rhs_id")
	private String rhsMappingId;
	@Column(name = "rel_rhs_type")
	private String rhsMappingType;
	
	public static Relation from(Relationship relation) {
	    return new Relation(relation.get("id").asString(),
	    		relation.get("type").asString(),
	    		relation.get("startTime").asLong(),
	    		relation.get("endTime").asLong(),
	    		relation.get("lhsMappingId").asString(),
	    		relation.get("lhsMappingType").asString(),
	    		relation.get("rhsMappingId").asString(),
	    		relation.get("rhsMappingType").asString());
	}
	
	private static StructType structType = DataTypes.createStructType(new StructField[] {
			DataTypes.createStructField("id", DataTypes.StringType, false),
			DataTypes.createStructField("type", DataTypes.StringType, false),
			DataTypes.createStructField("startTime", DataTypes.LongType, false),
			DataTypes.createStructField("endTime", DataTypes.LongType, false),
			DataTypes.createStructField("lhsMappingId", DataTypes.StringType, false),
			DataTypes.createStructField("lhsMappingType", DataTypes.StringType, false),
			DataTypes.createStructField("rhsMappingId", DataTypes.StringType, false),
			DataTypes.createStructField("rhsMappingType", DataTypes.StringType, false)});

	public static StructType getStructType() {
		return structType;
	}
}
