package org.aria.tina.material.validator;

import org.aria.tina.material.dto.MaterialDTO;

public interface MaterialValidator {
    void validateForInsert(MaterialDTO material);

    void validateForUpdate(Long id, MaterialDTO material);
}
