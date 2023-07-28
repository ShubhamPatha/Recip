package com.example.recipemanagement.service;

import com.example.recipemanagement.model.Recipe;
import com.example.recipemanagement.repo.IRecipeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {
    @Autowired
    IRecipeRepo iRecipeRepo;

    public Iterable<Recipe> getallrec() {
       return iRecipeRepo.findAll();
    }

    public Optional<Recipe> getspecific(Integer id) {
    return iRecipeRepo.findById(id);
    }

    public void deletebyid(Integer id) {


        iRecipeRepo.deleteById(id);
        return;

    }

    public String addRec(Recipe rec) {
        iRecipeRepo.save(rec);

        return "Added";
    }
//public String updaterecipebyid(Integer id, Recipe rec) {
//
//
//
//    }
    }
