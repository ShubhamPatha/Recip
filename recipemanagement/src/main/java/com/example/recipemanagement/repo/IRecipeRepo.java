package com.example.recipemanagement.repo;

import com.example.recipemanagement.model.Recipe;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IRecipeRepo extends CrudRepository<Recipe, Integer> {


    Recipe findfirstbyRecipeId(Integer id);
}
