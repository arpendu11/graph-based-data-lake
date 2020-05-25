package org.practice.mysql.repository;

import javax.enterprise.context.ApplicationScoped;

import org.practice.model.User;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

}
