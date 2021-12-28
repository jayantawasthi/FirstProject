package com.boot_project.Controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.boot_project.Dao.UserRepository;
import com.boot_project.Entities.User;
import com.boot_project.Helper.Message;

@Controller
public class HomeController {

	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private UserRepository userRepository;

	@GetMapping("/")
	public String Home(Model model) {
		model.addAttribute("title","HomePage");
		return "home";
	}

	@GetMapping("/about")
	public String About() {

		return "about";
	}

	@GetMapping("/signup")
	public String Signup(Model m) {

		m.addAttribute("title", " SignUP page");
		m.addAttribute("user", new User());
		return "signup";
	}

	// handller for registering user
	@PostMapping("/do_register")
	public String RegisterUser(@Valid @ModelAttribute("user") User user,BindingResult result ,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, 
			Model m,HttpSession session,@RequestParam("profileimage") MultipartFile file) {
		
		List<String> allUserEmail = this.userRepository.getAllUserEmail();
		allUserEmail.forEach(a->System.out.println(a));
		for (String string : allUserEmail) 
		{
			if (user.getEmail().equals(string))
			{
				session.setAttribute("message", new Message("This Email is already exist!! Please try with another","alert-danger"));
				return "signup";				
			}
			
		}
		
		
		try 
		{
			if(!agreement)
			{
				System.out.println("you have not agreed terms and conditions");
				throw new Exception("you have not agreed terms and conditions");
				
			}
			
			if(result.hasErrors())
			{
				System.out.println("Error:"+result.toString());
				m.addAttribute("user",user); 
				return "signup";
			}
			user.setRole("ROLE_USER");
			user.setEnabled(true);

// insert user Profile picture			
			if (file.isEmpty()) {
				System.out.println("file is empty");			
				user.setImageurl("user.png");			
			} 
			else{
				// save file to folder				
				user.setImageurl(file.getOriginalFilename());
				File saveUserPic= new ClassPathResource("static/UserProfileImage").getFile();
				
				Path path = Paths.get(saveUserPic.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("image is uploaded");

			}
// end of insert user profile picture
			
			//suser.setImageurl("default.png");
			user.setPassword(passwordEncoder.encode( user.getPassword() ));
				
//				System.out.println("Agreement"+agreement);
//				System.out.println("User"+user);
				User user1 = this.userRepository.save(user);
				m.addAttribute("user",new User());
				m.addAttribute("title","Register page");
				session.setAttribute("message",new Message("Successfully Registered!!!", "alert-success"));
				return "signup";
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			session.setAttribute("message", new Message("Somthing went Wrong!!!"+e.getMessage(),"alert-danger"));
			return "signup";
		}
	}
 
	// hadler for loginpage
	
	@GetMapping("/signin")
	public  String customelogin(Model model)
	{
		model.addAttribute("title","SignIn page");
		return "Login";
		
	}
}
