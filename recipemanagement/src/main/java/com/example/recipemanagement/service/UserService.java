package com.example.recipemanagement.service;


import com.example.recipemanagement.model.*;
import com.example.recipemanagement.model.User;
import com.example.recipemanagement.repo.IAuthTokenRepo;
import com.example.recipemanagement.repo.IRecipeRepo;
import com.example.recipemanagement.repo.IUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    IUserRepo iUserRepo;
    @Autowired
    IAuthTokenRepo iAuthTokenRepo;
    @Autowired
    IRecipeRepo iRecipeRepo;

    @Autowired
    RecipeService recipeService;









    public SignUpOutput signUpUser(User user) {

        boolean signUpStatus = true;
        String signUpStatusMessage = null;


        String newEmail =user.getUserrEmail();


        if(newEmail == null)
        {
            signUpStatusMessage = "Invalid email";
            signUpStatus = false;
            return new SignUpOutput(signUpStatus,signUpStatusMessage);
        }


        User existingPatient =iUserRepo.findById(newEmail).orElse(null);
                //findFirstByUserrEmail(newEmail);


        if(existingPatient != null)
        {
            signUpStatusMessage = "Email already registered!!!";
            signUpStatus = false;
            return new SignUpOutput(signUpStatus,signUpStatusMessage);
        }

        //hash the password: encrypt the password
        try {
            String encryptedPassword = PasswordEncrypter.encryptPassword(user.getPassword());


            //saveAppointment the patient with the new encrypted password

            user.setPassword(encryptedPassword);
            iUserRepo.save(user);


            return new SignUpOutput(signUpStatus, "User registered successfully!!!");
        }
        catch(Exception e)
        {
            signUpStatusMessage = "Internal error occurred during sign up";
            signUpStatus = false;
            return new SignUpOutput(signUpStatus,signUpStatusMessage);
        }
    }

    public Iterable<User> getAllUser() {
        return iUserRepo.findAll();
        //return iUserRepo.findAll();
        //patientRepo.findAll();
    }


    public String signInUser(SignInInput signInInput) {


        String signInStatusMessage = null;

        String signInEmail = signInInput.getEmail();

        if(signInEmail == null)
        {
            signInStatusMessage = "Invalid email";
            return signInStatusMessage;


        }

        //check if this patient email already exists ??
        User existingUser= iUserRepo.findById(signInEmail).orElse(null);
                //findFirstByUserrEmail(signInEmail);


        if(existingUser == null)
        {
            signInStatusMessage = "Email not registered!!!";
            return signInStatusMessage;

        }

        //match passwords :

        //hash the password: encrypt the password
        try {
            String encryptedPassword = PasswordEncrypter.encryptPassword(signInInput.getPassword());
            if(existingUser.getPassword().equals(encryptedPassword))
            {
                //session should be created since password matched and user id is valid
                AuthenticationToken authToken  = new AuthenticationToken(existingUser);
                iAuthTokenRepo.save(authToken);

                EmailHandler.sendEmail(signInEmail,"email testing",authToken.getTokenValue());
                return "Token sent to your email";
            }
            else {
                signInStatusMessage = "Invalid credentials!!!";
                return signInStatusMessage;
            }
        }
        catch(Exception e)
        {
            signInStatusMessage = "Internal error occurred during sign in";
            return signInStatusMessage;
        }

    }



    public String sigOutUser(String email) {

        User user = iUserRepo.findById(email).orElse(null);
                //findFirstByUserrEmail(email);
        iAuthTokenRepo.delete(iAuthTokenRepo.findFirstByUser(user));

        return "Patient Signed out successfully";
    }

    public boolean addRec(Recipe recipe) {

        Integer RecipeId=recipe.getRecId();
        boolean isRecipeValid = iRecipeRepo.existsById(RecipeId);



        if(isRecipeValid)
        {
            recipeService.addRec(recipe);


            return true;
        }
        else {
            return false;
        }
    }
}
