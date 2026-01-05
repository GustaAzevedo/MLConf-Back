package com.mlconf.application.usecase;

import com.mlconf.application.dto.ScanRequestDTO;
import com.mlconf.application.dto.SessionSnapshotDTO;

import java.util.UUID;

public interface RegisterScanUseCase {

    SessionSnapshotDTO execute(UUID sessionId, ScanRequestDTO request);
}
