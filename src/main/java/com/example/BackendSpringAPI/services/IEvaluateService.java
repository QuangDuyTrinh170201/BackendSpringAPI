package com.example.BackendSpringAPI.services;

import com.example.BackendSpringAPI.dtos.EvaluateDTO;
import com.example.BackendSpringAPI.exceptions.DataNotFoundException;
import com.example.BackendSpringAPI.models.Evaluate;

import java.util.List;

public interface IEvaluateService {
    public Evaluate createEvaluate(EvaluateDTO evaluateDTO) throws DataNotFoundException;
    Evaluate getEvaluateById(long id);
    List<Evaluate> getAllEvaluates();
    List<Evaluate> getEvaluatesByUserId(Long userId) throws DataNotFoundException;
    List<Evaluate> getEvaluatesByProductId(Long productId) throws DataNotFoundException;
    void deleteEvaluate(long id);
}

