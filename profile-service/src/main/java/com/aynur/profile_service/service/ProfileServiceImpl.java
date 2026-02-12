package com.aynur.profile_service.service;

import com.aynur.profile_service.dto.ProfileDto;
import com.aynur.profile_service.entity.Profile;
import com.aynur.profile_service.mapper.ProfileMapper;
import com.aynur.profile_service.repository.ProfileRepository;
import com.aynur.profile_service.security.CurrentUserProvider;
import org.springframework.stereotype.Service;

@Service
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository repository;
    private final CurrentUserProvider currentUserProvider;
    public ProfileServiceImpl(ProfileRepository repository,
                              CurrentUserProvider currentUserProvider) {
        this.repository = repository;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    public ProfileDto getMyProfile() {
        Long userId = currentUserProvider.getCurrentUserId();
        Profile profile = repository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        return ProfileMapper.toDto(profile);
    }

    @Override
    public ProfileDto updateMyProfile(ProfileDto dto) {
        Long userId = currentUserProvider.getCurrentUserId();
        Profile profile = repository.findById(userId)
                .orElseGet(() -> {
                    Profile p = new Profile();
                    p.setUserId(userId);
                    return p;
                });
        ProfileMapper.updateEntity(profile, dto);

        repository.save(profile);
        return ProfileMapper.toDto(profile);
    }
}