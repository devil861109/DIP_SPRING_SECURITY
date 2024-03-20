package edu.unam.springsecurity.auth.controller;

import edu.unam.springsecurity.auth.dto.UserInfoDTO;
import edu.unam.springsecurity.auth.dto.UserInfoRoleDTO;
import edu.unam.springsecurity.auth.exception.UserInfoNotFoundException;
import edu.unam.springsecurity.auth.service.UserInfoService;
import edu.unam.springsecurity.security.jwt.JWTTokenProvider;
import edu.unam.springsecurity.security.request.JwtRequest;
import edu.unam.springsecurity.security.request.LoginUserRequest;
import edu.unam.springsecurity.security.service.UserDetailsServiceImpl;
import edu.unam.springsecurity.system.service.AdminService;
import edu.unam.springsecurity.system.service.HomeService;
import edu.unam.springsecurity.system.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Controller
public class HomeController {
	private final HomeService homeService;
	private final UserService userService;
	private final AdminService adminService;
	private final UserInfoService userInfoService;
	private final AuthenticationManager authenticationManager;
	private final JWTTokenProvider jwtTokenProvider;
	private final UserDetailsServiceImpl userDetailsService;

	// Controller Injection
	public HomeController(HomeService homeService, UserService userService, AdminService adminService, UserInfoService userInfoService,
						  AuthenticationManager authenticationManager, JWTTokenProvider jwtTokenProvider, UserDetailsServiceImpl userDetailsService) {
		this.homeService = homeService;
		this.userService = userService;
		this.adminService = adminService;
		this.userInfoService = userInfoService;
		this.authenticationManager = authenticationManager;
		this.jwtTokenProvider = jwtTokenProvider;
		this.userDetailsService = userDetailsService;
	}

	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("text", homeService.getText());
		return "index";
	}

	@GetMapping("/index")
	public String index() {
		return "redirect:/";
	}

	@GetMapping("/user")
	@PreAuthorize("hasRole('USER')")
	public String user(Model model) {
		model.addAttribute("text", userService.getText());
		return "user";
	}

	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public String admin(Model model) {
		model.addAttribute("text", adminService.getText());
		return "admin";
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@PostMapping("/login_success_handler")
	public String loginSuccessHandler() {
		System.out.println("Logging user login success...");
		return "index";
	}

	@PostMapping("/login_failure_handler")
	public String loginFailureHandler() {
		System.out.println("Login failure handler....");
		return "login";
	}

	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		model.addAttribute("user", new UserInfoDTO());
		return "signup_form";
	}

	@PostMapping("/process_register")
	public String processRegister(UserInfoDTO user) throws UserInfoNotFoundException {
		user.setUseIdStatus(1);
		Set<UserInfoRoleDTO> roles = new HashSet<>();
		roles.add(UserInfoRoleDTO.builder().usrId(1L).build());
		user.setUseInfoRoles(roles);
		user.setUseCreatedBy(1L);
		user.setUseModifiedBy(1L);
		userInfoService.save(user);
		return "register_success";
	}

	@PostMapping("/token")
	public String createAuthenticationToken(Model model, HttpSession session,
											@ModelAttribute LoginUserRequest loginUserRequest, HttpServletResponse res) throws Exception {
		log.info("LoginUserRequest {}", loginUserRequest);
		try {
			UserInfoDTO user = userInfoService.findByUseEmail(loginUserRequest.getUsername());
			if (user.getUseIdStatus() == 1) {
				Authentication authentication = authenticate(loginUserRequest.getUsername(),
						loginUserRequest.getPassword());
				log.info("authentication {}", authentication);
				String jwtToken = jwtTokenProvider.generateJwtToken(authentication, user);
				log.info("jwtToken {}", jwtToken);
				JwtRequest jwtRequest = new JwtRequest(jwtToken, user.getUseId(), user.getUseEmail(),
						jwtTokenProvider.getExpiryDuration(), authentication.getAuthorities());
				log.info("jwtRequest {}", jwtRequest);
				Cookie cookie = new Cookie("token",jwtToken);
				cookie.setMaxAge(Integer.MAX_VALUE);
				res.addCookie(cookie);
				session.setAttribute("msg","Login OK!");
			}
		} catch (UsernameNotFoundException | BadCredentialsException e) {
			session.setAttribute("msg","Bad Credentials");
			return "redirect:/login";
		}
		return "redirect:/index";
	}

	private Authentication authenticate(String username, String password) throws Exception {
		try {
			return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
	}
}
