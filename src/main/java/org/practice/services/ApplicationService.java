package org.practice.services;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.practice.model.Application;
import org.practice.mysql.repository.ApplicationRepository;
import org.practice.neo4j.resources.ApplicationResource;

import com.google.gson.Gson;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;

public class ApplicationService {

	@Inject
	ApplicationRepository applicationRepo;
	
	@Inject
	ApplicationResource applicationResource;
	
	public List<Application> listAll() {
		return applicationRepo.listAll();
	}
	
	@Outgoing("entity-application")
	public Flowable<String> stream() {
		Gson gson = new Gson();
		return Flowable
				.interval(20, 360, TimeUnit.MINUTES)
				.map(new Function<Long, List<Application>>() {
					@Override
					public List<Application> apply(Long t) throws Exception {
						return applicationRepo.listAll();
					}
				})
				.flatMap(list -> Flowable.fromIterable(list))
				.map(a -> gson.toJson(a));			
	}
	
	@Incoming("entity-application-delta")
	public void saveToNeo(String json) {
		Gson gson = new Gson();
		Application application = gson.fromJson(json, Application.class);
		applicationResource.create(application);
	}
}
