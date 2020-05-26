package org.practice.mysql.repository;

import javax.enterprise.context.ApplicationScoped;

import org.practice.model.Application;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class ApplicationRepository implements PanacheRepository<Application>  {

}
