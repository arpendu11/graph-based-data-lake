package org.practice.services;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.practice.model.Relation;
import org.practice.mysql.repository.RelationRepository;
import org.practice.neo4j.resources.RelationResource;

import com.google.gson.Gson;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;

public class RelationService {

	@Inject
	RelationRepository relationRepo;
	
	@Inject
	RelationResource relationResource;
	
	public List<Relation> getAllRelations() {
		return relationRepo.listAll();
	}
	
	@Outgoing("entity-relation")
	public Flowable<String> stream() {
		Gson gson = new Gson();
		return Flowable
				.interval(1, 10, TimeUnit.MINUTES)
				.map(new Function<Long, List<Relation>>() {
					@Override
					public List<Relation> apply(Long t) throws Exception {
						return relationRepo.listAll();
					}
				})
				.flatMap(list -> Flowable.fromIterable(list))
				.map(a -> gson.toJson(a));			
	}
	
	@Incoming("entity-relation-delta")
	public void saveToNeo(String json) {
		Gson gson = new Gson();
		Relation relation = gson.fromJson(json, Relation.class);
		relationResource.create(relation);
	}
}
