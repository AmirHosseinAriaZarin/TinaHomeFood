package org.aria.tina.material.service;

import org.aria.tina.common.response.PageResponse;
import org.aria.tina.common.exception.BusinessException;
import org.aria.tina.material.dto.MaterialDTO;
import org.springframework.data.domain.Pageable;

public interface MaterialService {
    MaterialDTO create(MaterialDTO dto);
    MaterialDTO update(Long id, MaterialDTO dto) throws BusinessException;
    void delete(Long id);
    MaterialDTO findById(Long id) throws BusinessException;
    PageResponse<MaterialDTO> findAll(Pageable pageable);
    PageResponse<MaterialDTO> searchByName(String name, Pageable pageable);
}