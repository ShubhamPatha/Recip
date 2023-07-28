package com.example.recipemanagement.repo;

import com.example.recipemanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface IUserRepo extends JpaRepository<User,String> {



}
