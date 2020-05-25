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
import org.practice.model.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/neo/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountResource {
	
	Logger logger = LoggerFactory.getLogger(AccountResource.class);

	@Inject
	Driver driver;
	
	@Path("/all")
	@GET
	public CompletionStage<Response> get() {
	    AsyncSession session = driver.asyncSession(); 
	    return session
	        .runAsync("MATCH (a:Account) RETURN a")  
	        .thenCompose(cursor ->  
	            cursor.listAsync(record -> Account.from(record.get("a").asNode()))
	        )
	        .thenCompose(accounts ->  
	            session.closeAsync().thenApply(signal -> accounts)
	        )
	        .thenApply(Response::ok) 
	        .thenApply(ResponseBuilder::build);
	}
	
	@POST
	public CompletionStage<Response> create(Account account) {
		logger.info(account.toString());
	    AsyncSession session = driver.asyncSession();
	    return session
	        .writeTransactionAsync(tx -> tx
	            .runAsync("MERGE (a:Account {id: $id,"
	            		+ " type: $type,"
	            		+ " startTime: $startTime,"
	            		+ " endTime: $endTime,"
	            		+ " fqdn: $fqdn,"
	            		+ " accountName: $accountName,"
	            		+ " accountDomain: $accountDomain,"
	            		+ " accountStatus: $accountStatus}) RETURN a",
	            		Values.parameters("id", account.getId(),
	            				"type", account.getType(),
	            				"startTime", account.getStartTime(),
	            				"endTime", account.getEndTime(),
	            				"fqdn", account.getFqdn(),
	            				"accountName", account.getAccountName(),
	            				"accountDomain", account.getAccountDomain(),
	            				"accountStatus", account.getAccountStatus()))
	            .thenCompose(fn -> fn.singleAsync())
	        )
	        .thenApply(record -> Account.from(record.get("a").asNode()))
	        .thenCompose(persistedAccount -> session.closeAsync().thenApply(signal -> persistedAccount))
	        .thenApply(persistedAccount -> Response
	            .created(URI.create("/neo/accounts/" + persistedAccount.getId()))
	            .build()
	        );
	}
	
	@GET
	@Path("/{id}")
	public CompletionStage<Response> getSingle(@PathParam("id") String id) {
	    AsyncSession session = driver.asyncSession();
	    return session
	        .readTransactionAsync(tx -> tx
	            .runAsync("MATCH (a:Account) WHERE a.id = $id RETURN a", Values.parameters("id", id))
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
	            return Response.ok(Account.from(record.get("a").asNode())).build();
	        }
	    })
	    .thenCompose(response -> session.closeAsync().thenApply(signal -> response));
	}
}
