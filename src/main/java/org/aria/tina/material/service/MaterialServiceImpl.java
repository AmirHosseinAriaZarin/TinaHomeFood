package org.aria.tina.material.service;

import lombok.RequiredArgsConstructor;
import org.aria.tina.common.exception.ErrorCodes;
import org.aria.tina.common.response.PageResponse;
import org.aria.tina.common.exception.BusinessException;
import org.aria.tina.material.dto.MaterialDTO;
import org.aria.tina.material.model.Material;
import org.aria.tina.material.repository.MaterialRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MaterialServiceImpl implements MaterialService {

    private final MaterialRepository repository;

    @Override
    public MaterialDTO create(MaterialDTO dto) {
        Material material = new Material();
        material.setName(dto.name());
        material.setCurrentStock(0);
        material.setMinimumStock(0);
        Material saved = repository.save(material);
        return toDto(saved);
    }

    @Override
    public MaterialDTO update(Long id, MaterialDTO dto) throws BusinessException {
        Material material = repository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCodes.MATERIAL_NOT_FOUND));
        material.setName(dto.name());
        Material updated = repository.save(material);
        return toDto(updated);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public MaterialDTO findById(Long id) throws BusinessException {
        Material material = repository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCodes.MATERIAL_NOT_FOUND));
        return toDto(material);
    }

    @Override
    public PageResponse<MaterialDTO> findAll(Pageable pageable) {
        Page<Material> page = repository.findAll(pageable);
        return toPageResponse(page);
    }

    @Override
    public PageResponse<MaterialDTO> searchByName(String name, Pageable pageable) {
        Page<Material> page = repository.findByNameContaining(name, pageable);
        return toPageResponse(page);
    }

    private MaterialDTO toDto(Material entity) {
        return new MaterialDTO(
                entity.getId(),
                entity.getName(),
                entity.getCurrentStock(),
                entity.getMinimumStock()
        );
    }

    private PageResponse<MaterialDTO> toPageResponse(Page<Material> page) {
        return new PageResponse<>(
                page.getContent().stream().map(this::toDto).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast(),
                page.isFirst()
        );
    }
}