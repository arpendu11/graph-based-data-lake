package org.practice.mysql.repository;

import javax.enterprise.context.ApplicationScoped;

import org.practice.model.AccessRight;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class AccessRightRepository implements PanacheRepository<AccessRight> {

}
