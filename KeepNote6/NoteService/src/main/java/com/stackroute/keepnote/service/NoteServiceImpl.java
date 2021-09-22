package com.stackroute.keepnote.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stackroute.keepnote.exception.NoteNotFoundExeption;
import com.stackroute.keepnote.model.Note;
import com.stackroute.keepnote.model.NoteUser;
import com.stackroute.keepnote.repository.NoteRepository;

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
public class NoteServiceImpl implements NoteService{

	/*
	 * Autowiring should be implemented for the NoteRepository and MongoOperation.
	 * (Use Constructor-based autowiring) Please note that we should not create any
	 * object using the new keyword.
	 */
	
	@Autowired
	private NoteRepository noteRepository;
	
			
	

	/*
	 * This method should be used to save a new note.
	 */
	public boolean createNote(Note note) {
		
		
		NoteUser noteUser = new NoteUser();
		if(note!= null && note.getNoteCreatedBy()!= null) {
			note.setNoteCreationDate(new Date());

			List<Note> notes = null;
			
			notes = getAllNoteByUserId(note.getNoteCreatedBy());
			if(notes!= null && !notes.isEmpty()) {
				
				notes.add(note);
				noteUser.setNotes(notes);
				noteUser.setUserId(note.getNoteCreatedBy());
				NoteUser noteUser2=  noteRepository.save(noteUser);
				System.out.println("noteUser2 Update:::: "+noteUser2);
				if(noteUser2!=null) {
					return true;
				}
				
			}else {

				notes = new ArrayList<>();
				notes.add(note);
				noteUser.setNotes(notes);
				noteUser.setUserId(note.getNoteCreatedBy());
				
				NoteUser noteUser2=  noteRepository.insert(noteUser);
				System.out.println("noteUser2 Insert:::: "+noteUser2);
				if(noteUser2!=null) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/* This method should be used to delete an existing note. */

	
	public boolean deleteNote(String userId, int noteId) {
		
		if(userId!= null) {
			
			Note note = null;
			try {
				note = getNoteByNoteId(userId, noteId);
			} catch (NoteNotFoundExeption e) {
				throw new NullPointerException();
			}
			if(note!= null) {
				NoteUser noteUser = new NoteUser();
				List<Note> notes = new ArrayList<>();
				notes.add(note);
				noteUser.setNotes(notes);
				noteUser.setUserId(userId);
				noteRepository.delete(noteUser);
				return true;
			}
		}
		
		return false;
	}
	
	/* This method should be used to delete all notes with specific userId. */

	
	public boolean deleteAllNotes(String userId) {
		
		if(userId!= null) {
			
			noteRepository.deleteById(userId);
			return true;
		}
		
		return false;
	}

	/*
	 * This method should be used to update a existing note.
	 */
	public Note updateNote(Note note, int id, String userId) throws NoteNotFoundExeption {
		
		List<Note> notes = null;
		if(userId != null) {
			
			 getNoteByNoteId(userId, id);
			
			notes = getAllNoteByUserId(userId);
			//note1 =  getNoteByNoteId(userId, id);
			if(note!= null && notes != null && !notes.isEmpty()) {
				NoteUser noteUser = new NoteUser();
				List<Note> newNotes = new ArrayList<>();
				for (Note note2 : notes) {
					if(note2.getNoteId()!=id) {
						newNotes.add(note2);
					}
				}
				
				newNotes.add(note);
				noteUser.setNotes(newNotes);
				noteUser.setUserId(userId);
				noteRepository.save(noteUser);
				return note;
			}
			
		}
		
		throw new NoteNotFoundExeption("Note not found");
	}

	/*
	 * This method should be used to get a note by noteId created by specific user
	 */
	public Note getNoteByNoteId(String userId, int noteId) throws NoteNotFoundExeption {
		
		try {
			if(userId!= null && noteId > 0) {
				
				Optional<NoteUser> optional =  noteRepository.findById(userId);
				if(optional.isPresent() && optional.get().getNotes()!= null) {
					
					Iterator<Note> notes = optional.get().getNotes().iterator();
					while (notes.hasNext()) {
						Note note = (Note) notes.next();
						if(note.getNoteId() ==noteId ) {
							 return  note;
						}
					}
					return null;
				}
			}
			
		} catch (Exception e) {
			throw new NoteNotFoundExeption("Note Not Found!");
		}
		
		throw new NoteNotFoundExeption("Note Not Found!");
	}

	/*
	 * This method should be used to get all notes with specific userId.
	 */
	public List<Note> getAllNoteByUserId(String userId) {
		
		if(userId!= null) {
			
			Optional<NoteUser> optional =  noteRepository.findById(userId);
			
			if(optional.isPresent() && optional.get().getNotes()!= null) {
				
				return optional.get().getNotes();
			}
		}
			
		return null;
	}

}