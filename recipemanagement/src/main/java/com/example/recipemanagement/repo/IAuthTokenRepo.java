package com.example.recipemanagement.repo;


import com.example.recipemanagement.model.AuthenticationToken;
import com.example.recipemanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IAuthTokenRepo extends JpaRepository<AuthenticationToken,Long> {


    AuthenticationToken findFirstByTokenValue(String authTokenValue);

    AuthenticationToken findFirstByUser(User user);
}
