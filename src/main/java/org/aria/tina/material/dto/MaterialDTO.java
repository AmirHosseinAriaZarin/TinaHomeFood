package org.aria.tina.material.dto;

public record MaterialDTO(
        Long id,
        String name,
        Integer currentStock,
        Integer minimumStock) {
}
