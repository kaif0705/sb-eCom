package com.ecommerce.project.Service;

import com.ecommerce.project.Model.Category;
import com.ecommerce.project.Payload.CategoryDTO;
import com.ecommerce.project.Payload.CategoryResponse;
import com.ecommerce.project.Repository.CategoryRepository;
import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImple implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder= sortOrder.equalsIgnoreCase("asc")
                             ? Sort.by(sortBy).ascending()
                             : Sort.by(sortBy).descending();

        Pageable pageDetails= PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Category> categoryPage= categoryRepository.findAll(pageDetails);
        List<Category> allCategories= categoryPage.getContent();

        if(allCategories.isEmpty()){
            throw new APIException("No categories found");
        }else{

            List<CategoryDTO> categoryDTO= allCategories.stream()
                                           .map(category -> modelMapper.map(category, CategoryDTO.class))
                                           .toList();

            CategoryResponse categoryResponse= new CategoryResponse();
            categoryResponse.setContent(categoryDTO);

            categoryResponse.setPageNumber(categoryPage.getNumber());
            categoryResponse.setPageSize(categoryPage.getSize());
            categoryResponse.setTotalElements(categoryPage.getTotalElements());
            categoryResponse.setTotalPages(categoryPage.getTotalPages());
            categoryResponse.setLastPage(categoryPage.isLast());

            return categoryResponse;
        }
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO){
        Category update= categoryRepository.findByCategoryName(categoryDTO.getCategoryName());
        if(update!=null){
            throw new APIException("Category already exists with name: " + categoryDTO.getCategoryName());
        }else{
            Category categoryMap= modelMapper.map(categoryDTO, Category.class);
            Category saved=categoryRepository.save(categoryMap);
            return modelMapper.map(saved, CategoryDTO.class);
        }
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId){

        Optional<Category> findCategoriesById= categoryRepository.findById(categoryId);

        Category categoryToDelete= findCategoriesById
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        categoryRepository.delete(categoryToDelete);

        return modelMapper.map(findCategoriesById, CategoryDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId){

        Category category= modelMapper.map(categoryDTO, Category.class);

        Category check= categoryRepository.findByCategoryName(categoryDTO.getCategoryName());

        if(check!=null){
            throw new APIException("Category already exists with name: " + categoryDTO.getCategoryName());
        }

        Optional<Category> findCategoriesById= categoryRepository.findById(categoryId);

        Category categoryToUpdate= findCategoriesById
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        categoryToUpdate.setCategoryName(categoryDTO.getCategoryName());

        Category updated= categoryRepository.save(categoryToUpdate);

        return modelMapper.map(updated, CategoryDTO.class);

    }

}
