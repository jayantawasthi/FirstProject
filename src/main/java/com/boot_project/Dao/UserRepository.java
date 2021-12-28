package com.boot_project.Dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.boot_project.Entities.User;
@Service
public interface UserRepository  extends  JpaRepository<User,Integer>{

	@Query("select u from User u where u.email=:email")
	public User getUserByUserName(@Param("email") String email);
	
	@Query("select email from User")
	public List<String> getAllUserEmail();
	
	
}
