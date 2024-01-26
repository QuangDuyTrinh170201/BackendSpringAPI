package com.example.BackendSpringAPI.controllers;

import com.example.BackendSpringAPI.dtos.UserDTO;
import com.example.BackendSpringAPI.dtos.UserLoginDTO;
import com.example.BackendSpringAPI.models.User;
import com.example.BackendSpringAPI.responses.LoginResponse;
import com.example.BackendSpringAPI.services.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.util.List;
import java.util.Locale;


@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;
    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;
    @PostMapping("/register")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDTO userDTO, BindingResult result){
        try{
            if(result.hasErrors()){
                List<String> errorMessage = result.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(errorMessage);
            }
            if(!userDTO.getPassword().equals(userDTO.getRetypePassword())){
                return ResponseEntity.badRequest().body("Password does not match");
            }
            User user = userService.CreateUser(userDTO);
            return ResponseEntity.ok(user);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody UserLoginDTO userLoginDTO, HttpServletRequest request){
        try {
            String token = userService.login(userLoginDTO.getEmail(), userLoginDTO.getPassword(), userLoginDTO.getRoleId());
            //Trả về token trong response
            Locale locale = localeResolver.resolveLocale(request);
            return ResponseEntity.ok(LoginResponse.builder()
                            .message(messageSource.getMessage("user.login.login_successfully", null, locale))
                            .token(token)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                   LoginResponse.builder()
                           .message(e.getMessage())
                           .build()
            );
        }

    }
}
