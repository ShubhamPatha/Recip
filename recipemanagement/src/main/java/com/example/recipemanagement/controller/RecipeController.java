package com.example.recipemanagement.controller;

import com.example.recipemanagement.model.Recipe;
import com.example.recipemanagement.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class RecipeController {
    @Autowired
    RecipeService recipeService;



    @GetMapping("getallrecipe")
    public Iterable<Recipe> Getallrecipe()
    {
        return recipeService.getallrec();
    }

    @GetMapping("GetSpecificRecipe/{Id}")
    public Optional<Recipe> Getspecific(Integer Id)
    {
        return recipeService.getspecific(Id);
    }

    @DeleteMapping("DeleteRecipe/{Id}")
    public  void Deleterec(Integer Id)
    {
        recipeService.deletebyid(Id);
        return;
    }

    @PostMapping("AddRecipe")
    public String AddRecipe(Recipe Rec)
    {
        return recipeService.addRec(Rec);
    }

   // @PutMapping("UpdateRecipe/{Id}")
    //    public String updateRecipe(Integer id , Recipe rec)
    //    {
    //        return recipeService.updaterecipebyid(id,rec);
    //    }

}
