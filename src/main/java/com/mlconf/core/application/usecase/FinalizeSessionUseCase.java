package com.mlconf.core.application.usecase;

import com.mlconf.core.application.dto.SessionSnapshotDTO;

import java.util.UUID;

public interface FinalizeSessionUseCase {

    SessionSnapshotDTO execute(UUID sessionId);
}
