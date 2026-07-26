package org.example.repository;

import java.util.UUID;
import org.example.entity.CorrectionTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CorrectionTaskRepository extends JpaRepository<CorrectionTask, UUID> {}
