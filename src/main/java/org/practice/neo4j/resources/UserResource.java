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
import org.practice.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/neo/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
	
	Logger logger = LoggerFactory.getLogger(UserResource.class);

	@Inject
	Driver driver;
	
	@Path("/all")
	@GET
	public CompletionStage<Response> get() {
	    AsyncSession session = driver.asyncSession(); 
	    return session
	        .runAsync("MATCH (u:User) RETURN u")  
	        .thenCompose(cursor ->  
	            cursor.listAsync(record -> User.from(record.get("u").asNode()))
	        )
	        .thenCompose(users ->  
	            session.closeAsync().thenApply(signal -> users)
	        )
	        .thenApply(Response::ok) 
	        .thenApply(ResponseBuilder::build);
	}
	
	@POST
	public CompletionStage<Response> create(User user) {
		logger.info(user.toString());
	    AsyncSession session = driver.asyncSession();
	    return session
	        .writeTransactionAsync(tx -> tx
	            .runAsync("MERGE (u:User {id: $id,"
	            		+ " type: $type,"
	            		+ " startTime: $startTime,"
	            		+ " endTime: $endTime,"
	            		+ " firstName: $firstName,"
	            		+ " middleName: $middleName,"
	            		+ " lastName: $lastName,"
	            		+ " homePhone: $homePhone,"
	            		+ " mobilephone: $mobilePhone,"
	            		+ " officePhone: $officePhone,"
	            		+ " notes: $notes,"
	            		+ " location: $location,"
	            		+ " email: $email,"
	            		+ " photo: $photo,"
	            		+ " employeeId: $employeeId,"
	            		+ " employeeTitle: $employeeTitle,"
	            		+ " employeeType: $employeeType,"
	            		+ " employeeStatus: $employeeStatus,"
	            		+ " employeeOrganization: $employeeOrganization}) RETURN u",
	            		Values.parameters("id", user.getId(),
	            				"type", user.getType(),
	            				"startTime", user.getStartTime(),
	            				"endTime", user.getEndTime(),
	            				"firstName", user.getFirstName(),
	            				"middleName", user.getMiddleName(),
	            				"lastName", user.getLastName(),
	            				"homePhone", user.getHomePhone(),
	            				"mobilePhone", user.getMobilePhone(),
	            				"officePhone", user.getMobilePhone(),
	            				"notes", user.getNotes(),
	            				"location", user.getLocation(),
	            				"email", user.getEmail(),
	            				"photo", user.getPhoto(),
	            				"employeeId", user.getEmployeeId(),
	            				"employeeTitle", user.getEmployeeTitle(),
	            				"employeeType", user.getEmployeeType(),
	            				"employeeStatus", user.getEmployeeStatus(),
	            				"employeeOrganization", user.getEmployeeOrganization()))
	            .thenCompose(fn -> fn.singleAsync())
	        )
	        .thenApply(record -> User.from(record.get("u").asNode()))
	        .thenCompose(persistedUser -> session.closeAsync().thenApply(signal -> persistedUser))
	        .thenApply(persistedUser -> Response
	            .created(URI.create("/neo/users/" + persistedUser.getId()))
	            .build()
	        );
	}
	
	@GET
	@Path("/{id}")
	public CompletionStage<Response> getSingle(@PathParam("id") String id) {
	    AsyncSession session = driver.asyncSession();
	    return session
	        .readTransactionAsync(tx -> tx
	            .runAsync("MATCH (u:User) WHERE u.id = $id RETURN u", Values.parameters("id", id))
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
	            return Response.ok(User.from(record.get("u").asNode())).build();
	        }
	    })
	    .thenCompose(response -> session.closeAsync().thenApply(signal -> response));
	}
}
