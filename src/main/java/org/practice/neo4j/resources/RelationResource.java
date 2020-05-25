package org.practice.neo4j.resources;

import java.net.URI;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Values;
import org.neo4j.driver.async.AsyncSession;
import org.neo4j.driver.exceptions.NoSuchRecordException;
import org.practice.model.Relation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/neo/relations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RelationResource {
	
	Logger logger = LoggerFactory.getLogger(RelationResource.class);

	@Inject
	Driver driver;

	@POST
	public CompletionStage<Response> create(Relation relation) {
		logger.info(relation.toString());
		AsyncSession session = driver.asyncSession();
		String neoQuery;
		switch (relation.getType()) {
		case "PersonaHasAccount":
			neoQuery = "MATCH (u:User), (a:Account) WHERE u.id = $lhsMappingId AND a.id = $rhsMappingId"
					+ " AND u.startTime <= $startTime AND a.startTime <= $startTime"
					+ " MERGE (u)-[r:PersonaHasAccount {id: $id, type: $type, startTime: $startTime,"
					+ " endTime: $endTime, lhsMappingId: $lhsMappingId, lhsMappingType: $lhsMappingType,"
					+ " rhsMappingId: $rhsMappingId, rhsMappingType: $rhsMappingType}]->(a) RETURN r";
			break;
			
		case "EntitlementHasChildEntitlement":
			neoQuery = "MATCH (ar1:AccessRight), (ar2:AccessRight) WHERE ar1.id = $lhsMappingId AND ar2.id = $rhsMappingId"
					+ " AND ar1.startTime <= $startTime AND ar2.startTime <= $startTime"
					+ " MERGE (ar1)-[r:EntitlementHasChildEntitlement {id: $id, type: $type, startTime: $startTime,"
					+ " endTime: $endTime, lhsMappingId: $lhsMappingId, lhsMappingType: $lhsMappingType,"
					+ " rhsMappingId: $rhsMappingId, rhsMappingType: $rhsMappingType}]->(ar2) RETURN r";
			break;
			
		case "IdentityHasEntitlement":
			neoQuery = "MATCH (u:User), (ar:AccessRight) WHERE u.id = $lhsMappingId AND ar.id = $rhsMappingId"
					+ " AND u.startTime <= $startTime AND ar.startTime <= $startTime"
					+ " MERGE (u)-[r:IdentityHasEntitlement {id: $id, type: $type, startTime: $startTime,"
					+ " endTime: $endTime, lhsMappingId: $lhsMappingId, lhsMappingType: $lhsMappingType,"
					+ " rhsMappingId: $rhsMappingId, rhsMappingType: $rhsMappingType}]->(ar) RETURN r";
			break;
			
		case "PersonaReportsToPersona":
			neoQuery = "MATCH (u1:User), (u2:User) WHERE u1.id = $lhsMappingId AND u2.id = $rhsMappingId"
					+ " AND u1.startTime <= $startTime AND u2.startTime <= $startTime"
					+ " MERGE (u1)-[r:PersonaReportsToPersona {id: $id, type: $type, startTime: $startTime,"
					+ " endTime: $endTime, lhsMappingId: $lhsMappingId, lhsMappingType: $lhsMappingType,"
					+ " rhsMappingId: $rhsMappingId, rhsMappingType: $rhsMappingType}]->(u2) RETURN r";
			break;
			
		case "IdentityGroupHasIdentity":
			neoQuery = "MATCH (ug:UserGroup), (u:User) WHERE ug.id = $lhsMappingId AND u.id = $rhsMappingId"
					+ " AND ug.startTime <= $startTime AND u.startTime <= $startTime"
					+ " MERGE (ug)-[r:IdentityGroupHasIdentity {id: $id, type: $type, startTime: $startTime,"
					+ " endTime: $endTime, lhsMappingId: $lhsMappingId, lhsMappingType: $lhsMappingType,"
					+ " rhsMappingId: $rhsMappingId, rhsMappingType: $rhsMappingType}]->(u) RETURN r";
			break;
			
		case "IdentityGroupHasEntitlement":
			neoQuery = "MATCH (ug:UserGroup), (ar:AccessRight) WHERE ug.id = $lhsMappingId AND ar.id = $rhsMappingId"
					+ " AND ug.startTime <= $startTime AND ar.startTime <= $startTime"
					+ " MERGE (ug)-[r:IdentityGroupHasEntitlement {id: $id, type: $type, startTime: $startTime,"
					+ " endTime: $endTime, lhsMappingId: $lhsMappingId, lhsMappingType: $lhsMappingType,"
					+ " rhsMappingId: $rhsMappingId, rhsMappingType: $rhsMappingType}]->(ar) RETURN r";
			break;
			
		case "IdentityGroupHasChildIdentityGroup":
			neoQuery = "MATCH (ug1:UserGroup), (ug2:UserGroup) WHERE ug1.id = $lhsMappingId AND ug2.id = $rhsMappingId"
					+ " AND ug1.startTime <= $startTime AND ug2.startTime <= $startTime"
					+ " MERGE (ug1)-[r:IdentityGroupHasChildIdentityGroup {id: $id, type: $type, startTime: $startTime,"
					+ " endTime: $endTime, lhsMappingId: $lhsMappingId, lhsMappingType: $lhsMappingType,"
					+ " rhsMappingId: $rhsMappingId, rhsMappingType: $rhsMappingType}]->(ug2) RETURN r";
			break;
			
		case "AccountHasEntitlement":
			neoQuery = "MATCH (a:Account), (ar:AccessRight) WHERE a.id = $lhsMappingId AND ar.id = $rhsMappingId"
					+ " AND a.startTime <= $startTime AND ar.startTime <= $startTime"
					+ " MERGE (a)-[r:AccountHasEntitlement {id: $id, type: $type, startTime: $startTime,"
					+ " endTime: $endTime, lhsMappingId: $lhsMappingId, lhsMappingType: $lhsMappingType,"
					+ " rhsMappingId: $rhsMappingId, rhsMappingType: $rhsMappingType}]->(ar) RETURN r";
			break;

		default:
			neoQuery = null;
			break;
		}

		return session
				.writeTransactionAsync(tx -> tx.runAsync(neoQuery, Values.parameters("id", relation.getId(), "type",
						relation.getType(), "startTime", relation.getStartTime(), "endTime", relation.getEndTime(),
						"lhsMappingId", relation.getLhsMappingId(), "lhsMappingType", relation.getLhsMappingType(),
						"rhsMappingId", relation.getRhsMappingId(), "rhsMappingType", relation.getRhsMappingType()))
						.thenCompose(fn -> fn.singleAsync()))
				.thenApply(record -> Relation.from(record.get("r").asRelationship()))
				.thenCompose(persistedRelation -> session.closeAsync().thenApply(signal -> persistedRelation))
				.thenApply(persistedRelation -> Response
						.created(URI.create("/neo/relations/" + persistedRelation.getId())).build());

	}

	@GET
	@Path("/{id}")
	public CompletionStage<Response> getSingle(@PathParam("id") String id) {
		AsyncSession session = driver.asyncSession();
		return session.readTransactionAsync(
				tx -> tx.runAsync("MATCH ()-[r]->() WHERE r.id = $id RETURN r", Values.parameters("id", id))
						.thenCompose(fn -> fn.singleAsync()))
				.handle((record, exception) -> {
					if (exception != null) {
						Throwable source = exception;
						if (exception instanceof CompletionException) {
							source = ((CompletionException) exception).getCause();
						}
						Status status = Status.INTERNAL_SERVER_ERROR;
						if (source instanceof NoSuchRecordException) {
							status = Status.NOT_FOUND;
						}
						return Response.status(status).build();
					} else {
						return Response.ok(Relation.from(record.get("r").asRelationship())).build();
					}
				}).thenCompose(response -> session.closeAsync().thenApply(signal -> response));
	}
}
