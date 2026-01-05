package com.mlconf.application.usecase;

import com.mlconf.application.dto.ImportRequestDTO;
import com.mlconf.application.dto.SessionSnapshotDTO;

import java.util.UUID;

public interface ImportConferenceListUseCase {

    SessionSnapshotDTO execute(UUID sessionId, ImportRequestDTO request);
}
