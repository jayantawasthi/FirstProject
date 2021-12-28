package com.boot_project.Entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Table(name = "CONTACT")
public class Contact {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int cid;
	
	@NotBlank(message = "Name field is Require")
	@Size(min = 2,max = 20,message = "FirstName should be between 2 to 30 characters are allowed!!")
	private String name;
	
	@NotBlank(message = "Name field is Require")
	@Size(min = 2,max = 20,message = "Second Name should be between 2 to 30 characters are allowed!!")
	private String secondname;
	
	@Size(min = 2,max = 20,message = "Work should be between 2 to 30 characters are allowed!!")
	private String work;
	
	@NotBlank(message = "contact number must be of  10 digit ")
	@Pattern(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$",message = "Invalid Email !! please type a valid email.")
	private String email;
	
	@NotBlank(message = "contact number  can not be  empty")
	@Pattern(regexp = "^\\+?(?:977)?[ -]?(?:(?:(?:98|97)-?\\d{8})|(?:01-?\\d{7}))$",message = "wrong mobile number or number must be of 10 digit ")
	private String phone;
	
	private String image;
	
	@Column(length = 1000)
	private String description;
	
	@ManyToOne()
	private User users;

	public User getUsers() {
		return users;
	}

	public void setUsers(User users) {
		this.users = users;
	}

	

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSecondname() {
		return secondname;
	}

	public void setSecondname(String secondname) {
		this.secondname = secondname;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}


	public String getImage() {
		return image;
	}

	@Override
	public String toString() {
		return "Contact [cid=" + cid + ", name=" + name + ", secondname=" + secondname + ", work=" + work + ", email="
				+ email + ", phone=" + phone + ", image=" + image + ", description=" + description + ", users=" + users
				+ "]";
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
