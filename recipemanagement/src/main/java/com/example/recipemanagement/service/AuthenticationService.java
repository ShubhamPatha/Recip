package com.example.recipemanagement.service;


import com.example.recipemanagement.model.AuthenticationToken;
import com.example.recipemanagement.repo.IAuthTokenRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    IAuthTokenRepo authTokenRepo;

    public boolean authenticate(String email, String authTokenValue)
    {
        AuthenticationToken authToken = authTokenRepo.findFirstByTokenValue(authTokenValue);

        if(authToken == null)
        {
            return false;
        }

        String tokenConnectedEmail = authToken.getUser().getUseremail();


        return tokenConnectedEmail.equals(email);
    }
}