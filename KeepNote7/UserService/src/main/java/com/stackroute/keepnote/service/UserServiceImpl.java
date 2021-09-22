package com.stackroute.keepnote.service;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stackroute.keepnote.exceptions.UserAlreadyExistsException;
import com.stackroute.keepnote.exceptions.UserNotFoundException;
import com.stackroute.keepnote.model.User;
import com.stackroute.keepnote.repository.UserRepository;

/*
* Service classes are used here to implement additional business logic/validation 
* This class has to be annotated with @Service annotation.
* @Service - It is a specialization of the component annotation. It doesn't currently 
* provide any additional behavior over the @Component annotation, but it's a good idea 
* to use @Service over @Component in service-layer classes because it specifies intent 
* better. Additionally, tool support and additional behavior might rely on it in the 
* future.
* */

@Service
public class UserServiceImpl implements UserService {

	/*
	 * Autowiring should be implemented for the UserRepository. (Use
	 * Constructor-based autowiring) Please note that we should not create any
	 * object using the new keyword.
	 */
	@Autowired
	private UserRepository userRepository;
	public UserServiceImpl(UserRepository userRepository) {
		
		this.userRepository=userRepository;
	}


	/*
	 * This method should be used to save a new user.Call the corresponding method
	 * of Respository interface.
	 */

	
	public User registerUser(User user) throws UserAlreadyExistsException {
		User usr = null;
		
		if(user!= null 
				&& user.getUserId()!= null ) {
			user.setUserAddedDate(new Date());
			
			usr = userRepository.saveAndFlush(user);
			if(usr!= null ) {
				return usr;
			}
			
		}
				
		throw new UserAlreadyExistsException("User already exists");
	}

	/*
	 * This method should be used to update a existing user.Call the corresponding
	 * method of Respository interface.
	 */

	public User updateUser(String userId,User user) throws UserNotFoundException {

		if(userId!= null) {
			User usr = getUserById(userId);
			
			if(usr!= null && user!= null) {
				user.setUserId(usr.getUserId());
				userRepository.save(user);
			}else {
				throw new UserNotFoundException("User Not Found");
			}
		}

		return user;
	}

	/*
	 * This method should be used to delete an existing user. Call the corresponding
	 * method of Respository interface.
	 */

	public boolean deleteUser(String userId) throws UserNotFoundException {

		if(userId!= null) {
			User usr = getUserById(userId);
			
			if(usr!= null) {
				userRepository.delete(usr);
				return true;
			}else {
				throw new UserNotFoundException("User Not Found");
			}
		}

		return false;
	}

	/*
	 * This method should be used to get a user by userId.Call the corresponding
	 * method of Respository interface.
	 */

	public User getUserById(String userId) throws UserNotFoundException {
		Optional<User> user = null;
		if(userId!= null) {
			user = userRepository.findById(userId);
			if(user!= null && user.isPresent()) {
				return user.get();
			}
		}

		throw new UserNotFoundException("User not found");
	}

}