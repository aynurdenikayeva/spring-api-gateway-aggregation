package com.aynur.profile_service.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProfileDto {
    private String fullName;
    private String email;
    private String phone;
    private String address;
}