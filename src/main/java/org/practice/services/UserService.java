package org.practice.services;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.practice.model.User;
import org.practice.mysql.repository.UserRepository;
import org.practice.neo4j.resources.UserResource;

import com.google.gson.Gson;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;

@ApplicationScoped
public class UserService {

	@Inject
	UserRepository userRepo;
	
	@Inject
	UserResource userResource;

	public List<User> getAllUsers() {
		return userRepo.listAll();
	}

	@Outgoing("entity-identity")
	public Flowable<String> stream() {
		Gson gson = new Gson();
		return Flowable
				.interval(1, 360, TimeUnit.MINUTES)
				.map(new Function<Long, List<User>>() {
					@Override
					public List<User> apply(Long t) throws Exception {
						return userRepo.listAll();
					}
				})
				.flatMap(list -> Flowable.fromIterable(list))
				.map(u -> gson.toJson(u));			
	}
	
	@Incoming("entity-identity-delta")
	public void saveToNeo(String json) {
		Gson gson = new Gson();
		User user = gson.fromJson(json, User.class);
		userResource.create(user);
	}
}
