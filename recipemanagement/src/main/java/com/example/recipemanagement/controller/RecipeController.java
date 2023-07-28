package com.example.recipemanagement.controller;

import com.example.recipemanagement.model.Recipe;
import com.example.recipemanagement.model.SignInInput;
import com.example.recipemanagement.model.SignUpOutput;
import com.example.recipemanagement.model.User;
import com.example.recipemanagement.service.AuthenticationService;
import com.example.recipemanagement.service.RecipeService;
import com.example.recipemanagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class RecipeController {
    @Autowired
    RecipeService recipeService;
    @Autowired
    UserService userService;
    @Autowired
    AuthenticationService authenticationService;




    @PostMapping("User/signup")
    public SignUpOutput signUpPatient(@RequestBody User user)
    {


        return userService.signUpUser(user);
    }

    @PostMapping("User/signIn")
    public String sigInPatient(@RequestBody @Valid SignInInput signInInput)
    {

        return userService.signInUser(signInInput);
    }

    @DeleteMapping("User/signOut")
    public String sigOutPatient(String email, String token)
    {
        if(authenticationService.authenticate(email,token)) {

            return userService.sigOutUser(email);
        }
        else {
            return "Sign out not allowed for non authenticated user.";
        }

    }




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
    public  String Deleterec(@RequestBody Integer Id,String email, String token)
    {
        if(authenticationService.authenticate(email,token)) {

            return recipeService.ifsamedelete(email,Id);
                    //deleterecipebyid(email,Id);



        }
        else
        {
            return "Incoorect token or email";

        }

    }


    @PostMapping("AddRecipe")
    public String  AddRecipe(@RequestBody Recipe recipe,String email, String token)
    {

        if(authenticationService.authenticate(email,token)) {

            boolean status = userService.addRec(recipe);

            //patientService.scheduleAppointment(appointment);

            return status ? "appointment scheduled":"error occurred during scheduling appointment";
        }
        else
        {
            return "Scheduling failed because invalid authentication";
        }
    }



}
