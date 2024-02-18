package com.example.BackendSpringAPI.services;

import com.example.BackendSpringAPI.dtos.EvaluateDTO;
import com.example.BackendSpringAPI.exceptions.DataNotFoundException;
import com.example.BackendSpringAPI.models.Evaluate;
import com.example.BackendSpringAPI.models.Product;
import com.example.BackendSpringAPI.models.User;
import com.example.BackendSpringAPI.repositories.EvaluateRepository;
import com.example.BackendSpringAPI.repositories.ProductRepository;
import com.example.BackendSpringAPI.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EvaluateService implements IEvaluateService {
    private final EvaluateRepository evaluateRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Evaluate createEvaluate(EvaluateDTO evaluateDTO) throws DataNotFoundException {
        User user = userRepository.findById(evaluateDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find user with id: " + evaluateDTO.getUserId()));

        Product product = productRepository.findById(evaluateDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find product with id: " + evaluateDTO.getProductId()));

        Evaluate newEvaluate = new Evaluate();
        newEvaluate.setComment(evaluateDTO.getComment());
        newEvaluate.setRate(evaluateDTO.getRate());
        newEvaluate.setUser(user);
        newEvaluate.setProduct(product);

        return evaluateRepository.save(newEvaluate);
    }


    @Override
    public Evaluate getEvaluateById(long id) {
        return evaluateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evaluate not found!"));
    }

    @Override
    public List<Evaluate> getAllEvaluates() {
        return evaluateRepository.findAll();
    }

    @Override
    public List<Evaluate> getEvaluatesByUserId(Long userId) throws DataNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find user with id: " + userId));

        return evaluateRepository.findByUser(user);
    }

    @Override
    public List<Evaluate> getEvaluatesByProductId(Long productId) throws DataNotFoundException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find product with id: " + productId));

        return evaluateRepository.findByProduct(product);
    }


    @Override
    public void deleteEvaluate(long id) {
        evaluateRepository.deleteById(id);
    }
}

