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
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Values;
import org.neo4j.driver.async.AsyncSession;
import org.neo4j.driver.exceptions.NoSuchRecordException;
import org.practice.model.UserGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/neo/usergroups")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserGroupResource {
	
	Logger logger = LoggerFactory.getLogger(UserGroupResource.class);

	@Inject
	Driver driver;
	
	@Path("/all")
	@GET
	public CompletionStage<Response> get() {
	    AsyncSession session = driver.asyncSession(); 
	    return session
	        .runAsync("MATCH (ug:UserGroup) RETURN ug")  
	        .thenCompose(cursor ->  
	            cursor.listAsync(record -> UserGroup.from(record.get("ug").asNode()))
	        )
	        .thenCompose(userGroup ->  
	            session.closeAsync().thenApply(signal -> userGroup)
	        )
	        .thenApply(Response::ok) 
	        .thenApply(ResponseBuilder::build);
	}
	
	@POST
	public CompletionStage<Response> create(UserGroup userGroup) {
		logger.info(userGroup.toString());
	    AsyncSession session = driver.asyncSession();
	    return session
	        .writeTransactionAsync(tx -> tx
	            .runAsync("MERGE (ug:UserGroup {id: $id,"
	            		+ " type: $type,"
	            		+ " startTime: $startTime,"
	            		+ " endTime: $endTime,"
	            		+ " identityGroupId: $identityGroupId,"
	            		+ " identityGroupName: $identityGroupName,"
	            		+ " identityGroupDescription: $identityGroupDescription}) RETURN ug",
	            		Values.parameters("id", userGroup.getId(),
	            				"type", userGroup.getType(),
	            				"startTime", userGroup.getStartTime(),
	            				"endTime", userGroup.getEndTime(),
	            				"identityGroupId", userGroup.getIdentityGroupId(),
	            				"identityGroupName", userGroup.getIdentityGroupName(),
	            				"identityGroupDescription", userGroup.getIdentityGroupDescription()))
	            .thenCompose(fn -> fn.singleAsync())
	        )
	        .thenApply(record -> UserGroup.from(record.get("ug").asNode()))
	        .thenCompose(persistedUserGroup -> session.closeAsync().thenApply(signal -> persistedUserGroup))
	        .thenApply(persistedUserGroup -> Response
	            .created(URI.create("/neo/usergroups/" + persistedUserGroup.getId()))
	            .build()
	        );
	}
	
	@GET
	@Path("/{id}")
	public CompletionStage<Response> getSingle(@PathParam("id") String id) {
	    AsyncSession session = driver.asyncSession();
	    return session
	        .readTransactionAsync(tx -> tx
	            .runAsync("MATCH (ug:UserGroup) WHERE ug.id = $id RETURN ug", Values.parameters("id", id))
	            .thenCompose(fn -> fn.singleAsync())
	    )
	    .handle((record, exception) -> {
	        if(exception != null) {
	            Throwable source = exception;
	            if(exception instanceof CompletionException) {
	                source = ((CompletionException)exception).getCause();
	            }
	            Status status = Status.INTERNAL_SERVER_ERROR;
	            if(source instanceof NoSuchRecordException) {
	                status = Status.NOT_FOUND;
	            }
	            return Response.status(status).build();
	        } else  {
	            return Response.ok(UserGroup.from(record.get("ug").asNode())).build();
	        }
	    })
	    .thenCompose(response -> session.closeAsync().thenApply(signal -> response));
	}
}
