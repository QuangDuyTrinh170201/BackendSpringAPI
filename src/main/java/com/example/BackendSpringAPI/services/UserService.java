package com.example.BackendSpringAPI.services;

import com.example.BackendSpringAPI.components.JwtTokenUtil;
import com.example.BackendSpringAPI.dtos.UpdateUserDTO;
import com.example.BackendSpringAPI.dtos.UserDTO;
import com.example.BackendSpringAPI.exceptions.DataNotFoundException;
import com.example.BackendSpringAPI.exceptions.PermissionDenyException;
import com.example.BackendSpringAPI.models.Role;
import com.example.BackendSpringAPI.models.User;
import com.example.BackendSpringAPI.repositories.RoleRepository;
import com.example.BackendSpringAPI.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService implements IUserService{
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;
    @Override
    @Transactional
    public User CreateUser(UserDTO userDTO) throws Exception {
        //register user
        String email = userDTO.getEmail();
        //check trùng email
        if(userRepository.existsByEmail(email)){
            throw new DataIntegrityViolationException("Email already exists!");
        }
        Role role = roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(() -> new DataNotFoundException("Role not found!"));
        if(role.getName().toUpperCase().equals(Role.ADMIN)){
            throw new PermissionDenyException("You cannot register an admin account!");
        }
        //convert từ userDto => user
        User newUser = User.builder()
                .fullName(userDTO.getFullName())
                .phoneNumber(userDTO.getPhoneNumber())
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .address(userDTO.getAddress())
                .dateOfBirth(userDTO.getDateOfBirth())
                .facebookAccountId(userDTO.getFacebookAccountId())
                .googleAccountId(userDTO.getGoogleAccountId())
                .active(true)
                .build();

        newUser.setRole(role);
        if(userDTO.getFacebookAccountId() == 0 && userDTO.getGoogleAccountId() == 0){
            String password = userDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            newUser.setPassword(encodedPassword);
        }
        return userRepository.save(newUser);
    }

    @Override
    public String login(String email, String password, Long roleId) throws Exception{
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty()){
            throw new DataNotFoundException("Invalid email or password!");
        }
//        return optionalUser.get(); //muốn trả về jwt token?
        User existingUser = optionalUser.get();
        //check password
        if(existingUser.getFacebookAccountId() == 0 && existingUser.getGoogleAccountId() == 0){
            if(!passwordEncoder.matches(password, existingUser.getPassword())){
                throw new BadCredentialsException("Invalid email or password");
            }
        }

        Optional<Role> optionalRole = roleRepository.findById(roleId);
        if(optionalRole.isEmpty() || !roleId.equals(existingUser.getRole().getId())){
            throw new DataNotFoundException("Your role is not accepted or correct");
        }
        if(!optionalUser.get().isActive()) {
            throw new DataNotFoundException("Your account is locked, please contact admin to unlock!");
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
          email, password,
                existingUser.getAuthorities()
        );
        //authenticate with java spring security
        authenticationManager.authenticate(authenticationToken);
        return jwtTokenUtil.generateToken(existingUser);
    }

    @Override
    public User getUserDetailsFromToken(String token) throws Exception {
        if(jwtTokenUtil.isTokenExpired(token)) {
            throw new Exception("Token is expired");
        }
        String email = jwtTokenUtil.extractEmail(token);
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            return user.get();
        } else {
            throw new Exception("User not found");
        }
    }

    @Transactional
    @Override
    public User updateUser(Long userId, UpdateUserDTO updatedUserDTO) throws Exception{
        //find the existing user by userId
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        String newEmail = updatedUserDTO.getEmail();
        if(!existingUser.getEmail().equals(newEmail) && userRepository.existsByEmail(newEmail)){
            throw new DataIntegrityViolationException("Email already exists");
        }
        if(updatedUserDTO.getFullName() != null){
            existingUser.setFullName(updatedUserDTO.getFullName());
        }
        if(newEmail != null){
            existingUser.setEmail(newEmail);
        }
        if(updatedUserDTO.getPhoneNumber() != null){
            existingUser.setPhoneNumber(updatedUserDTO.getPhoneNumber());
        }
        if(updatedUserDTO.getAddress() != null){
            existingUser.setAddress(updatedUserDTO.getAddress());
        }
        if(updatedUserDTO.getDateOfBirth() != null){
            existingUser.setDateOfBirth(updatedUserDTO.getDateOfBirth());
        }
        if(updatedUserDTO.getFacebookAccountId() > 0){
            existingUser.setFacebookAccountId(updatedUserDTO.getFacebookAccountId());
        }
        if(updatedUserDTO.getGoogleAccountId() > 0){
            existingUser.setGoogleAccountId(updatedUserDTO.getGoogleAccountId());
        }

        if(updatedUserDTO.getPassword() != null && !updatedUserDTO.getPassword().isEmpty()){
            if(!updatedUserDTO.getPassword().equals(updatedUserDTO.getRetypePassword())) {
                throw new DataNotFoundException("Password and retype password not the same");
            }
            String newPassword = updatedUserDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(newPassword);
            existingUser.setPassword(encodedPassword);
        }

        return userRepository.save(existingUser);
    }

    @Transactional
    @Override
    public User updateUserInforByAdmin(Long userId, UpdateUserDTO updatedUserDTO) throws Exception {
        // Tìm người dùng hiện có dựa vào userId
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        // Cập nhật trường isActive nếu có
        Boolean isActive = updatedUserDTO.getIsActive();
        if (isActive != null) {
            existingUser.setActive(isActive);
        }

        // Cập nhật trường roleId nếu có
        Long roleId = updatedUserDTO.getRoleId();
        if (roleId != null) {
            // Truy vấn cơ sở dữ liệu để lấy đối tượng Role tương ứng với roleId
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new DataNotFoundException("Role not found"));

            // Gán đối tượng Role vào trường role của User
            existingUser.setRole(role);
        }

        return userRepository.save(existingUser);
    }



    @Override
    public List<User> getAllUsers() throws Exception {
        try {
            List<User> users = userRepository.findAll();
            if (users.isEmpty()) {
                throw new DataNotFoundException("No users found");
            }
            return users;
        } catch (Exception e) {
            throw new Exception("Failed to retrieve users: " + e.getMessage());
        }
    }


}
