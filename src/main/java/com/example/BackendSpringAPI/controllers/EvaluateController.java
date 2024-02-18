package com.example.BackendSpringAPI.controllers;

import com.example.BackendSpringAPI.dtos.EvaluateDTO;
import com.example.BackendSpringAPI.exceptions.DataNotFoundException;
import com.example.BackendSpringAPI.models.Evaluate;
import com.example.BackendSpringAPI.services.EvaluateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/evaluates")
@RequiredArgsConstructor
public class EvaluateController {
    private final EvaluateService evaluateService;

    @PostMapping("/create")
    public ResponseEntity<?> createEvaluate(@Valid @RequestBody EvaluateDTO evaluateDTO) {
        try {
            Evaluate newEvaluate = evaluateService.createEvaluate(evaluateDTO);
            return ResponseEntity.ok(newEvaluate);
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/getAll")
    public ResponseEntity<List<Evaluate>> getAllEvaluates() {
        List<Evaluate> evaluates = evaluateService.getAllEvaluates();
        return ResponseEntity.ok(evaluates);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEvaluateById(@PathVariable("id") Long id) {
        try {
            Evaluate evaluate = evaluateService.getEvaluateById(id);
            return ResponseEntity.ok(evaluate);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEvaluate(@PathVariable Long id) {
        evaluateService.deleteEvaluate(id);
        return ResponseEntity.ok("Delete evaluate with id = " + id + " successfully!");
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Evaluate>> getEvaluatesByUserId(@PathVariable Long userId) {
        List<Evaluate> evaluates = null;
        try {
            evaluates = evaluateService.getEvaluatesByUserId(userId);
        } catch (DataNotFoundException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(evaluates);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Evaluate>> getEvaluatesByProductId(@PathVariable Long productId) {
        List<Evaluate> evaluates = null;
        try {
            evaluates = evaluateService.getEvaluatesByProductId(productId);
        } catch (DataNotFoundException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(evaluates);
    }
}
