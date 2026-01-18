package com.mlconf.core.application.dto;

import java.util.Objects;

public record ScanRequestDTO(String packageId) {

    public ScanRequestDTO {
        if (packageId == null || packageId.isBlank()) {
            throw new IllegalArgumentException("packageId must not be blank");
        }
    }
}
