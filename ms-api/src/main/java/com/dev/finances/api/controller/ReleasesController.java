package com.dev.finances.api.controller;

import com.dev.finances.api.dto.ReleasesDTO;
import com.dev.finances.exception.BusinessException;
import com.dev.finances.model.entity.Release;
import com.dev.finances.model.entity.User;
import com.dev.finances.model.enums.ReleaseStatusEnum;
import com.dev.finances.service.ReleaseService;
import com.dev.finances.service.UserService;
import com.dev.finances.utils.DateUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/releases")
@RequiredArgsConstructor
public class ReleasesController {

    private final ReleaseService service;
    private final UserService userService;

    @GetMapping
    public ResponseEntity search(
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "ano", required = false) Integer ano,
            @RequestParam("userId") Long userId
    ) {
        Release releaseFilter = new Release();
        releaseFilter.setDescription(description);
        releaseFilter.setMes(mes);
        releaseFilter.setAno(ano);

        Optional<User> user = userService.getById(userId);
        if (!user.isPresent()) {
            return ResponseEntity.badRequest().body("Não foi possível realizar a consulta.");
        } else {
            releaseFilter.setUser(user.get());
        }

        List<Release> lancamentos = service.find(releaseFilter);
        return ResponseEntity.ok(lancamentos);
    }

    @GetMapping("{id}")
    public ResponseEntity getRelease(@PathVariable("id") Long id) {
        return service.findById(id)
                .map(release -> new ResponseEntity(converter(release), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/last-releases/{userId}")
    public ResponseEntity lastReleases(@PathVariable("userId") @NonNull Long userId) {
        return ResponseEntity.ok(service.lastReleases(userId));
    }

    @GetMapping("{userId}/releases-paginated")
    public ResponseEntity releasesPaginated(@PathVariable("userId") @NonNull Long userId,
                                            @RequestParam("page") Integer page,
                                            @RequestParam("size") Integer size) {
        return ResponseEntity.ok(service.getReleasesPaginated(userId, page, size));
    }

    @PostMapping("/create-release")
    public ResponseEntity create(@RequestBody ReleasesDTO dto) {
        try {
            Release release = converter(dto);
            release = service.save(release);
            return new ResponseEntity(release, HttpStatus.CREATED);
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity update(@RequestBody ReleasesDTO dto) {
        return service.findById(dto.getId()).map(entity -> {

            try {
                Release release = converter(dto);
                release.setId(entity.getId());
                service.update(release);

                return ResponseEntity.ok(release);
            } catch (BusinessException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }

        }).orElseGet(() ->
                new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
    }

    @PutMapping("{id}/update-status")
    public ResponseEntity updateStatus(@PathVariable("id") Long id,
                                       @RequestParam("status") ReleaseStatusEnum status) {
        return service.findById(id).map(entity -> {
            ReleaseStatusEnum selectedStatus = ReleaseStatusEnum.valueOf(status.name());

            if (selectedStatus == null) {
                return ResponseEntity.badRequest().body("Não foi possível atualizar o status do lançamentos, informe um status válido");
            }

            try {
                entity.setStatus(selectedStatus);
                service.update(entity);
                return ResponseEntity.ok(entity);
            } catch (BusinessException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }).orElseGet(() ->
                new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
    }

    @DeleteMapping("{id}")
    public ResponseEntity deletar(@PathVariable("id") Long id) {
        return service.findById(id).map(entity -> {
            service.delete(entity);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }).orElseGet(() ->
                new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
    }

    private ReleasesDTO converter(Release release) {
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

    private Release converter(ReleasesDTO dto) {
        User user = userService
                .getById(dto.getUserId())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado para o Id informado."));

        Release release = new Release();
        release.setId(dto.getId());
        release.setDescription(dto.getDescription());
        release.setAno(dto.getYear());
        release.setMes(dto.getMouth());
        release.setValue(dto.getValue());
        release.setReleaseDate(DateUtils.dateFormatDefault(dto.getReleaseDate()));
        release.setUser(user);

        if (dto.getType() != null)
            release.setType(dto.getType());
        if (dto.getStatus() != null)
            release.setStatus(dto.getStatus());

        return release;
    }
}
