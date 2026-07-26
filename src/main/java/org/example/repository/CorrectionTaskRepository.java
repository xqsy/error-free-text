package org.example.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.entity.CorrectionTask;
import org.example.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CorrectionTaskRepository extends JpaRepository<CorrectionTask, UUID> {

  Optional<CorrectionTask> findFirstByStatusOrderByCreatedAtAsc(TaskStatus status);

  List<CorrectionTask> findAllByStatus(TaskStatus status);
}
