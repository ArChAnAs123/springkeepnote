package com.stackroute.keepnote.controller;

import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stackroute.keepnote.exception.ReminderNotCreatedException;
import com.stackroute.keepnote.exception.ReminderNotFoundException;
import com.stackroute.keepnote.model.Reminder;
import com.stackroute.keepnote.service.ReminderService;

/*
 * As in this assignment, we are working with creating RESTful web service, hence annotate
 * the class with @RestController annotation.A class annotated with @Controller annotation
 * has handler methods which returns a view. However, if we use @ResponseBody annotation along
 * with @Controller annotation, it will return the data directly in a serialized 
 * format. Starting from Spring 4 and above, we can use @RestController annotation which 
 * is equivalent to using @Controller and @ResposeBody annotation
 */
@RestController
@RequestMapping("/api/v1/reminder")
public class ReminderController {

	/*
	 * From the problem statement, we can understand that the application requires
	 * us to implement five functionalities regarding reminder. They are as
	 * following:
	 * 
	 * 1. Create a reminder 
	 * 2. Delete a reminder 
	 * 3. Update a reminder 
	 * 4. Get all reminders by userId 
	 * 5. Get a specific reminder by id.
	 * 
	 */

	/*
	 * Autowiring should be implemented for the ReminderService. (Use
	 * Constructor-based autowiring) Please note that we should not create any
	 * object using the new keyword
	 */
	
	private Log log = LogFactory.getLog(getClass());
	
	private ReminderService reminderService;

	public ReminderController(ReminderService reminderService) {
		this.reminderService = reminderService;
	}

	/*
	 * Define a handler method which will create a reminder by reading the
	 * Serialized reminder object from request body and save the reminder in
	 * database. Please note that the reminderId has to be unique. This handler
	 * method should return any one of the status messages basis on different
	 * situations: 
	 * 1. 201(CREATED - In case of successful creation of the reminder
	 * 2. 409(CONFLICT) - In case of duplicate reminder ID
	 *
	 * This handler method should map to the URL "/api/v1/reminder" using HTTP POST
	 * method".
	 */
	@PostMapping
	public ResponseEntity<?> createReminder(@RequestBody Reminder reminder) {
		log.info("createReminder : STARTED");
		try {
			reminder.setReminderCreationDate(new Date());
			if(reminderService.createReminder(reminder)!=null)
			{
				return new ResponseEntity<>(HttpStatus.CREATED);
			}
		} catch (ReminderNotCreatedException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		log.info("createReminder : ENDED");
		return new ResponseEntity<>( HttpStatus.CONFLICT);
	}

	/*
	 * Define a handler method which will delete a reminder from a database.
	 * 
	 * This handler method should return any one of the status messages basis on
	 * different situations: 
	 * 1. 200(OK) - If the reminder deleted successfully from database. 
	 * 2. 404(NOT FOUND) - If the reminder with specified reminderId is not found.
	 * 
	 * This handler method should map to the URL "/api/v1/reminder/{id}" using HTTP Delete
	 * method" where "id" should be replaced by a valid reminderId without {}
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteReminder(@PathVariable("id") String id) {
		log.info("deleteReminder : STARTED");
		try {
			if(reminderService.deleteReminder(id))
			{
				return new ResponseEntity<>(HttpStatus.OK);
			}
		} catch (ReminderNotFoundException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		log.info("deleteReminder : ENDED");
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	/*
	 * Define a handler method which will update a specific reminder by reading the
	 * Serialized object from request body and save the updated reminder details in
	 * a database. This handler method should return any one of the status messages
	 * basis on different situations: 
	 * 1. 200(OK) - If the reminder updated successfully. 
	 * 2. 404(NOT FOUND) - If the reminder with specified reminderId is not found. 
	 * 
	 * This handler method should map to the URL "/api/v1/reminder/{id}" using HTTP PUT
	 * method.
	 */
	@PutMapping("/{id}")
	public ResponseEntity<?> updateReminder(@RequestBody Reminder reminder,@PathVariable("id") String id) 
	{
		log.info("updateReminder : STARTED");
		try {	
				if(reminderService.updateReminder(reminder,id)!=null)
				{
					return new ResponseEntity<>(HttpStatus.OK);
				}
				else
				{
					return new ResponseEntity<>(HttpStatus.NOT_FOUND);
				}
		} catch (ReminderNotFoundException e) {
			e.printStackTrace();
		}
		log.info("updateReminder : ENDED");
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	

	/*
	 * Define a handler method which will show details of a specific reminder. This
	 * handler method should return any one of the status messages basis on
	 * different situations: 
	 * 1. 200(OK) - If the reminder found successfully. 
	 * 2. 404(NOT FOUND) - If the reminder with specified reminderId is not found. 
	 * 
	 * This handler method should map to the URL "/api/v1/reminder/{id}" using HTTP GET method
	 * where "id" should be replaced by a valid reminderId without {}
	 */
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getReminderById(@PathVariable("id") String id) {
		log.info("getReminderById : STARTED");
		try {
				Reminder reminder =reminderService.getReminderById(id);
				if(reminder!=null)
				{
					return new ResponseEntity<>(HttpStatus.OK);
					
				}
				
		} catch (ReminderNotFoundException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		log.info("getReminderById : ENDED");
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	/*
	 * Define a handler method which will get us the all reminders.
	 * This handler method should return any one of the status messages basis on
	 * different situations: 
	 * 1. 200(OK) - If the reminder found successfully. 
	 * 2. 404(NOT FOUND) - If the reminder with specified reminderId is not found.
	 * 
	 * This handler method should map to the URL "/api/v1/reminder" using HTTP GET method
	 */
	
	@GetMapping
	public ResponseEntity<?> getAllReminders() {
		log.info("getAllReminders : STARTED");
		try {
				List<Reminder> reminders =reminderService.getAllReminders();
				if(reminders!=null)
				{
					return new ResponseEntity<List<Reminder>>(reminders, HttpStatus.OK);
				}
				
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		log.info("getAllReminders : ENDED");
		return new ResponseEntity<>(HttpStatus.OK);
	}
}