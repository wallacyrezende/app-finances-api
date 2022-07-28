package com.dev.finances.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import com.dev.finances.api.dto.UserAuthenticated;
import com.dev.finances.model.entity.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.dev.finances.exception.AuthenticationException;
import com.dev.finances.exception.BusinessException;
import com.dev.finances.model.entity.User;
import com.dev.finances.repository.UserRepository;
import com.dev.finances.service.impl.UserServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UserServiceTest {
	
	@SpyBean
	UserServiceImpl service;
	
	@MockBean
	UserRepository repository;
	
	public static final String NAME = "name";
	public static final String EMAIL = "email@email.com";
	public static final String PASSWORD = "password";

	@Test
	public void deveSalvarUmUsuario() {
		Mockito.doNothing().when(service).validateEmail(Mockito.anyString());
		User user = User.builder()
								 .id(1l)
								 .name(NAME)
								 .email(EMAIL)
								 .password("senha")
								 .build();
		
		Assertions.assertDoesNotThrow(() -> {
			
			Mockito.when(repository.save(Mockito.any(User.class))).thenReturn(user);
			User userSalvo = service.save(new User());
			
			Assertions.assertEquals(userSalvo != null, userSalvo != null);
			Assertions.assertEquals(1l, userSalvo.getId());
			Assertions.assertEquals("nome", userSalvo.getName());
			Assertions.assertEquals(EMAIL, userSalvo.getEmail());
			Assertions.assertEquals("senha", userSalvo.getPassword());
		});		
	}
	
	@Test
	public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
		User user = User.builder().email(EMAIL).build();
		Mockito.doThrow(BusinessException.class).when(service).validateEmail(EMAIL);
		
		
		
		Assertions.assertThrows(BusinessException.class, () -> {
			service.save(user);
		});
		
		Mockito.verify( repository, Mockito.never() ).save(user);
	}
	
	@Test
	public void deveAutenticarUmUsuarioComSucesso() {
//		cenario
		User user = User.builder().email(EMAIL).password(PASSWORD).id(1l).build();
		Mockito.when(repository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
		

		Assertions.assertDoesNotThrow(() -> {
//			acao
			UserAuthenticated result = service.auth(EMAIL, PASSWORD);
		
//			verificacao
			Assertions.assertNotNull(result);
		});
	}
	
	@Test
	public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComOEmailInformado() {
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
		
		Exception exception = Assertions.assertThrows(AuthenticationException.class, () -> {
			service.auth(EMAIL, "senha");
		});
		
		assertEquals("Usuário não encontrado para o e-mail informado.", exception.getMessage());
		
//		verificacao junit 4
//		Throwblw exception = Assertions.assertCatchThrowable( () -> service.autenticar(EMAIL, "senha"));
//		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Usuário não encontrado para o e-mail informado.");
	}
	
	@Test
	public void deveLancarErroQuandoSenhaNaoCorresponder() {
		String password = "senha";
		User user = User.builder()
								 .email(EMAIL)
								 .password(password)
								 .build();
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(user));
		
	    Exception exception = Assertions.assertThrows(AuthenticationException.class, () -> {
			service.auth(EMAIL, "123");
		});
	    
	 	assertEquals("Senha inválida.", exception.getMessage());
	}
	
	@Test
	public void deveValidarEmail() {
//		cenario
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
		
//		ação
		Assertions.assertDoesNotThrow(() -> service.validateEmail(EMAIL));
	}
	
	@Test
	public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
		
		Assertions.assertThrows(BusinessException.class, () -> service.validateEmail(EMAIL));
	}

	public static User createUser() {
		return User.builder().id(1L)
				.name(NAME)
				.email(EMAIL)
				.password(PASSWORD)
				.build();
	}

}
