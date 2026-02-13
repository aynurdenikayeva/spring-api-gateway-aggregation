package com.aynur.profile_service.service;

import com.aynur.profile_service.dto.ProfileDto;
import com.aynur.profile_service.entity.Profile;
import com.aynur.profile_service.mapper.ProfileMapper;
import com.aynur.profile_service.repository.ProfileRepository;
import com.aynur.profile_service.security.CurrentUserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(readOnly = true)
    public ProfileDto getMyProfile() {
        Long userId = currentUserProvider.getCurrentUserId();

        Profile profile = repository.findById(userId)
                .orElseGet(() -> {
                    // readOnly=true olduğuna görə burada save etməyək,
                    // sadəcə boş dto qaytaraq (və ya readOnly-ni götür)
                    Profile p = new Profile();
                    p.setUserId(userId);
                    return p;
                });

        return ProfileMapper.toDto(profile);
    }

    @Override
    @Transactional
    public ProfileDto updateMyProfile(ProfileDto dto) {
        Long userId = currentUserProvider.getCurrentUserId();

        Profile profile = repository.findById(userId)
                .orElseGet(() -> {
                    Profile p = new Profile();
                    p.setUserId(userId);
                    return repository.save(p); // <-- vacib: DB-də yaranır
                });

        ProfileMapper.updateEntity(profile, dto);

        profile = repository.save(profile);
        return ProfileMapper.toDto(profile);
    }
}
