package com.mlconf.core.application.usecase;

import com.mlconf.core.application.dto.ScanRequestDTO;
import com.mlconf.core.application.dto.SessionSnapshotDTO;

import java.util.UUID;

public interface RegisterScanUseCase {

    SessionSnapshotDTO execute(UUID sessionId, ScanRequestDTO request);
}
