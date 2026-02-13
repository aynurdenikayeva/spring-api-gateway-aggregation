package com.aynur.profile_service.controller;

import com.aynur.profile_service.dto.ProfileDto;
import com.aynur.profile_service.service.ProfileService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profiles")
public class ProfileController {

    private final ProfileService service;

    public ProfileController(ProfileService service) {
        this.service = service;
    }

    @GetMapping("/me")
    public ProfileDto getMyProfile() {
        return service.getMyProfile();
    }

    @PutMapping("/me")
    public ProfileDto updateMyProfile(@RequestBody ProfileDto dto) {
        return service.updateMyProfile(dto);
    }
}
