package org.practice.services;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.practice.model.AccessRight;
import org.practice.mysql.repository.AccessRightRepository;
import org.practice.neo4j.resources.AccessRightResource;

import com.google.gson.Gson;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;

public class AccessRightService {

	@Inject
	AccessRightRepository accessRightRepo;
	
	@Inject
	AccessRightResource accessRightResource;
	
	public List<AccessRight> listAll() {
		return accessRightRepo.listAll();
	}
	
	@Outgoing("entity-access-right")
	public Flowable<String> stream() {
		Gson gson = new Gson();
		return Flowable
				.interval(10, 360, TimeUnit.MINUTES)
				.map(new Function<Long, List<AccessRight>>() {
					@Override
					public List<AccessRight> apply(Long t) throws Exception {
						return accessRightRepo.listAll();
					}
				})
				.flatMap(list -> Flowable.fromIterable(list))
				.map(a -> gson.toJson(a));			
	}
	
	@Incoming("entity-access-right-delta")
	public void saveToNeo(String json) {
		Gson gson = new Gson();
		AccessRight accessRight = gson.fromJson(json, AccessRight.class);
		accessRightResource.create(accessRight);
	}
}
