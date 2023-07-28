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


    public String addRec(Recipe rec) {
        iRecipeRepo.save(rec);

        return "Added";
    }

    public String deleterecipebyid(String email, Integer id) {

        Recipe r=iRecipeRepo.findfirstbyRecipeId(id);


                if(r.getUser().getUseremail()==email)
        {
            iRecipeRepo.deleteById(id);
            return "Deleted successfully";
        }
              else {
            return "Cannot deleted recipe as it don't belong to you";
        }

    }
}
