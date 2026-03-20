package com.edsonuso.collabapi.user.service;

import com.edsonuso.collabapi.common.exception.BusinessException;
import com.edsonuso.collabapi.common.exception.ResourceNotFoundException;
import com.edsonuso.collabapi.user.command.SpecializationCommands;
import com.edsonuso.collabapi.user.entity.Specialization;
import com.edsonuso.collabapi.user.entity.User;
import com.edsonuso.collabapi.user.repository.SpecializationRepository;
import com.edsonuso.collabapi.user.repository.UserRepository;
import com.edsonuso.collabapi.user.repository.UserSpecializationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.edsonuso.collabapi.user.entity.UserSpecialization;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpecializationService {

    private final SpecializationRepository specializationRepository;
    private final UserSpecializationRepository userSpecializationRepository;
    private final UserRepository userRepository;

    // ── Catálogo (público) ──

    @Transactional(readOnly = true)
    public List<SpecializationCommands.SpecializationResponse> listAll() {
        return specializationRepository.findByActiveTrueOrderByDisplayOrderAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    // ── Especializações do usuário ──

    @Transactional(readOnly = true)
    public List<SpecializationCommands.UserSpecializationResponse> getUserSpecializations(String publicId) {
        User user = findUserByPublicId(publicId);

        return userSpecializationRepository.findByUserId(user.getId()).stream()
                .map(us -> new SpecializationCommands.UserSpecializationResponse(
                        us.getSpecialization().getName(),
                        us.getSpecialization().getSlug(),
                        us.getSpecialization().getIcon(),
                        us.isPrimary()
                ))
                .toList();
    }

    /**
     * Atualiza as especializações do usuário (replace completo).
     * Deleta as existentes e insere as novas — simples e seguro.
     */
    @Transactional
    public List<SpecializationCommands.UserSpecializationResponse> updateUserSpecializations(
            String publicId,
            SpecializationCommands.UpdateSpecializationsRequest request
    ) {
        User user = findUserByPublicId(publicId);

        // Valida que todos os slugs existem
        List<Specialization> specs = specializationRepository.findBySlugIn(request.slugs());

        if (specs.size() != request.slugs().size()) {
            Set<String> found = specs.stream().map(Specialization::getSlug).collect(Collectors.toSet());
            Set<String> invalid = request.slugs().stream()
                    .filter(s -> !found.contains(s))
                    .collect(Collectors.toSet());
            throw new BusinessException(
                    "Especializações inválidas: " + String.join(", ", invalid),
                    HttpStatus.UNPROCESSABLE_ENTITY
            );
        }

        // Valida primarySlug
        if (request.primarySlug() != null && !request.slugs().contains(request.primarySlug())) {
            throw new BusinessException(
                    "A especialização primária deve estar na lista de especializações selecionadas",
                    HttpStatus.UNPROCESSABLE_ENTITY
            );
        }

        // Remove existentes
        userSpecializationRepository.deleteAllByUserId(user.getId());

        // Insere novas
        Map<String, Specialization> specMap = specs.stream()
                .collect(Collectors.toMap(Specialization::getSlug, Function.identity()));

        String primarySlug = request.primarySlug() != null
                ? request.primarySlug()
                : request.slugs().iterator().next(); // Primeira como padrão se não informada

        List<com.edsonuso.collabapi.user.entity.UserSpecialization> newSpecs = request.slugs().stream()
                .map(slug -> {
                    Specialization spec = specMap.get(slug);
                    boolean isPrimary = slug.equals(primarySlug);
                    return UserSpecialization.of(user, spec, isPrimary);
                })
                .toList();

        userSpecializationRepository.saveAll(newSpecs);

        log.info("Especializações atualizadas para publicId={}: {}", publicId, request.slugs());

        return newSpecs.stream()
                .map(us -> new SpecializationCommands.UserSpecializationResponse(
                        us.getSpecialization().getName(),
                        us.getSpecialization().getSlug(),
                        us.getSpecialization().getIcon(),
                        us.isPrimary()
                ))
                .toList();
    }

    // ── Queries para outros módulos (feed, colaboração) ──

    @Transactional(readOnly = true)
    public List<String> findUsersBySpecialization(String slug) {
        return userSpecializationRepository.findUserPublicIdsBySpecializationSlug(slug);
    }

    @Transactional(readOnly = true)
    public List<String> findCollaborators(String publicId) {
        User user = findUserByPublicId(publicId);
        return userSpecializationRepository.findUsersWithSharedSpecializations(user.getId());
    }

    // ── Helpers ──

    private User findUserByPublicId(String publicId) {
        return userRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", publicId));
    }

    private SpecializationCommands.SpecializationResponse toResponse(Specialization spec) {
        return new SpecializationCommands.SpecializationResponse(
                spec.getId(),
                spec.getName(),
                spec.getSlug(),
                spec.getDescription(),
                spec.getIcon()
        );
    }
}