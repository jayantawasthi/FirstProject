package com.boot_project.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.boot_project.Dao.ContactRepository;
import com.boot_project.Dao.UserRepository;

@RestController
public class SearchController 
{
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContactRepository  contactRepository;
	
//search handler
//@GetMapping("/search/{query}")
//	public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal){
//		
//		System.out.println(query);
//
//		
//		List<Contact> contacts = this.contactRepository.findByNameContainingAndUser(query, this.userRepository.getUserByUserName(principal.getName()));
//		return ResponseEntity.ok(contacts);
//		
//	}

}
