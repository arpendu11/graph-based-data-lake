package org.practice.services;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.practice.model.UserGroup;
import org.practice.mysql.repository.UserGroupRepository;
import org.practice.neo4j.resources.UserGroupResource;

import com.google.gson.Gson;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;

public class UserGroupService {

	@Inject
	UserGroupRepository userGroupRepo;
	
	@Inject
	UserGroupResource userGroupResource;
	
	public List<UserGroup> listAll() {
		return userGroupRepo.listAll();
	}
	
	@Outgoing("entity-user-group")
	public Flowable<String> stream() {
		Gson gson = new Gson();
		return Flowable
				.interval(15, 360, TimeUnit.MINUTES)
				.map(new Function<Long, List<UserGroup>>() {
					@Override
					public List<UserGroup> apply(Long t) throws Exception {
						return userGroupRepo.listAll();
					}
				})
				.flatMap(list -> Flowable.fromIterable(list))
				.map(a -> gson.toJson(a));			
	}
	
	@Incoming("entity-user-group-delta")
	public void saveToNeo(String json) {
		Gson gson = new Gson();
		UserGroup userGroup = gson.fromJson(json, UserGroup.class);
		userGroupResource.create(userGroup);
	}
}
