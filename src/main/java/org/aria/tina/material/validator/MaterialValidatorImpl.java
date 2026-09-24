package org.aria.tina.material.validator;

import lombok.RequiredArgsConstructor;
import org.aria.tina.common.exception.BusinessException;
import org.aria.tina.common.exception.ErrorCodes;
import org.aria.tina.material.dto.MaterialDTO;
import org.aria.tina.material.model.Material;
import org.aria.tina.material.repository.MaterialRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MaterialValidatorImpl implements MaterialValidator {
    private final MaterialRepository materialRepository;

    @Override
    public void validateForInsert(MaterialDTO materialDTO) {
        validateDuplicateName(materialDTO.name());
    }

    @Override
    public void validateForUpdate(Long id, MaterialDTO materialDTO) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCodes.MATERIAL_NOT_FOUND));
        if (!material.getName().equals(materialDTO.name())) {
            validateDuplicateName(materialDTO.name());
        }
    }

    private void validateDuplicateName(String name) {
        if (!materialRepository.findByName(name).isEmpty()) {
            throw new BusinessException(ErrorCodes.MATERIAL_ALREADY_EXISTS);
        }
    }
}
