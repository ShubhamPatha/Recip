package com.example.recipemanagement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Recipe {
    @Id
    private  Integer RecId;
   private String name;
    private String ingredients;
    private String instructions;
}
