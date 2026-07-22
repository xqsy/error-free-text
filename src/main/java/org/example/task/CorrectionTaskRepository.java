package org.example.task;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CorrectionTaskRepository extends JpaRepository<CorrectionTask, UUID> {}
