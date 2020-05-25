package org.practice.mysql.repository;

import javax.enterprise.context.ApplicationScoped;

import org.practice.model.UserGroup;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class UserGroupRepository implements PanacheRepository<UserGroup> {

}
