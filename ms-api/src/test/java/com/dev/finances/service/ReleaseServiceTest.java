package com.dev.finances.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Arrays;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.dev.finances.exception.BusinessException;
import com.dev.finances.model.entity.Release;
import com.dev.finances.model.entity.User;
import com.dev.finances.model.enums.ReleaseStatusEnum;
import com.dev.finances.repository.ReleaseRepository;
import com.dev.finances.model.repository.ReleaseRepositoryTest;
import com.dev.finances.service.impl.ReleaseServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class ReleaseServiceTest {
	
	@SpyBean
    ReleaseServiceImpl service;
	
	@MockBean
    ReleaseRepository repository;
	
	@Test
	public void deveSalvarUmLancamento() {
		//cenario
		Release lancamentoASalvar = ReleaseRepositoryTest.criarLancamento();
		Mockito.doNothing().when(service).validate(lancamentoASalvar);
		
		Release lancamentoSalvo = ReleaseRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(ReleaseStatusEnum.PENDENTE);
		Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);
		
		//execucao
		Release lancamento = service.save(lancamentoASalvar);
		
		//verificacao
		Assertions.assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(ReleaseStatusEnum.PENDENTE);
	}
	
	@Test
	public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
		Release lancamentoASalvar = ReleaseRepositoryTest.criarLancamento();
		Mockito.doThrow(BusinessException.class).when(service).validate(lancamentoASalvar);
		
		Assertions.catchThrowableOfType( () -> service.save(lancamentoASalvar), BusinessException.class);
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
	}
	
	@Test
	public void deveAtualizarUmLancamento() {
		//cenario
		Release lancamentoSalvo = ReleaseRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(ReleaseStatusEnum.PENDENTE);
		
		Mockito.doNothing().when(service).validate(lancamentoSalvo);
		Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);
		
		//execucao
		service.update(lancamentoSalvo);
		
		//verificacao
		Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
	}
	
	@Test
	public void deveLancarErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {
		//cenario
		Release lancamentoASalvar = ReleaseRepositoryTest.criarLancamento();
		
		//execucao
		Assertions.catchThrowableOfType( () -> service.update(lancamentoASalvar), NullPointerException.class);
		
		//verificacao
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
	}
	
	@Test
	public void deveDeletarUmLancamento() {
		Release lancamento = ReleaseRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		
		service.delete(lancamento);
		
		Mockito.verify(repository).delete(lancamento);
	}
	
	@Test
	public void deveLancarErroAoTentarDeletarUmLancamentoQueAindaNaoFoiSalvo() {
		Release lancamento = ReleaseRepositoryTest.criarLancamento();
		
		Assertions.catchThrowableOfType(() -> service.delete(lancamento), NullPointerException.class);
		
		Mockito.verify(repository, Mockito.never()).delete(lancamento);
	}
	
	@Test
	public void deveFiltrarLancamentos() {
		Release lancamento = ReleaseRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		
		List<Release> lista = Arrays.asList(lancamento);
		Mockito.when( repository.findAll(Mockito.any(Example.class))).thenReturn(lista);
		
		List<Release> resultado = service.find(lancamento);
		
		Assertions
			.assertThat(resultado)
			.isNotEmpty()
			.hasSize(1)
			.contains(lancamento);
	}
	
	@Test
	public void deveAtualizarOStatusDeUmLancamento() {
		Release lancamento = ReleaseRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		lancamento.setStatus(ReleaseStatusEnum.PENDENTE);
		
		ReleaseStatusEnum novoStatus = ReleaseStatusEnum.EFETIVADO;
		Mockito.doReturn(lancamento).when(service).update(lancamento);
		
		
		service.updateStatus(lancamento, novoStatus);
		
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
		Mockito.verify(service).update(lancamento);
	}
	
	@Test
	public void deveObterUmLancamentoPorId() {
		Long id = 1l;
		Release lancamento = ReleaseRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));
		
		Optional<Release> resultado = service.findById(id);
		
		Assertions.assertThat(resultado.isPresent()).isTrue();
	}
	
	@Test
	public void deveRetornarVazioQuandoOLancamentoNaoExiste() {
		Long id = 1l;
		Release lancamento = ReleaseRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
		
		Optional<Release> resultado = service.findById(id);
		
		Assertions.assertThat(resultado.isPresent()).isFalse();
	}
	
	@Test
	public void deveLancarErrosAoValidarUmLancamento() {
		Release release = new Release();
		
		Throwable erro = Assertions.catchThrowable( () -> service.validate(release) );
		Assertions.assertThat(erro).isInstanceOf(BusinessException.class).hasMessage("Informe uma Descrição válida.");
		
		release.setDescription("");
		erro = Assertions.catchThrowable( () -> service.validate(release) );
		Assertions.assertThat(erro).isInstanceOf(BusinessException.class).hasMessage("Informe uma Descrição válida.");
		
		release.setDescription("Salário");
		erro = Assertions.catchThrowable( () -> service.validate(release) );
		Assertions.assertThat(erro).isInstanceOf(BusinessException.class).hasMessage("Informe um Mês válido.");
		
		release.setMes(0);
		erro = Assertions.catchThrowable( () -> service.validate(release) );
		Assertions.assertThat(erro).isInstanceOf(BusinessException.class).hasMessage("Informe um Mês válido.");
		
		release.setMes(13);
		erro = Assertions.catchThrowable( () -> service.validate(release) );
		Assertions.assertThat(erro).isInstanceOf(BusinessException.class).hasMessage("Informe um Mês válido.");
		
		release.setMes(1);
		erro = Assertions.catchThrowable( () -> service.validate(release) );
		Assertions.assertThat(erro).isInstanceOf(BusinessException.class).hasMessage("Informe um Ano válido.");
		
		release.setAno(202);
		erro = Assertions.catchThrowable( () -> service.validate(release) );
		Assertions.assertThat(erro).isInstanceOf(BusinessException.class).hasMessage("Informe um Ano válido.");
		
		release.setAno(2020);
		erro = Assertions.catchThrowable( () -> service.validate(release) );
		Assertions.assertThat(erro).isInstanceOf(BusinessException.class).hasMessage("Informe um Usuário.");
		
		release.setUser(new User());
		erro = Assertions.catchThrowable( () -> service.validate(release) );
		Assertions.assertThat(erro).isInstanceOf(BusinessException.class).hasMessage("Informe um Usuário.");
		
		release.getUser().setId(1l);
		erro = Assertions.catchThrowable( () -> service.validate(release) );
		Assertions.assertThat(erro).isInstanceOf(BusinessException.class).hasMessage("Informe um Valor válido.");
		
		release.setValue(BigDecimal.ZERO);
		erro = Assertions.catchThrowable( () -> service.validate(release) );
		Assertions.assertThat(erro).isInstanceOf(BusinessException.class).hasMessage("Informe um Valor válido.");
		
		release.setValue(BigDecimal.ONE);
		erro = Assertions.catchThrowable( () -> service.validate(release) );
		Assertions.assertThat(erro).isInstanceOf(BusinessException.class).hasMessage("Informe um Tipo de lançamento.");
	}
}
