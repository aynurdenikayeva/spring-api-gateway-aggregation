package com.aynur.profile_service.entity;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "profiles")
public class Profile {
    @Id
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private String address;


}
