package com.dev.finances.api.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.dev.finances.model.enums.ReleaseStatusEnum;
import com.dev.finances.model.enums.ReleaseTypeEnum;
import com.dev.finances.utils.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReleasesDTO {

	private Long id;
	private String description;
	private Integer mouth;
	private Integer year;
	private BigDecimal value;
	private Long userId;
	private ReleaseTypeEnum type;
	private ReleaseStatusEnum status;
	private String releaseDate;

	public ReleasesDTO (Long id,  String description, Integer mouth, Integer year, BigDecimal value, Long userId,  ReleaseTypeEnum type,
						ReleaseStatusEnum status, Date releaseDate) {
		this.id = id;
		this.description = description;
		this.mouth = mouth;
		this.year = year;
		this.value = value;
		this.userId = userId;
		this.type = type;
		this.status = status;
		this.releaseDate = DateUtils.dateFormatDefault(releaseDate);
	}
}
