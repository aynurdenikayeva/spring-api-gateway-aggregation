package com.aynur.profile_service.service;

import com.aynur.profile_service.dto.ProfileDto;

public interface ProfileService {
    ProfileDto getMyProfile();
    ProfileDto updateMyProfile(ProfileDto dto);

}
