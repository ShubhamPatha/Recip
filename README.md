# Recipie Management API


## Overview
The Blogging Platform API is a SpringBoot-based web application that allows users to create, read, update, and delete Recipe and comments. The application uses a MySQL database to persist data, and is deployed using a static IP address on AMAZON AWS.

## Prerequisites
- Java 17
- MySQL database
- Maven
- IDE (Eclipse, IntelliJ IDEA, etc.)
- AWS

## Set up
- Clone the repository from the GitHub link.
- Import the project into the IDE.
- Configure the MySQL database by updating the application.properties file located in the src/main/resources folder. Change the spring.datasource.url and spring.datasource.username and spring.datasource.password properties with the corresponding values for the database.
- Run the project as a Maven build.

## Architecture
- The project follows the MVC (Model-View-Controller) pattern, with separate packages for controllers, services, and repositories.
- The controllers handle HTTP requests and return HTTP responses.
- The services contain the business logic of the application and interact with the repositories to retrieve and store data.
- The repositories communicate with the database to perform CRUD operations on the entities.
- The project uses annotation-based validation to validate the input data.

## Framework And language used
- The Blogging Platform API is built using the Spring Boot framework, which is a popular and widely used framework for building web applications in Java.
- Spring Boot provides a simplified approach to building web applications and eliminates the need for complex XML configuration.
- It also provides a wide range of features and plugins that make it easy to develop, test, and deploy web applications.
- The API is written in Java, which is a widely used programming language known for its robustness, flexibility, and platform independence.
- Java is widely used in the development of web applications and provides a wide range of features and libraries that make it easy to build scalable and high- performance application. 

## Data Flow

### Model
  #### User
  ```
package com.example.recipemanagement.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class,scope=User.class,property="UserEmail")
public class User {

    @Id
    private String UserrEmail;
    private String Password;
}

```
  #### SignUpOutput
  ```
package com.example.recipemanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignUpOutput {

    private boolean signUpStatus;
    private String signUpStatusMessage;


}
```
  #### SignInInput
  ```
package com.example.recipemanagement.model;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignInInput {

    @Pattern(regexp = "^.+@(?![Hh][Oo][Ss][Pp][Aa][Dd][Mm][Ii][Nn]\\.[Cc][Oo][Mm]$).+$")
    private String email;
    private String password;
}

```
  ### Recipe
  ```
package com.example.recipemanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer RecId;
    private String name;
    private String ingredients;
    private String instructions;
    @OneToOne
    User user;

}

```
  ### AuthenticationToken
  ```
package com.example.recipemanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AuthenticationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;
    private String tokenValue;
    private LocalDateTime tokenCreationDateTime;

    //mapping
    @OneToOne
    @JoinColumn(name = "fk_user_emailId")
    User user;


    //create a parameterized constructor which takes patient as an argument
    public AuthenticationToken(User user)
    {
        this.user=user;
        this.tokenValue = UUID.randomUUID().toString();
        this.tokenCreationDateTime = LocalDateTime.now();
    }
}

```


### Controller

 #### RecipeController
  ```
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

```

### Service
 #### AuthenticationService
  ```
package com.example.recipemanagement.service;


import com.example.recipemanagement.model.AuthenticationToken;
import com.example.recipemanagement.model.Recipe;
import com.example.recipemanagement.model.User;
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

        String tokenConnectedEmail = authToken.getUser().getUserrEmail();


        return tokenConnectedEmail.equals(email);
    }
}

```
 #### EmailHandler
  ```
package com.example.recipemanagement.service;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;


public class EmailHandler {

    private static final String EMAIL_USERNAME ="shubhampathak1350@gmail.com";
    private static final String EMAIL_PASSWORD ="mzbqcpamiudsahtn";


    public static void sendEmail(String toEmail, String subject, String body) throws MessagingException {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(toEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
            System.out.println("OTP sent successfully to " + toEmail);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}

```
 #### PasswordEncrypter
  ```
package com.example.recipemanagement.service;

import jakarta.xml.bind.DatatypeConverter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordEncrypter {

    public static String encryptPassword(String userPassword) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");

        md5.update(userPassword.getBytes());
        byte[] digested = md5.digest();

        return DatatypeConverter.printHexBinary(digested);


    }
}
```
 #### RecipeService
  ```
package com.example.recipemanagement.service;

import com.example.recipemanagement.model.Recipe;
import com.example.recipemanagement.model.User;
import com.example.recipemanagement.repo.IAuthTokenRepo;
import com.example.recipemanagement.repo.IRecipeRepo;
import com.example.recipemanagement.repo.IUserRepo;
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

    public String ifsamedelete(String email, Integer id) {

        //Patient patient = patientRepo.findFirstByPatientEmail(email);

       Recipe recipe2=iRecipeRepo.findById(id).orElse(null);

                //findFIrstByRecId(id);
             String k=recipe2.getUser().getUserrEmail();
        if(k==email)
        {
            iRecipeRepo.deleteById(id);
            return "deleted";
        }

        //Appointment appointment = appointmentService.getAppointmentForPatient(patient);


        //appointmentService.cancelAppointment(appointment);




return " Cannot be deleted";
    }

    }

```
 #### UserService
  ```
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

```




### Repository
 #### IAuthTokenRepo
  ```
package com.example.recipemanagement.repo;

import com.example.recipemanagement.model.AuthenticationToken;
import com.example.recipemanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface IAuthTokenRepo extends JpaRepository<AuthenticationToken,Long> {


    AuthenticationToken findFirstByTokenValue(String authTokenValue);

    AuthenticationToken findFirstByUser(User user);
}

```
 #### IRecipeRepo
  ```
package com.example.recipemanagement.repo;

import com.example.recipemanagement.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


public interface IRecipeRepo extends JpaRepository<Recipe, Integer> {


}

```
#### IUserRepo
  ```
package com.example.recipemanagement.repo;

import com.example.recipemanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface IUserRepo extends JpaRepository<User,String> {



}

```

### Application resource
  ```
spring.datasource.url=jdbc:mysql://16.171.115.208:3306/rdb
spring.datasource.username=m1
spring.datasource.password=1234
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update


spring.jpa.properties.hibernate.show_sql=true
spring.jpa.properties.hibernate.use_sql_comments=true
spring.jpa.properties.hibernate.format_sql=true
```

### POM
  ```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>3.1.1</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
		<groupId>com.example</groupId>
	<artifactId>recipemanagement</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>recipemanagement</name>
	<description>Demo project for Spring Boot</description>
	<properties>
		<java.version>17</java.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-validation</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>

		<dependency>
			<groupId>com.mysql</groupId>
			<artifactId>mysql-connector-j</artifactId>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<optional>true</optional>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.springdoc</groupId>
			<artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
			<version>2.1.0</version>
		</dependency>
		<dependency>
			<groupId>com.sun.mail</groupId>
			<artifactId>javax.mail</artifactId>
			<version>1.6.2</version>
		</dependency>
	</dependencies>

	
	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
				<configuration>
					<excludes>
						<exclude>
							<groupId>org.projectlombok</groupId>
							<artifactId>lombok</artifactId>
						</exclude>
					</excludes>
				</configuration>
			</plugin>
		</plugins>
	</build>

</project>

```

## Project Summary
- The RECIPE MANAGEMENT Platform API project is a web-based application that allows users to create, read, update, and delete posts and comments.
- The project includes two main models - User, Recipe - which are managed by separate controllers, services, and repositories, the User model represents a user of the blogging platform, while the Recipe model represents a recipe post and holds data of recipe
- The Recipecontroller is responsible for handling HTTP requests and responses, while the UserService, RecipeService manage the business logic for creating, updating, deleting, and searching for users, recipe, the UserRepository, recipeRepository provide data access and persistence for these models using the MySQL database.
- The project also includes several key features, such as validation and authorization. Input validation is performed using annotation-based validation, which ensures that all user input is validated and sanitized before it is stored in the database.

Overall, the Recipe Management  Platform API is a solid example of how to build a RESTful web service with Spring Boot and Java. It is well-structured, easy to understand, and provides a wide range of features that make it suitable for building scalable and high-performance web applications.

## Endpoints
Any REST client or any internet connected browser (e.g. Postman, Swagger) can be used to interact with the API endpoints. The available endpoints include:

1) User
- PostMapping -> http://13.48.135.33:8080/User/signup

- PostMapping  -> http://13.48.135.33:8080/User/signIn

- DeleteMapping -> http://13.48.135.33:8080/User/signOut


<br></br>

2) Recipe
- GetMapping -> http://13.48.135.33:8080/getallrecipe

- GetMapping -> http://13.48.135.33:8080/GetSpecificRecipe/{Id}

- DeleteMapping -> http://13.48.135.33:8080/DeleteRecipe/{Id}
  
- PostMapping  -> http://13.48.135.33:8080/AddRecipe


<br></br>

3) Comments
- PostMapping -> http://13.48.135.33:8080/api/v1/comment/postComment
![PostComment](PostComments.png)
- GetMapping ->http://13.48.135.33:8080/api/v1/comment/getCommentsById
![GetComments](GetCommentById.png)

 

