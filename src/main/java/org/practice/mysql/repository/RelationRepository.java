package org.practice.mysql.repository;

import javax.enterprise.context.ApplicationScoped;

import org.practice.model.Relation;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class RelationRepository implements PanacheRepository<Relation> {

}
