package com.stackroute.keepnote.controller;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stackroute.keepnote.exception.UserAlreadyExistsException;
import com.stackroute.keepnote.model.User;
import com.stackroute.keepnote.service.UserAuthenticationService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/*
 * As in this assignment, we are working on creating RESTful web service, hence annotate
 * the class with @RestController annotation. A class annotated with the @Controller annotation
 * has handler methods which return a view. However, if we use @ResponseBody annotation along
 * with @Controller annotation, it will return the data directly in a serialized 
 * format. Starting from Spring 4 and above, we can use @RestController annotation which 
 * is equivalent to using @Controller and @ResposeBody annotation
 */
@CrossOrigin(origins="http://localhost:4200")
@RestController
@Api
@RequestMapping
public class UserAuthenticationController {
	
	static final long EXPIRATION_TIME= 3000000;
	Map<String, String> map = new HashMap<>();
	Map<String, Boolean> map1 = new HashMap<>();

    /*
	 * Autowiring should be implemented for the UserAuthenticationService. (Use Constructor-based
	 * autowiring) Please note that we should not create an object using the new
	 * keyword
	 */
	
	@Autowired
	private UserAuthenticationService authenticationService;
	

    public UserAuthenticationController(UserAuthenticationService authicationService) {
    	this.authenticationService=authicationService;
	}

   /*
	 * Define a handler method which will create a specific user by reading the
	 * Serialized object from request body and save the user details in the
	 * database. This handler method should return any one of the status messages
	 * basis on different situations:
	 * 1. 201(CREATED) - If the user created successfully. 
	 * 2. 409(CONFLICT) - If the userId conflicts with any existing user
	 * 
	 * This handler method should map to the URL "/api/v1/auth/register" using HTTP POST method
	 */
    @ApiOperation(value="User Registration")
    @PostMapping("/api/v1/auth/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
    	
    	try {
			boolean flag = authenticationService.saveUser(user);
			
			return new ResponseEntity<>(flag, HttpStatus.CREATED);
		} catch (UserAlreadyExistsException e) {
			return new ResponseEntity<>( false, HttpStatus.CONFLICT);
		}
    	
    	
    }

    
    @ApiOperation(value="User Authentication Check")
    @PostMapping("/api/v1/auth/isAuthenticated")
    public ResponseEntity<?> authCheck(@RequestHeader(value="authorization") String authorization) throws Exception{
    	String isAuthenticated = "false";
    	User user = new User();
    	try {
    		if (authorization == null || !authorization.startsWith("Bearer ")) {
				throw new ServletException("Missing or invalid Authorization header");
			}
			final String token = authorization.substring(7);
			final Claims claims = Jwts.parser()
									  .setSigningKey("secretkey")
									  .parseClaimsJws(token)
									  .getBody();
			map1.clear();
    		map1.put("isAuthenticated", true);
    		return new ResponseEntity<> (map1, HttpStatus.OK);
		} catch (Exception e) {
			map1.clear();
    		map1.put("isAuthenticated", false);
			return new ResponseEntity<> (map1, HttpStatus.OK);
		}
    	
    }


	/* Define a handler method which will authenticate a user by reading the Serialized user
	 * object from request body containing the username and password. The username and password should be validated 
	 * before proceeding ahead with JWT token generation. The user credentials will be validated against the database entries. 
	 * The error should be return if validation is not successful. If credentials are validated successfully, then JWT
	 * token will be generated. The token should be returned back to the caller along with the API response.
	 * This handler method should return any one of the status messages basis on different
	 * situations:
	 * 1. 200(OK) - If login is successful
	 * 2. 401(UNAUTHORIZED) - If login is not successful
	 * 
	 * This handler method should map to the URL "/api/v1/auth/login" using HTTP POST method
	*/

    @ApiOperation(value="User Login")
    @PostMapping("/api/v1/auth/login")
    public ResponseEntity<?> login(@RequestBody User user) {
    	
    	String jwtToken = "";
    	System.out.println("User Data :: " + user);
    	try {
			
    		jwtToken = getToken(user.getUserId(), user.getUserPassword());
    		map.clear();
    		map.put("message", "user successfully logged in!");
    		map.put("token", jwtToken);
		} catch (Exception e) {
			e.printStackTrace();
			map.clear();
    		map.put("message", e.getMessage());
    		map.put("token", null);
    		return new ResponseEntity<> (map, HttpStatus.UNAUTHORIZED);
		}
    	
    	return new ResponseEntity<> (map, HttpStatus.OK);
    	
    }





// Generate JWT token
	public String getToken(String username, String password) throws Exception {
		
		String jwtToken = null;
		if(username== null && password==null) {
			throw new ServletException("Please enter username and password!");
		}
		
		try {
			User user = authenticationService.findByUserIdAndPassword(username, password);
			
			if(user== null) {			
				throw new ServletException("Invalid Credentials!");
			}
			
			jwtToken = Jwts.builder()
					.setSubject(username)
					.setIssuedAt(new Date()) 
					.setExpiration(new Date(System.currentTimeMillis()+EXPIRATION_TIME))
					.signWith(SignatureAlgorithm.HS256, "secretkey")
					.compact();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("jwtToken :: " +jwtToken);	
			
        return jwtToken;
        
        
}


}