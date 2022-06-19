package com.dev.finances.api.controller;

import com.dev.finances.api.dto.UserAuthenticated;
import com.dev.finances.api.dto.UserDTO;
import com.dev.finances.exception.AuthenticationException;
import com.dev.finances.exception.BusinessException;
import com.dev.finances.model.entity.User;
import com.dev.finances.model.enums.ReleaseTypeEnum;
import com.dev.finances.service.ReleaseService;
import com.dev.finances.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final ReleaseService releaseService;

    @PostMapping("/auth")
    public ResponseEntity auth(@RequestBody UserDTO dto) {

        try {
            UserAuthenticated userAuthenticated = service.auth(dto.getEmail(), dto.getPassword());
            return ResponseEntity.ok(userAuthenticated);
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping(value = "/search")
    public ResponseEntity<User> findByEmail(@RequestParam String email) {
        User user = service.findByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity save(@RequestBody UserDTO dto) {

        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .build();
        try {
            return new ResponseEntity(service.save(user), HttpStatus.CREATED);
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("{id}/balance")
    public ResponseEntity getBalance(@PathVariable("id") Long id) {
        Optional<User> user = service.getById(id);

        if (!user.isPresent())
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        BigDecimal balance = releaseService.getBalanceByUser(id);
        return ResponseEntity.ok(balance);
    }

    @GetMapping("{userId}/extract")
    public ResponseEntity getExtract(@PathVariable("userId") Long userId,
                                     @RequestParam("releaseType") ReleaseTypeEnum releaseType) {
        Optional<User> user = service.getById(userId);

        if (!user.isPresent())
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        BigDecimal extract = releaseService.getExtractByReleaseType(userId, releaseType);
        return ResponseEntity.ok(extract);
    }

}
