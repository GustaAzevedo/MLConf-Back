package com.mlconf.application.usecase;

import com.mlconf.application.dto.SessionSnapshotDTO;

import java.util.UUID;

public interface FinalizeSessionUseCase {

    SessionSnapshotDTO execute(UUID sessionId);
}
