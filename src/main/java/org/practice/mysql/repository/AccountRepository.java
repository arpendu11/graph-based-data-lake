package org.practice.mysql.repository;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.practice.model.Account;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class AccountRepository implements PanacheRepository<Account> {	

	public List<Account> listAllAccounts() {
		return list("type", "account");		
	}
}
