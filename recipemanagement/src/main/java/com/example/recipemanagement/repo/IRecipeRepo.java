package com.example.recipemanagement.repo;

import com.example.recipemanagement.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


public interface IRecipeRepo extends JpaRepository<Recipe, Integer> {


}
