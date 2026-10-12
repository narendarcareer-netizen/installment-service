package com.narender.installment.web;

import com.narender.installment.dto.CreatePlanRequest;
import com.narender.installment.dto.PlanResponse;
import com.narender.installment.service.InstallmentPlanService;
import com.narender.installment.service.PlanNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST API for installment plans.
 *
 *   POST   /api/plans            create a plan
 *   GET    /api/plans?cardId=..  list plans for a card
 *   GET    /api/plans/{id}       get one plan
 *   POST   /api/plans/{id}/cancel  cancel a plan
 */
@RestController
@RequestMapping("/api/plans")
public class InstallmentPlanController {

    private final InstallmentPlanService service;

    public InstallmentPlanController(InstallmentPlanService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PlanResponse> create(@Valid @RequestBody CreatePlanRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createPlan(request));
    }

    @GetMapping
    public List<PlanResponse> list(@RequestParam String cardId) {
        return service.plansForCard(cardId);
    }

    @GetMapping("/{id}")
    public PlanResponse get(@PathVariable UUID id) {
        return service.getPlan(id);
    }

    @PostMapping("/{id}/cancel")
    public PlanResponse cancel(@PathVariable UUID id) {
        return service.cancelPlan(id);
    }

    @ExceptionHandler(PlanNotFoundException.class)
    public ResponseEntity<Map<String, String>> notFound(PlanNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> badState(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", e.getMessage()));
    }
}
