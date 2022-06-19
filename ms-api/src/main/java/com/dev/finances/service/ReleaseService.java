package com.dev.finances.service;

import com.dev.finances.api.dto.PaginatedResponseDTO;
import com.dev.finances.api.dto.ReleasesDTO;
import com.dev.finances.model.entity.Release;
import com.dev.finances.model.enums.ReleaseStatusEnum;
import com.dev.finances.model.enums.ReleaseTypeEnum;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ReleaseService {
	
	Release save(Release release);
	
	Release update(Release release);
	
	void delete(Release release);
	
	List<Release> find(Release releaseFilter);
	
	void updateStatus(Release release, ReleaseStatusEnum status);
	
	void validate(Release release);
	
	Optional<Release> findById(Long id);
	
	BigDecimal getBalanceByUser(Long id);

	List<ReleasesDTO> lastReleases(Long userId);

	BigDecimal getExtractByReleaseType(Long userId, ReleaseTypeEnum releaseType);

	PaginatedResponseDTO<ReleasesDTO> getReleasesPaginated(Long userId, Integer page, Integer size);
}
