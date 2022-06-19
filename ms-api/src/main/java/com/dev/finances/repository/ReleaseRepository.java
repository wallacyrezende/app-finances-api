package com.dev.finances.repository;

import com.dev.finances.api.dto.ReleasesDTO;
import com.dev.finances.model.entity.Release;
import com.dev.finances.model.enums.ReleaseStatusEnum;
import com.dev.finances.model.enums.ReleaseTypeEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReleaseRepository extends JpaRepository<Release, Long> {

    String WHERE_RELEASE_TYPE_USER_AND_STATUS = " u.id = :userId and l.type = :type and l.status = :status group by u ";
    String SELECT_COLUMNS_FIND_ALL = " select new com.dev.finances.api.dto.ReleasesDTO(l.id, l.description, l.mes, l.ano, l.value, l.user.id, l.type, l.status, l.releaseDate) ";
    String WHERE_FIND_ALL = " l.user.id = :userId ";

    @Query(value = "select sum(l.value) from Release l join l.user u where l.createAt between :startDate and :endDate and " + WHERE_RELEASE_TYPE_USER_AND_STATUS)
    BigDecimal getBalanceByReleaseTypeUserAndStatus(
            @Param("userId") Long userId,
            @Param("type") ReleaseTypeEnum type,
            @Param("status") ReleaseStatusEnum status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query(value = "select l from Release l join l.user u where u.id = :userId and l.createAt between :startDate and :endDate order by l.id desc")
    List<Optional<Release>> lastReleases(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query(value = SELECT_COLUMNS_FIND_ALL + "from Release l where " + WHERE_FIND_ALL,
            countQuery = "select count(l.id) from Release l where " + WHERE_FIND_ALL)
    Page<ReleasesDTO> findAll(@Param("userId") Long userId, Pageable pageable);
}
