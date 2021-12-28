package com.boot_project.Dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.boot_project.Entities.Contact;
import com.boot_project.Entities.User;

public interface ContactRepository  extends JpaRepository<Contact,Integer>{
	
	//currentpage-page
	//contact per page
	@Query("from Contact as c where c.users.id=:userid")	
	public Page<Contact> findContactsByUser(@Param("userid") int userid,Pageable pePageable);
	
	@Query("select email from Contact")
	public List<String> getAllContactsEmail();
	
	
	//public List<Contact> findAllContactsByUserId(int i);
	
	//search
	//public List<Contact> findByNameContainingAndUser(String name,User user);

}
