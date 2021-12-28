package com.boot_project.Controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.boot_project.Dao.ContactRepository;
import com.boot_project.Dao.UserRepository;
import com.boot_project.Entities.Contact;
import com.boot_project.Entities.User;
import com.boot_project.Helper.Message;


@Controller
@RequestMapping("/user")
public class UserController
{
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@ModelAttribute
	public void CommnData(Model model,Principal principal)
	{
		String username = principal.getName();
		
		model.addAttribute("contact",new Contact());

		User user = userRepository.getUserByUserName(username);

		model.addAttribute("user", user);
		
	}
	
	//Dashboard home
	@RequestMapping("/index")
	public String Dashboard(Model model,Principal principal) 
	{
		
		String username = principal.getName();	
		
		User user = userRepository.getUserByUserName(username);
		
		model.addAttribute("user",user);	
		model.addAttribute("title","UserDashBoard");	
		
		return "Normal/user_dashboard";

	}
	
	//open add contact handler
	@GetMapping("/contactform")
	public String OpenAddContactForm(Model model)
	{
		
		model.addAttribute("title","Add Contact");
		model.addAttribute("contact",new  Contact());
		
		return "Normal/ContactForm";
		
	}

	
	//processing add contact form
	@PostMapping("/process-contact")
	public String processContactForm(@Valid @ModelAttribute Contact contact,BindingResult result, @RequestParam("profileimage") MultipartFile file,
			Principal principal,HttpSession session) {
		
			List<String> allContactsEmail = this.contactRepository.getAllContactsEmail();
			
			
			for (String contacts : allContactsEmail)
			{
				if (contact.getEmail().equals(contacts))
				{
					 session.setAttribute("message",new Message("Sorry! This Email is already exist.Try with another","danger"));	
					 return "Normal/ContactForm";
				}
				
			}
			
		try {

				String name = principal.getName();
				User user = this.userRepository.getUserByUserName(name);

			if (file.isEmpty()) 
				{
					System.out.println("file is empty");
					contact.setImage("contact.png");				

				} 
			else
				{
					// save file to folder
					
					contact.setImage(file.getOriginalFilename());
					File savefile = new ClassPathResource("static/Image").getFile();
					
					Path path = Paths.get(savefile.getAbsolutePath() + File.separator + file.getOriginalFilename());
	
					Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
					System.out.println("image is uploaded");

				}
			
			
			contact.setUsers(user);			
			
			user.getContacts().add(contact);	
			
			User save = this.userRepository.save(user);
		

			session.setAttribute("message",new Message("Your Contact is successfully Added !!   Add More....","success"));		
		} 
		catch (Exception e)
			{
				System.out.println("Error" + e.getMessage());
				e.printStackTrace();

			   session.setAttribute("message",new Message("Sorry!! Something went worng.........","danger"));	

			}
			return "Normal/ContactForm";
	}
	
	// Show contacts handler
	//per page 5[n]
	//current page 0[page]
	@GetMapping("/showcontact/{page}")
	public String ShowContacts( @PathVariable("page") Integer page,Model model,Principal principal)
	{
		model.addAttribute("title","ShowContactForm");
		
		String name = principal.getName();
		User user = this.userRepository.getUserByUserName(name);	
		
		Pageable pageable = PageRequest.of(page,4);
		
		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(),pageable);


		model.addAttribute("contact",contacts);
		
		model.addAttribute("currentpage",page);
		model.addAttribute("totalpages",contacts.getTotalPages());
		
		return "Normal/ShowContactForm";
		
	}
	
	//showing particular contact details
	@GetMapping("/ShowParticularDetails/{cid}")
	public String ShowParticularDetails(@PathVariable("cid") Integer cid,Model model,Principal principal)
	{
	    
	    Optional<Contact> contactOptional = this.contactRepository.findById(cid);
	 
	    Contact contact = contactOptional.get();
	    
	    String username = principal.getName();
	    User user = this.userRepository.getUserByUserName(username);
	    model.addAttribute("title","DetailsOfParticularUser");
	  

		if (user.getId() == contact.getUsers().getId()) {
			model.addAttribute("contact", contact);		
			return "Normal/ParticularDetailsOfUser";
		}
		else 
		{
		    model.addAttribute("title","Not Authorized");
			return "Normal/UnauthorizedUser";
		}		 
	}

  	//Delete Contact Handler
	@GetMapping("/delete/{cid}")
	public String DeleteContact(@PathVariable("cid" )Integer cid,Model model,HttpSession Session)
	{
		Optional<Contact> contactOptional = this.contactRepository.findById(cid);
		Contact contact = contactOptional.get();
		
		this.contactRepository.delete(contact);
		Session.setAttribute("message",new Message("Contact Deleted Successfully......","success"));
		
		return "redirect:/user/showcontact/0";
		
	}
	
	// Upadate form Handler
	@PostMapping("/update/{cid}")
	public String UpdateForm(@PathVariable("cid") Integer cid, Model model)
	{
		model.addAttribute("title","UpdateForm");
		Contact contact = this.contactRepository.findById(cid).get();
	
		model.addAttribute("contact",contact);
		
		return "Normal/UpdateForm";
		
	}
	
	//Update contact handler	
@RequestMapping( value = "/Process_Update",method = RequestMethod.POST)
	public String UpdateHandler(@ModelAttribute Contact contact, @RequestParam("profileimage") MultipartFile file,
			HttpSession session, Model model,Principal principal,@RequestParam("id") Integer id) {
						
						contact.setCid(id);
						Contact oldContact = this.contactRepository.findById(contact.getCid()).get();
		
				try 
				{
					if (! file.isEmpty())
							{
								//file work
								// rewrite
								//delete old photo
									File deleteFile = new ClassPathResource("static/Image").getFile();
									File file1= new File(deleteFile,oldContact.getImage());
									file1.delete();
						
						
								// update new photo
									File savefile = new ClassPathResource("static/Image").getFile();
									
									Path path = Paths.get(savefile.getAbsolutePath() + File.separator + file.getOriginalFilename());
			
									Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
									contact.setImage(file.getOriginalFilename());
								
					}
					else
							{
								contact.setImage(oldContact.getImage());
							}
				
					User user = this.userRepository.getUserByUserName(principal.getName());
					contact.setUsers(user);
					
					Contact contact2 = this.contactRepository.save(contact);
					
						
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
	
		return "redirect:/user/ShowParticularDetails/"+contact.getCid();
	
	}

// Users profile

	@GetMapping("/profile")
	public String UserProfile(Model model) {
		model.addAttribute("title", "UserProfile");
		return "Normal/UserProfile";

	}

//open setting handler
	@GetMapping("/setting")
	public String SettingHandler(Model model) {

		model.addAttribute("title", "ChangePassword");
		return "Normal/Setting";

	}

	@PostMapping("/process-setting")
	public String ProcessSetting(@RequestParam("oldpassword") String oldpassword,
			@RequestParam("newpassword") String newpassword, Principal principal,HttpSession session) 
	{
		
		String name = principal.getName();
		User currentUser = this.userRepository.getUserByUserName(name);

		if (this.bCryptPasswordEncoder.matches(oldpassword, currentUser.getPassword())) 
		{
			//change the password
			currentUser.setPassword(this.bCryptPasswordEncoder.encode(newpassword));
			this.userRepository.save(currentUser);
			session.setAttribute("message",new Message("Your Password is Successfully Changed......... ","success"));
		}
		else 
		{
			session.setAttribute("message",new Message("Please Enter Your Correct Old Password ","danger"));
			return "redirect:/user/setting";
		}	

		return "redirect:/user/index";

	}

}


