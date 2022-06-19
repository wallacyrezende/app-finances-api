package com.dev.finances.model.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

import com.dev.finances.repository.ReleaseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.dev.finances.model.entity.Release;
import com.dev.finances.model.enums.ReleaseStatusEnum;
import com.dev.finances.model.enums.ReleaseTypeEnum;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class ReleaseRepositoryTest {

	@Autowired
    ReleaseRepository repository;

	@Autowired
	TestEntityManager entityManager;

	@Test
	public void deveSalvarUmLancamento() {
		Release lancamento = criarLancamento();
		lancamento = repository.save(lancamento);

		assertThat(lancamento.getId()).isNotNull();
	}

	@Test
	public void deveDeletarUmLancamento() {
		Release lancamento = criarEPersistirLancamento();

		lancamento = entityManager.find(Release.class, lancamento.getId());
		repository.delete(lancamento);

		Release lancamentoInexistente = entityManager.find(Release.class, lancamento.getId());
		assertThat(lancamentoInexistente).isNull();
	}

	@Test
	public void deveAtualizarUmLancamento() {
		Release lancamento = criarEPersistirLancamento();

		lancamento.setAno(2018);
		lancamento.setDescription("Teste atualizar");
		lancamento.setStatus(ReleaseStatusEnum.CANCELADO);

		repository.save(lancamento);

		Release lancamentoAtualizado = entityManager.find(Release.class, lancamento.getId());
		assertThat(lancamentoAtualizado.getAno()).isEqualTo(2018);
		assertThat(lancamentoAtualizado.getDescription()).isEqualTo("Teste atualizar");
		assertThat(lancamentoAtualizado.getStatus()).isEqualTo(ReleaseStatusEnum.CANCELADO);

	}

	@Test
	public void deveBuscarUmLancamentoPorId() {
		Release lancamento = criarEPersistirLancamento();

		Optional<Release> lancamentoEncontrado = repository.findById(lancamento.getId());

		assertThat(lancamentoEncontrado.isPresent()).isTrue();
	}

	private Release criarEPersistirLancamento() {
		Release lancamento = criarLancamento();
		entityManager.persist(lancamento);
		return lancamento;
	}

	public static Release criarLancamento() {
		return Release.builder()
				.ano(2019)
				.mes(1)
				.description("lancamento qualquer")
				.value(BigDecimal.valueOf(10))
				.type(ReleaseTypeEnum.RECEITA)
				.status(ReleaseStatusEnum.PENDENTE)
				.createAt(LocalDate.now())
				.build();
	}
}
