package org.practice.controllers;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.practice.model.User;
import org.practice.services.UserService;

import io.quarkus.runtime.annotations.RegisterForReflection;

@Path("/users")
@ApplicationScoped
@RegisterForReflection
public class UserController {
	
	@Inject
	UserService userService;
	
	@Path("/all")
	@GET
	@Counted(name = "performedChecks", description = "How many checks have been performed.")
    @Timed(name = "checksTimer", description = "A measure of how long it takes to execute the query", unit = MetricUnits.MILLISECONDS)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllUsers() {
		List<User> users = userService.getAllUsers();
		return Response.ok(users).build();
	}
	
}
