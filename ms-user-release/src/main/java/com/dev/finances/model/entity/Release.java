package com.dev.finances.model.entity;

import com.dev.finances.model.enums.ReleaseStatusEnum;
import com.dev.finances.model.enums.ReleaseTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;


@Entity
@Table(name = "release", schema = "financas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Release {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long id;

	@Column(nullable = false)
	private String description;

	@Column
	private Integer mes;

	@Column
	private Integer ano;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Column
	private BigDecimal value;

	@Column
	@Convert(converter = Jsr310JpaConverters.LocalDateConverter.class)
	private LocalDate createAt;

	@Column(nullable = false)
	private Date releaseDate;

	@Column
	@Enumerated(value = EnumType.STRING)
	private ReleaseTypeEnum type;

	@Column
	@Enumerated(value = EnumType.STRING)
	private ReleaseStatusEnum status;
}
