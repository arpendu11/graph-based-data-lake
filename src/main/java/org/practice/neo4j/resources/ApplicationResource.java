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
import org.practice.model.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/neo/applications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ApplicationResource {
	
	Logger logger = LoggerFactory.getLogger(ApplicationResource.class);

	@Inject
	Driver driver;
	
	@Path("/all")
	@GET
	public CompletionStage<Response> get() {
	    AsyncSession session = driver.asyncSession(); 
	    return session
	        .runAsync("MATCH (ap:Application) RETURN ap")  
	        .thenCompose(cursor ->  
	            cursor.listAsync(record -> Application.from(record.get("ap").asNode()))
	        )
	        .thenCompose(application ->  
	            session.closeAsync().thenApply(signal -> application)
	        )
	        .thenApply(Response::ok) 
	        .thenApply(ResponseBuilder::build);
	}
	
	@POST
	public CompletionStage<Response> create(Application application) {
		logger.info(application.toString());
	    AsyncSession session = driver.asyncSession();
	    return session
	        .writeTransactionAsync(tx -> tx
	            .runAsync("MERGE (ap:Application {id: $id,"
	            		+ " type: $type,"
	            		+ " startTime: $startTime,"
	            		+ " endTime: $endTime,"
	            		+ " applicationName: $applicationName,"
	            		+ " applicationDescription: $applicationDescription,"
	            		+ " applicationType: $applicationType,"
	            		+ " applicationDriver: $applicationDriver}) RETURN ap",
	            		Values.parameters("id", application.getId(),
	            				"type", application.getType(),
	            				"startTime", application.getStartTime(),
	            				"endTime", application.getEndTime(),
	            				"applicationName", application.getApplicationName(),
	            				"applicationDescription", application.getApplicationDescription(),
	            				"applicationType", application.getApplicationDriver()))
	            .thenCompose(fn -> fn.singleAsync())
	        )
	        .thenApply(record -> Application.from(record.get("ap").asNode()))
	        .thenCompose(persistedApplication -> session.closeAsync().thenApply(signal -> persistedApplication))
	        .thenApply(persistedApplication -> Response
	            .created(URI.create("/neo/applications/" + persistedApplication.getId()))
	            .build()
	        );
	}
	
	@GET
	@Path("/{id}")
	public CompletionStage<Response> getSingle(@PathParam("id") String id) {
	    AsyncSession session = driver.asyncSession();
	    return session
	        .readTransactionAsync(tx -> tx
	            .runAsync("MATCH (ap:Application) WHERE ap.id = $id RETURN ap", Values.parameters("id", id))
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
	            return Response.ok(Application.from(record.get("ap").asNode())).build();
	        }
	    })
	    .thenCompose(response -> session.closeAsync().thenApply(signal -> response));
	}
}
