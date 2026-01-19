package com.mlconf.core.application.dto;

public record ScanRequestDTO(String packageId) {

    public ScanRequestDTO {
        if (packageId == null || packageId.isBlank()) {
            throw new IllegalArgumentException("packageId must not be blank");
        }
    }
}
