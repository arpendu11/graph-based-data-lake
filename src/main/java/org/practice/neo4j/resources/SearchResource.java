package org.practice.neo4j.resources;

import java.util.concurrent.CompletionStage;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;
import org.neo4j.driver.Values;
import org.neo4j.driver.async.AsyncSession;
import org.practice.model.SearchCriteria;
import org.practice.model.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/search")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SearchResource implements AutoCloseable {
	
	Logger logger = LoggerFactory.getLogger(SearchResource.class);

	@Inject
	Driver driver;
	
	public void createNodeIndex() {
		try ( Session session = driver.session() )
        {
            session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                	Result checkIndex = tx.run("call db.indexes() YIELD name where name = \"SearchString\" RETURN name");
                    if (!checkIndex.hasNext()) {
                		logger.info("Creating a search index on Nodes !!");
                    	tx.run("CALL db.index.fulltext.createNodeIndex(\"SearchString\",[\"User\", \"UserGroup\","
            	        		+ "\"AccessRight\", \"Account\"],[\"accountDomain\", \"accountStatus\", \"accountName\""
            	        		+ ", \"employeeOrganization\", \"employeeStatus\", \"entitlementDescription\", \"entitlementName\""
            	        		+ ", \"firstName\", \"fqdn\", \"identityGroupDescription\", \"identityGroupName\", \"lastName\""
            	        		+ ", \"location\", \"email\", \"middleName\", \"notes\"], {analyzer: \"url_or_email\", "
            	        		+ "eventually_consistent: \"true\"})");
                    }
                    return "executed";
                }
            } );
        }
	}
	
	@POST
	public CompletionStage<Response> create(SearchCriteria searchCriteria) {
		createNodeIndex();
		logger.info(searchCriteria.toString());
	    AsyncSession session = driver.asyncSession();
	    return session
		        .runAsync("CALL db.index.fulltext.queryNodes(\"SearchString\", $term) "
		        		+ "YIELD node, score RETURN node order by score DESC", Values.parameters("term", searchCriteria.getTerm()))  
		        .thenCompose(cursor ->  
		            cursor.listAsync(record -> SearchResult.from(record.get("node").asNode()))
		        )
		        .thenCompose(searchResult ->  
		            session.closeAsync().thenApply(signal -> searchResult)
		        )
		        .thenApply(Response::ok) 
		        .thenApply(ResponseBuilder::build);
	}

	@Override
	public void close() throws Exception {		
		driver.close();
	}

}
