package com.narender.installment.repo;

import com.narender.installment.domain.InstallmentPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface InstallmentPlanRepository extends JpaRepository<InstallmentPlan, UUID> {
    List<InstallmentPlan> findByCardId(String cardId);
}
