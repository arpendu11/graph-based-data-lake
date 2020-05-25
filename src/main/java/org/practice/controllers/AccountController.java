package org.practice.controllers;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.practice.model.Account;
import org.practice.services.AccountService;

import io.quarkus.runtime.annotations.RegisterForReflection;

@Path("/accounts")
@ApplicationScoped
@RegisterForReflection
public class AccountController {
	
	@Inject
	AccountService accountService;
	
	@Path("/all")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllAccounts() {
		List<Account> accounts = accountService.listAll();
		return Response.ok(accounts).build();
	}

}
