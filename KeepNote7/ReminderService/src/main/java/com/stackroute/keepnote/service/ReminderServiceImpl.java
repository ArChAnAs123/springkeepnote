package com.stackroute.keepnote.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stackroute.keepnote.exception.ReminderNotCreatedException;
import com.stackroute.keepnote.exception.ReminderNotFoundException;
import com.stackroute.keepnote.model.Reminder;
import com.stackroute.keepnote.repository.ReminderRepository;

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
public class ReminderServiceImpl implements ReminderService {

	/*
	 * Autowiring should be implemented for the ReminderRepository. (Use
	 * Constructor-based autowiring) Please note that we should not create any
	 * object using the new keyword.
	 */
	
	@Autowired
	private ReminderRepository reminderRepository;
	
	

	public ReminderServiceImpl(ReminderRepository reminderRepository) {
		this.reminderRepository=reminderRepository;
	}

	/*
	 * This method should be used to save a new reminder.Call the corresponding
	 * method of Respository interface.
	 */
	public Reminder createReminder(Reminder reminder) throws ReminderNotCreatedException {
		
		if(reminder!= null && reminder.getReminderId()!= null) {
			Reminder reminder2 = reminderRepository.insert(reminder);
			//System.out.println(reminder2);
			if(reminder2!= null) {
				return reminder2;
			}
		}

		throw new ReminderNotCreatedException("Reminder Not Created!");
		
	}

	/*
	 * This method should be used to delete an existing reminder.Call the
	 * corresponding method of Respository interface.
	 */
	public boolean deleteReminder(String reminderId) throws ReminderNotFoundException {
		
		if(reminderId!= null) {
			Reminder rem = getReminderById(reminderId);
			
			if(rem!= null) {
				reminderRepository.delete(rem);
				return true;
			}else {
				throw new ReminderNotFoundException("Reminder Not Found");
			}
		}

		return false;
	}

	/*
	 * This method should be used to update a existing reminder.Call the
	 * corresponding method of Respository interface.
	 */
	public Reminder updateReminder(Reminder reminder, String reminderId) throws ReminderNotFoundException {
		
		if(reminderId!= null) {
			Reminder rem = getReminderById(reminderId);
			
			if(rem!= null && reminder!= null) {
				reminder.setReminderId(rem.getReminderId());
				reminderRepository.save(reminder);
			}else {
				throw new ReminderNotFoundException("Reminder Not Found");
			}
		}

		return reminder;
	}

	/*
	 * This method should be used to get a reminder by reminderId.Call the
	 * corresponding method of Respository interface.
	 */
	public Reminder getReminderById(String reminderId) throws ReminderNotFoundException {
		Optional<Reminder> rem = null;
		if(reminderId!= null) {
			rem = reminderRepository.findById(reminderId);
			
			if(rem!= null) {
				return rem.get();
			}
			
		}

		throw new ReminderNotFoundException("reminder not found!");
	}

	/*
	 * This method should be used to get all reminders. Call the corresponding
	 * method of Respository interface.
	 */

	public List<Reminder> getAllReminders() {

		return reminderRepository.findAll();
	}

}