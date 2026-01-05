package com.mlconf.application.dto;

import java.util.List;
import java.util.Objects;

public record ImportRequestDTO(List<String> packageIds) {

    public ImportRequestDTO {
        Objects.requireNonNull(packageIds, "packageIds must not be null");
    }
}
