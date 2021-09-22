package com.stackroute.keepnote.service;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stackroute.keepnote.exception.CategoryDoesNoteExistsException;
import com.stackroute.keepnote.exception.CategoryNotCreatedException;
import com.stackroute.keepnote.exception.CategoryNotFoundException;
import com.stackroute.keepnote.model.Category;
import com.stackroute.keepnote.repository.CategoryRepository;

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
public class CategoryServiceImpl implements CategoryService {

	/*
	 * Autowiring should be implemented for the CategoryRepository. (Use
	 * Constructor-based autowiring) Please note that we should not create any
	 * object using the new keyword.
	 */
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	public CategoryServiceImpl(CategoryRepository categoryRepository) {
		
		this.categoryRepository=categoryRepository;
	}

	/*
	 * This method should be used to save a new category.Call the corresponding
	 * method of Respository interface.
	 */
	public Category createCategory(Category category) throws CategoryNotCreatedException {


		if(category!= null && category.getId()!= null) {
			category.setCategoryCreationDate(new Date());
			Category cat = categoryRepository.insert(category);
			
			if(cat!= null) {
				return cat;
			}
		}
		
		throw new CategoryNotCreatedException("Category not created");
	}

	/*
	 * This method should be used to delete an existing category.Call the
	 * corresponding method of Respository interface.
	 */
	public boolean deleteCategory(String categoryId) throws CategoryDoesNoteExistsException {

		Category cat1 = null;
		try {
			if(categoryId!= null) {
				cat1 = getCategoryById(categoryId);
			}
			if(cat1 != null) {
				 categoryRepository.delete(cat1);
				 return true;
			}
		} catch (CategoryNotFoundException e) {
			e.printStackTrace();
			throw new  CategoryDoesNoteExistsException(e.getMessage());
		}

		return false;
	}

	/*
	 * This method should be used to update a existing category.Call the
	 * corresponding method of Respository interface.
	 */
	public Category updateCategory(Category category, String categoryId) {

		
		if(categoryId!= null) {
			try {
				Category cat1 = getCategoryById(categoryId);
			
				if(cat1!= null && category!= null) {
					category.setId(cat1.getId());
					category.setCategoryCreationDate(new Date());
					categoryRepository.save(category);
					
					return category;
				}
			
				
			} catch (CategoryNotFoundException e) {
				return null;
			}
		}
		return category;
	}

	/*
	 * This method should be used to get a category by categoryId.Call the
	 * corresponding method of Respository interface.
	 */
	public Category getCategoryById(String categoryId) throws CategoryNotFoundException {

		Optional<Category> category = null;
		
		try {
			if(categoryId!= null) {
				category = categoryRepository.findById(categoryId);
			}
			if(category != null) {
				return category.get();
			}
			
		} catch (NoSuchElementException e) {
			throw new CategoryNotFoundException("Category not found") ;
		}

		throw new CategoryNotFoundException("Category not found") ;
	}

	/*
	 * This method should be used to get a category by userId.Call the corresponding
	 * method of Respository interface.
	 */
	public List<Category> getAllCategoryByUserId(String userId) {

		return categoryRepository.findAllCategoryByCategoryCreatedBy(userId);
	}

}