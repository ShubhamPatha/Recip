package com.example.recipemanagement.repo;

import com.example.recipemanagement.model.AuthenticationToken;
import com.example.recipemanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserRepo  extends JpaRepository<User,String> {

    User findFirstByUserEmail(String newEmail);
}
