package com.edsonuso.collabapi.onboarding.service;

import com.edsonuso.collabapi.common.exception.BusinessException;
import com.edsonuso.collabapi.content.entity.ContentCategories;
import com.edsonuso.collabapi.content.entity.UserContentPreferences;
import com.edsonuso.collabapi.content.entity.UserContentPreferencesId;
import com.edsonuso.collabapi.content.repository.ContentCategoryRepository;
import com.edsonuso.collabapi.content.repository.UserContentPreferenceRepository;
import com.edsonuso.collabapi.follows.entity.Follow;
import com.edsonuso.collabapi.follows.repository.FollowRepository;
import com.edsonuso.collabapi.games.repository.GameRepository;
import com.edsonuso.collabapi.onboarding.commands.UserOnboardingCommands;
import com.edsonuso.collabapi.onboarding.entity.UserOnboarding;
import com.edsonuso.collabapi.onboarding.repository.UserOnboardingRepository;
import com.edsonuso.collabapi.specialization.entity.Specialization;
import com.edsonuso.collabapi.specialization.entity.UserSpecialization;
import com.edsonuso.collabapi.specialization.repository.SpecializationRepository;
import com.edsonuso.collabapi.specialization.repository.UserSpecializationRepository;
import com.edsonuso.collabapi.squad.repository.SquadRepository;
import com.edsonuso.collabapi.user.entity.User;
import com.edsonuso.collabapi.user.repository.UserRepository;
import com.edsonuso.collabapi.user.service.UsernameGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserOnboardingService {

    private final UserOnboardingRepository onboardingRepository;
    private final UserRepository userRepository;
    private final UsernameGeneratorService generatorService;
    private final SpecializationRepository specializationRepository;
    private final UserSpecializationRepository userSpecializationRepository;
    private final ContentCategoryRepository contentCategoryRepository;
    private final UserContentPreferenceRepository userContentPreferenceRepository;
    private final FollowRepository followRepository;
    private final SquadRepository squadRepository;
    private final GameRepository gameRepository;

    public UserOnboardingCommands.UserOnboardingResponse getOnboardingStatus(String publicId) {
        UserOnboarding onboarding = findByUserPublicId(publicId);
        return toResponse(onboarding);
    }

    @Transactional
    public UserOnboardingCommands.UserOnboardingResponse updateProfileStep(String publicId, UserOnboardingCommands.UserOnboardingProfileRequest request) {
        UserOnboarding onboarding = findByUserPublicId(publicId);

        if (!onboarding.getCurrentStep().equals(UserOnboarding.OnboardingStep.PROFILE)) {
            throw new BusinessException("Estado de Onboarding incompátivel", HttpStatus.CONFLICT);
        }

        User user = findUserByPublicId(publicId);

        if (generatorService.isReservedName(request.username()) && userRepository.existsByUsernameAndIdNot(request.username(), user.getId())) {
            throw new BusinessException("Este nome de usuário já esta em uso", HttpStatus.CONFLICT);
        }

        user.setUsername(request.username());
        Optional.ofNullable(request.headline()).ifPresent(user::setHeadline);
        Optional.ofNullable(request.bio()).ifPresent(user::setBio);
        Optional.ofNullable(request.avatarUrl()).ifPresent(user::setAvatarUrl);

        onboarding.setProfileCompletedAt(Instant.now());
        onboarding.setCurrentStep(UserOnboarding.OnboardingStep.SPECIALIZATIONS);

        userRepository.save(user);
        onboardingRepository.save(onboarding);

        return toResponse(onboarding);
    }

    @Transactional
    public UserOnboardingCommands.UserOnboardingResponse updateSpecializationStep(String publicId,
                                                                                  UserOnboardingCommands.UserOnboardingSpecializationRequest request) {
        UserOnboarding onboarding = findByUserPublicId(publicId);

        if (!onboarding.getCurrentStep().equals(UserOnboarding.OnboardingStep.SPECIALIZATIONS)) {
            throw new BusinessException("Estado de Onboarding incompátivel", HttpStatus.CONFLICT);
        }

        User user = findUserByPublicId(publicId);

        if (request.specializations().isEmpty()) {
            throw new BusinessException("É necessário selecionar pelo menos uma especialização", HttpStatus.BAD_REQUEST);
        }

        boolean hasPrimary = request.specializations().stream()
                .anyMatch(UserOnboardingCommands.SpecializationItem::isPrimary);
        if (!hasPrimary) {
            throw new BusinessException("Pelo menos uma especialização deve ser marcada como primária", HttpStatus.BAD_REQUEST);
        }

        List<Short> ids = request.specializations().stream()
                .map(UserOnboardingCommands.SpecializationItem::specializationId)
                .distinct()
                .toList();

        long count = specializationRepository.countByIdIn(ids);
        if (count != ids.size()) {
            throw new BusinessException("Uma ou mais especializações não existem", HttpStatus.BAD_REQUEST);
        }

        userSpecializationRepository.deleteAllByUserId(user.getId());

        List<Specialization> specializations = specializationRepository.findAllById(ids);
        Map<Short, Boolean> primaryMap = request.specializations().stream()
                .collect(Collectors.toMap(
                        UserOnboardingCommands.SpecializationItem::specializationId,
                        UserOnboardingCommands.SpecializationItem::isPrimary,
                        (a, b) -> a
                ));

        List<UserSpecialization> userSpecializations = specializations.stream()
                .map(spec -> UserSpecialization.of(user, spec, primaryMap.getOrDefault(spec.getId(), false)))
                .toList();

        userSpecializationRepository.saveAll(userSpecializations);

        onboarding.setSpecializationsCompletedAt(Instant.now());
        onboarding.setCurrentStep(UserOnboarding.OnboardingStep.PREFERENCES);
        onboardingRepository.save(onboarding);

        return toResponse(onboarding);
    }

    @Transactional
    public UserOnboardingCommands.UserOnboardingResponse updatePreferencesStep(String publicId,
                                                                               UserOnboardingCommands.UserOnboardingPreferencesRequest request) {
        UserOnboarding onboarding = findByUserPublicId(publicId);

        if (!onboarding.getCurrentStep().equals(UserOnboarding.OnboardingStep.PREFERENCES)) {
            throw new BusinessException("Estado de Onboarding incompátivel", HttpStatus.CONFLICT);
        }

        User user = findUserByPublicId(publicId);

        userContentPreferenceRepository.deleteAllByUserId(user.getId());

        boolean skipped = request.categoryIds().isEmpty();

        if (!skipped) {
            List<Long> distinctIds = request.categoryIds().stream().distinct().toList();

            long count = contentCategoryRepository.countByIdInAndActiveTrue(distinctIds);
            if (count != distinctIds.size()) {
                throw new BusinessException("Uma ou mais categorias não existem ou estão inativas", HttpStatus.BAD_REQUEST);
            }

            List<ContentCategories> categories = contentCategoryRepository.findAllById(distinctIds);
            List<UserContentPreferences> preferences = categories.stream()
                    .map(cat -> UserContentPreferences.builder()
                            .id(new UserContentPreferencesId(user.getId(), cat.getId()))
                            .user(user)
                            .contentCategories(cat)
                            .build())
                    .toList();
            userContentPreferenceRepository.saveAll(preferences);
        }

        onboarding.setPreferencesSkipped(skipped);
        onboarding.setPreferencesCompletedAt(Instant.now());
        onboarding.setCurrentStep(UserOnboarding.OnboardingStep.FOLLOWS);
        onboardingRepository.save(onboarding);

        return toResponse(onboarding);
    }

    @Transactional
    public UserOnboardingCommands.UserOnboardingResponse updateFollowsStep(String publicId,
                                                                            UserOnboardingCommands.UserOnboardingFollowsRequest request) {
        UserOnboarding onboarding = findByUserPublicId(publicId);

        if (!onboarding.getCurrentStep().equals(UserOnboarding.OnboardingStep.FOLLOWS)) {
            throw new BusinessException("Estado de Onboarding incompátivel", HttpStatus.CONFLICT);
        }

        User user = findUserByPublicId(publicId);

        boolean skipped = request.follows().isEmpty();

        if (!skipped) {
            for (UserOnboardingCommands.FollowItem item : request.follows()) {
                validateFollowableEntity(item);

                if (item.followableType() == Follow.FollowableType.USER && item.followableId().equals(user.getId())) {
                    continue;
                }

                boolean alreadyExists = followRepository.existsByFollowerIdAndFollowableTypeAndFollowableId(
                        user.getId(), item.followableType(), item.followableId());
                if (alreadyExists) {
                    continue;
                }

                Follow follow = Follow.builder()
                        .follower(user)
                        .followableType(item.followableType())
                        .followableId(item.followableId())
                        .build();
                followRepository.save(follow);
            }
        }

        Instant now = Instant.now();
        onboarding.setFollowsSkipped(skipped);
        onboarding.setFollowsCompletedAt(now);
        onboarding.setCurrentStep(UserOnboarding.OnboardingStep.COMPLETED);
        onboarding.setCompletedAt(now);
        onboardingRepository.save(onboarding);

        return toResponse(onboarding);
    }

    @Transactional
    public UserOnboardingCommands.UserOnboardingResponse skipCurrentStep(String publicId) {
        UserOnboarding onboarding = findByUserPublicId(publicId);
        UserOnboarding.OnboardingStep currentStep = onboarding.getCurrentStep();

        if (currentStep != UserOnboarding.OnboardingStep.PREFERENCES && currentStep != UserOnboarding.OnboardingStep.FOLLOWS) {
            throw new BusinessException("Apenas as etapas PREFERENCES e FOLLOWS podem ser puladas", HttpStatus.BAD_REQUEST);
        }

        Instant now = Instant.now();

        if (currentStep == UserOnboarding.OnboardingStep.PREFERENCES) {
            onboarding.setPreferencesSkipped(true);
            onboarding.setPreferencesCompletedAt(now);
            onboarding.setCurrentStep(UserOnboarding.OnboardingStep.FOLLOWS);
        } else {
            onboarding.setFollowsSkipped(true);
            onboarding.setFollowsCompletedAt(now);
            onboarding.setCurrentStep(UserOnboarding.OnboardingStep.COMPLETED);
            onboarding.setCompletedAt(now);
        }

        onboardingRepository.save(onboarding);

        return toResponse(onboarding);
    }

    private void validateFollowableEntity(UserOnboardingCommands.FollowItem item) {
        switch (item.followableType()) {
            case USER -> {
                if (!userRepository.existsByIdAndActiveTrue(item.followableId())) {
                    throw new BusinessException("Usuário com ID " + item.followableId() + " não encontrado ou inativo", HttpStatus.BAD_REQUEST);
                }
            }
            case SQUAD -> {
                if (!squadRepository.existsByIdAndActiveTrue(item.followableId())) {
                    throw new BusinessException("Squad com ID " + item.followableId() + " não encontrada ou inativa", HttpStatus.BAD_REQUEST);
                }
            }
            case GAME -> {
                if (!gameRepository.existsById(item.followableId())) {
                    throw new BusinessException("Game com ID " + item.followableId() + " não encontrado", HttpStatus.BAD_REQUEST);
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public UserOnboardingCommands.UsernameAvailabilityResponse checkUsernameAvailability(String username, String authenticatedPublicId) {
        String regex = "^[a-z0-9]([a-z0-9]{0,28}[a-z0-9])?$";
        if (username == null || !username.matches(regex) || username.length() < 2 || username.length() > 30) {
            throw new BusinessException("Formato de username inválido", HttpStatus.BAD_REQUEST);
        }

        if (generatorService.isReservedName(username)) {
            return new UserOnboardingCommands.UsernameAvailabilityResponse(false);
        }

        if (authenticatedPublicId != null) {
            boolean takenByOther = userRepository.findByUsername(username)
                    .map(u -> !u.getPublicId().equals(authenticatedPublicId))
                    .orElse(false);
            return new UserOnboardingCommands.UsernameAvailabilityResponse(!takenByOther);
        }

        boolean exists = userRepository.existsByUsername(username);
        return new UserOnboardingCommands.UsernameAvailabilityResponse(!exists);
    }

    private User findUserByPublicId(String publicId) {
        return userRepository.findByPublicId(publicId)
                .orElseThrow(() -> new BusinessException("Usuario não encontrado", HttpStatus.NOT_FOUND));
    }

    private UserOnboarding findByUserPublicId(String publicId) {
        User user = findUserByPublicId(publicId);
        return onboardingRepository.findById(user.getId())
                .orElseThrow(() -> new BusinessException("Onboarding de usuario não encontrado", HttpStatus.NOT_FOUND));
    }

    private UserOnboardingCommands.UserOnboardingResponse toResponse(UserOnboarding onboarding) {
        return new UserOnboardingCommands.UserOnboardingResponse(
                onboarding.getCurrentStep(),
                onboarding.getProfileCompletedAt(),
                onboarding.getSpecializationsCompletedAt(),
                onboarding.getPreferencesSkipped(),
                onboarding.getPreferencesCompletedAt(),
                onboarding.getFollowsSkipped(),
                onboarding.getFollowsCompletedAt(),
                onboarding.getCompletedAt()
        );
    }
}
