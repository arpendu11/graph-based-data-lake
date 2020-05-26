package org.practice.services;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.practice.model.Account;
import org.practice.mysql.repository.AccountRepository;
import org.practice.neo4j.resources.AccountResource;

import com.google.gson.Gson;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;

@ApplicationScoped
public class AccountService {

	@Inject
	AccountRepository accountRepo;
	
	@Inject
	AccountResource accountResource;
	
	public List<Account> listAll() {
		return accountRepo.listAllAccounts();
	}
	
	@Outgoing("entity-account")
	public Flowable<String> stream() {
		Gson gson = new Gson();
		return Flowable
				.interval(5, 360, TimeUnit.MINUTES)
				.map(new Function<Long, List<Account>>() {
					@Override
					public List<Account> apply(Long t) throws Exception {
						return accountRepo.listAll();
					}
				})
				.flatMap(list -> Flowable.fromIterable(list))
				.map(a -> gson.toJson(a));			
	}
	
	@Incoming("entity-account-delta")
	public void saveToNeo(String json) {
		Gson gson = new Gson();
		Account account = gson.fromJson(json, Account.class);
		accountResource.create(account);
	}
}
