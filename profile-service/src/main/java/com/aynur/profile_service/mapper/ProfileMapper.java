package com.aynur.profile_service.mapper;

import com.aynur.profile_service.dto.ProfileDto;
import com.aynur.profile_service.entity.Profile;

public class ProfileMapper {
    public static ProfileDto toDto(Profile profile) {
        ProfileDto dto = new ProfileDto();
        dto.setFullName(profile.getFullName());
        dto.setEmail(profile.getEmail());
        dto.setPhone(profile.getPhone());
        dto.setAddress(profile.getAddress());
        return dto;
    }
    public static void updateEntity(Profile profile, ProfileDto dto) {
        profile.setFullName(dto.getFullName());
        profile.setEmail(dto.getEmail());
        profile.setPhone(dto.getPhone());
        profile.setAddress(dto.getAddress());
    }
}
