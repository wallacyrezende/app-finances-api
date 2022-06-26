package com.dev.finances.api.controller;

import com.dev.finances.api.dto.UserAuthenticated;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.dev.finances.api.dto.UserDTO;
import com.dev.finances.exception.AuthenticationException;
import com.dev.finances.exception.BusinessException;
import com.dev.finances.model.entity.User;
import com.dev.finances.service.ReleaseService;
import com.dev.finances.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {

	static final String API = "/api/usuarios";
	static final MediaType JSON = MediaType.APPLICATION_JSON;
	
	
	@Autowired
	MockMvc mvc;
	
	@MockBean
	UserService service;
	
	@MockBean
	ReleaseService releaseService;
	
	@Test
	public void deveAutenticarUmUsuario() throws Exception {
		//cenario
		String name = "theo";
		String email = "usuario@email.com";
		String password = "123";
		UserDTO dto = UserDTO.builder().email(email).password(password).build();
		UserAuthenticated user = UserAuthenticated.builder().id(1l).name(name).email(email).build();
		Mockito.when(service.auth(email, password)).thenReturn(user);
		String json = new ObjectMapper().writeValueAsString(dto);
		
		//execucao e verificacao
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
			.post(API.concat("/autenticar"))
			.accept(JSON)
			.contentType(JSON)
			.content(json);
		
		mvc.perform(request)
		   .andExpect( MockMvcResultMatchers.status().isOk() )
		   .andExpect( MockMvcResultMatchers.jsonPath("id").value(user.getId()) )
		   .andExpect( MockMvcResultMatchers.jsonPath("name").value(user.getName()) )
		   .andExpect( MockMvcResultMatchers.jsonPath("email").value(user.getEmail()) );
	}
	
	@Test
	public void deveRetornarBadRequestAoObterErroDeAutenticacao() throws Exception {
		//cenario
		String email = "usuario@email.com";
		String password = "123";
		UserDTO dto = UserDTO.builder().email(email).password(password).build();

		Mockito.when(service.auth(email, password)).thenThrow(AuthenticationException.class);
		String json = new ObjectMapper().writeValueAsString(dto);
		
		//execucao e verificacao
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
			.post(API.concat("/autenticar"))
			.accept(JSON)
			.contentType(JSON)
			.content(json);
		
		mvc.perform(request)
		   .andExpect( MockMvcResultMatchers.status().isBadRequest() );
	}
	
	@Test
	public void deveCriarUmNovoUsuario() throws Exception {
		//cenario
		String email = "usuario@email.com";
		String password = "123";
		UserDTO dto = UserDTO.builder().email(email).password(password).build();
		User user = User.builder().id(1l).email(email).password(password).build();
		
		Mockito.when(service.save(Mockito.any(User.class))).thenReturn(user);
		String json = new ObjectMapper().writeValueAsString(dto);
		
		//execucao e verificacao
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
			.post(API)
			.accept(JSON)
			.contentType(JSON)
			.content(json);
		
		mvc.perform(request)
		   .andExpect( MockMvcResultMatchers.status().isCreated() )
		   .andExpect( MockMvcResultMatchers.jsonPath("id").value(user.getId()) )
		   .andExpect( MockMvcResultMatchers.jsonPath("nome").value(user.getName()) )
		   .andExpect( MockMvcResultMatchers.jsonPath("email").value(user.getEmail()) );
	}
	
	@Test
	public void deveRetornarBadRequestAoTentarCriarUmUsuarioInvalido() throws Exception {
		//cenario
		String email = "usuario@email.com";
		String password = "123";
		UserDTO dto = UserDTO.builder().email(email).password(password).build();
		
		Mockito.when(service.save(Mockito.any(User.class))).thenThrow(BusinessException.class);
		String json = new ObjectMapper().writeValueAsString(dto);
		
		//execucao e verificacao
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
			.post(API)
			.accept(JSON)
			.contentType(JSON)
			.content(json);
		
		mvc.perform(request)
		   .andExpect( MockMvcResultMatchers.status().isBadRequest() );
	}
}
