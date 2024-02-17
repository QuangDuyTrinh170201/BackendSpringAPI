package com.example.BackendSpringAPI.services;

import com.example.BackendSpringAPI.dtos.UpdateUserDTO;
import com.example.BackendSpringAPI.dtos.UserDTO;
import com.example.BackendSpringAPI.exceptions.DataNotFoundException;
import com.example.BackendSpringAPI.models.User;

import java.util.List;


public interface IUserService {
    User CreateUser(UserDTO userDTO) throws Exception;
    String login(String email, String password, Long roleId) throws Exception;

    User getUserDetailsFromToken(String extractedToken) throws Exception;
//    String login(String email, String password) throws Exception;

    public User updateUser(Long userId, UpdateUserDTO updatedUserDTO) throws Exception;

    public List<User> getAllUsers() throws Exception;

    public User updateUserInforByAdmin(Long userId, UpdateUserDTO updatedUserDTO) throws Exception;

}
