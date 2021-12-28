package com.boot_project.Controller_ForgetPass;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.boot_project.Dao.UserRepository;
import com.boot_project.Entities.User;
import com.boot_project.Service.EmailService;

@Controller
public class ForgotController {
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	Random random=new Random(1000);
	
	@GetMapping("/forgot")
	public String OpenEmailForm()
	{
		return "ForgotPassForm";
		
	}

	@PostMapping("/forgotprocess")
	public String ProcessEmailForm(@RequestParam("email") String email,HttpSession session)
	{
		System.out.println(email);
		
	
		
		int otp = random.nextInt(99999);
		System.out.println("otp:"+otp);
		
	//Creating message,Subject and Email	
		String subject="OTP FROM Smart Contact Manager";
		String message="This is Your PinCode=>"+otp;
		String to=email;
		
		boolean flag = this.emailService.SendEmail(subject, message, to);
		
		if (flag) 
		{
			session.setAttribute("pincode", otp);
			session.setAttribute("email", email);
			return "verify";
			
		}
		else 
		{
			session.setAttribute("error", "Please Check Your Email");
			return "ForgotPassForm";
			
		}		
		
	}
	
	@PostMapping("/verifyPincode")
	public String PincodeVerifyContoller(@RequestParam("pincode") int pincode,HttpSession session)
	{
		
		int sessionPincode = (int) session.getAttribute("pincode");
		String email = (String) session.getAttribute("email");
		
		if (pincode==sessionPincode)
		{
			User user = this.userRepository.getUserByUserName(email);
			if (user==null)
			{
				session.setAttribute("message", "User With This Email Id  Does Not Exist");
				return "ForgotPassForm";
			}
			else 
			{						
				return "ChangePassword";
			}
		}
		else 
		{
			session.setAttribute("error", "You Entered wrong PinCode ");
			return "Verify";
		}			 		
	}
	
	
	//Change password with new one
	
	@PostMapping("/Change-Password")
	public String changepasscontroller(@RequestParam("newpass") String updatepass,@RequestParam("rnewpass") String rnewpass,HttpSession session)
	{
		if (updatepass.equals(rnewpass))
		{
			String email=(String) session.getAttribute("email");
			User user = this.userRepository.getUserByUserName(email);
			user.setPassword(this.bCryptPasswordEncoder.encode(updatepass));
			
			this.userRepository.save(user);
			System.out.println("password successfully changed");
			
			return "redirect:/signin?change=Password Changed Successfully.......";
			
		}
		else 
		{
			session.setAttribute("message", "The Password you Entered Not Match!! Please Try Again");
			return "ChangePassword";
		}
		
	}
	

}
