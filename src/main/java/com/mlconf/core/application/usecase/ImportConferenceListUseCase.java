package com.mlconf.core.application.usecase;

import com.mlconf.core.application.dto.ImportRequestDTO;
import com.mlconf.core.application.dto.SessionSnapshotDTO;

import java.util.UUID;

public interface ImportConferenceListUseCase {

    SessionSnapshotDTO execute(UUID sessionId, ImportRequestDTO request);
}
