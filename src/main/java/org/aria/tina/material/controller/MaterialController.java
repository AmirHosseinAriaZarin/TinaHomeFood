package org.aria.tina.material.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.aria.tina.common.response.ApiResponse;
import org.aria.tina.common.response.PageResponse;
import org.aria.tina.common.exception.BusinessException;
import org.aria.tina.material.dto.MaterialDTO;
import org.aria.tina.material.service.MaterialService;
import org.aria.tina.material.validator.MaterialValidator;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/materials")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialService service;
    private final MaterialValidator validator;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<MaterialDTO>> create(@Valid @RequestBody MaterialDTO dto) {
        validator.validateForInsert(dto);
        MaterialDTO created = service.create(dto);
        return ResponseEntity.ok().body(ApiResponse.success(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MaterialDTO>> update(@PathVariable Long id, @Valid @RequestBody MaterialDTO dto) throws BusinessException {
        validator.validateForUpdate(id, dto);
        MaterialDTO updated = service.update(id, dto);
        return ResponseEntity.ok().body(ApiResponse.success(updated));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().body(ApiResponse.success(null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MaterialDTO>> findById(@PathVariable Long id) throws BusinessException {
        MaterialDTO materialDTO = service.findById(id);
        return ResponseEntity.ok().body(ApiResponse.success(materialDTO));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<MaterialDTO>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<MaterialDTO> materialDTOPageResponse = service.findAll(pageable);
        return ResponseEntity.ok().body(ApiResponse.success(materialDTOPageResponse));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<MaterialDTO>>> searchByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<MaterialDTO> materialDTOPageResponse = service.searchByName(name, pageable);
        return ResponseEntity.ok().body(ApiResponse.success(materialDTOPageResponse));
    }
}