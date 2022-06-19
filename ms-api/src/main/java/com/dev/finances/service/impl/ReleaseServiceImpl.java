package com.dev.finances.service.impl;

import com.dev.finances.api.dto.PaginatedResponseDTO;
import com.dev.finances.api.dto.ReleasesDTO;
import com.dev.finances.exception.BusinessException;
import com.dev.finances.model.entity.Release;
import com.dev.finances.model.entity.User;
import com.dev.finances.model.enums.ReleaseStatusEnum;
import com.dev.finances.model.enums.ReleaseTypeEnum;
import com.dev.finances.repository.ReleaseRepository;
import com.dev.finances.service.ReleaseService;
import com.dev.finances.service.UserService;
import com.dev.finances.utils.DateUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ReleaseServiceImpl implements ReleaseService {

    private final ReleaseRepository repository;
    private final @NonNull UserService userService;

    @Override
    @Transactional
    public Release save(Release release) {
        validate(release);
        release.setStatus(ReleaseStatusEnum.PENDENTE);
        release.setCreateAt(LocalDate.now());
        return repository.save(release);
    }

    @Override
    @Transactional
    public Release update(Release release) {
        Objects.requireNonNull(release.getId());
        validate(release);
        return repository.save(release);
    }

    @Override
    @Transactional
    public void delete(Release release) {
        Objects.requireNonNull(release.getId());
        repository.delete(release);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Release> find(Release releaseFilter) {
        Example example = Example.of(releaseFilter,
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withStringMatcher(StringMatcher.CONTAINING));
        return repository.findAll(example);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReleasesDTO> lastReleases(Long userId) {
        List<ReleasesDTO> releases = new LinkedList<>();
        repository.lastReleases(userId, LocalDate.now().minusDays(30), LocalDate.now()).forEach(release -> {
            releases.add(convert(release.get()));
        });
        return releases;
    }

    @Override
    @Transactional
    public BigDecimal getExtractByReleaseType(Long userId, ReleaseTypeEnum releaseType) {
        BigDecimal extract = repository.getBalanceByReleaseTypeUserAndStatus(userId, releaseType, ReleaseStatusEnum.EFETIVADO, LocalDate.now().minusDays(30),
                LocalDate.now());
        return (extract == null) ? BigDecimal.ZERO : extract;
    }

    @Override
    @Transactional
    public PaginatedResponseDTO<ReleasesDTO> getReleasesPaginated(Long userId, Integer page, Integer size) {
        Page<ReleasesDTO> pageReleases = Page.empty();
        Optional<User> user = userService.getById(userId);
        if (user.isPresent()) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("releaseDate", "id").descending());
            pageReleases = repository.findAll(user.get().getId(), pageable);
        }
        return new PaginatedResponseDTO<ReleasesDTO>(pageReleases.getContent(), pageReleases.getTotalElements());
    }

    @Override
    public void updateStatus(Release release, ReleaseStatusEnum status) {
        release.setStatus(status);
        update(release);
    }

    @Override
    public void validate(Release release) {
        if (release.getDescription() == null || release.getDescription().trim().equals("")) {
            throw new BusinessException("Informe uma Descrição válida.");
        }

        if (release.getUser() == null || release.getUser().getId() == null) {
            throw new BusinessException("Informe um Usuário.");
        }

        if (release.getValue() == null || release.getValue().compareTo(BigDecimal.ZERO) < 1) {
            throw new BusinessException("Informe um Valor válido.");
        }

        if (release.getType() == null) {
            throw new BusinessException("Informe um Tipo de lançamento.");
        }

        if (release.getReleaseDate() == null) {
            throw new BusinessException("Informe uma Data para o lançamento.");
        }
    }

    @Override
    public Optional<Release> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getBalanceByUser(Long id) {
        BigDecimal receitas = repository.getBalanceByReleaseTypeUserAndStatus(id, ReleaseTypeEnum.RECEITA, ReleaseStatusEnum.EFETIVADO,
                LocalDate.now().minusDays(30), LocalDate.now());
        BigDecimal despesas = repository.getBalanceByReleaseTypeUserAndStatus(id, ReleaseTypeEnum.DESPESA, ReleaseStatusEnum.EFETIVADO,
                LocalDate.now().minusDays(30), LocalDate.now());

        if (receitas == null)
            receitas = BigDecimal.ZERO;

        if (despesas == null)
            despesas = BigDecimal.ZERO;

        return receitas.subtract(despesas);
    }

    private ReleasesDTO convert(Release release) {
        return ReleasesDTO.builder()
                .id(release.getId())
                .description(release.getDescription())
                .value(release.getValue())
                .mouth(release.getMes())
                .year(release.getAno())
                .status(release.getStatus())
                .type(release.getType())
                .releaseDate(DateUtils.dateFormatDefault(release.getReleaseDate()))
                .userId(release.getUser().getId())
                .build();
    }
}
