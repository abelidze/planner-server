package com.skillmasters.server.model;

import java.util.UUID;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class User
{
    @Id
    @GeneratedValue
    private Long id;
    private String username;
    private String password;
    private String token;

    User(
      String username,
      String password
    ) {
        this.username = username;
        this.password = password;
        this.token = UUID.randomUUID().toString();
    }
}