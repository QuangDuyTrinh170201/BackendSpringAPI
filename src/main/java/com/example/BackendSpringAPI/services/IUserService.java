package com.example.BackendSpringAPI.services;

import com.example.BackendSpringAPI.dtos.UserDTO;
import com.example.BackendSpringAPI.exceptions.DataNotFoundException;
import com.example.BackendSpringAPI.models.User;


public interface IUserService {
    User CreateUser(UserDTO userDTO) throws DataNotFoundException;
    String login(String email, String password) throws Exception;
}
