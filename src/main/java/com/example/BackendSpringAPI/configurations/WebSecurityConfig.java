package com.example.BackendSpringAPI.configurations;
import com.example.BackendSpringAPI.filters.JwtTokenFilter;
import com.example.BackendSpringAPI.models.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpMethod.*;

@EnableWebMvc
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    @Value("${api.prefix}")
    private String apiPrefix;
    private final JwtTokenFilter jwtTokenFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(requests -> {
                    requests.requestMatchers(
                                    String.format("%s/users/register", apiPrefix),
                                    String.format("%s/users/login", apiPrefix),
                                    String.format("%s/categories/getAll", apiPrefix)

                            ).permitAll()
                            .requestMatchers(PUT,
                                    String.format("%s/users/updateForAdmin/**", apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(GET,
                                    String.format("%s/products/**", apiPrefix),
                                    String.format("%s/products/all**", apiPrefix),
                                    String.format("%s/products/findByName**", apiPrefix),
                                    String.format("%s/products/images/*", apiPrefix),
                                    String.format("%s/products/category**", apiPrefix)).permitAll()
                            .requestMatchers(DELETE, String.format("%s/products/images/*", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(GET, String.format("%s/roles**", apiPrefix)).permitAll()
                            .requestMatchers(POST,
                                    String.format("%s/categories/**", apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(PUT,
                                    String.format("%s/categories/**", apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(DELETE,
                                    String.format("%s/categories/**", apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(GET, String.format("%s/users/all", apiPrefix)).hasRole(Role.ADMIN)
                            //Product config
//                            .requestMatchers(GET,
//                                    String.format("%s/products**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(POST,
                                    String.format("%s/products/**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(POST,
                                    String.format("%s/products/uploads", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(PUT,
                                    String.format("%s/products/**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(DELETE,
                                    String.format("%s/products/**", apiPrefix)).hasRole(Role.ADMIN)

                            //Order config
                            .requestMatchers(POST,
                                    String.format("%s/orders/**", apiPrefix)).hasAnyRole(Role.USER)
                            .requestMatchers(GET,
                                    String.format("%s/orders/**", apiPrefix)).permitAll()
                            .requestMatchers(PUT,
                                    String.format("%s/orders/**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(DELETE,
                                    String.format("%s/orders/**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(GET,
                                    String.format("%s/orders/get-orders-by-keyword", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(GET,
                                    String.format("%s/orders/get-orders-by-keyword-not-paging", apiPrefix)).hasRole(Role.ADMIN)

                            //Order Details config
                            .requestMatchers(POST,
                                    String.format("%s/order_details/**", apiPrefix)).hasAnyRole(Role.USER)
                            .requestMatchers(GET,
                                    String.format("%s/order_details/**", apiPrefix)).hasAnyRole(Role.USER, Role.ADMIN)
                            .requestMatchers(GET,
                                    String.format("%s/order_details/order/**", apiPrefix)).hasAnyRole(Role.USER, Role.ADMIN)
                            .requestMatchers(PUT,
                                    String.format("%s/order_details/**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(DELETE,
                                    String.format("%s/order_details/**", apiPrefix)).hasRole(Role.ADMIN)

                            //Evaluate config
                            .requestMatchers(GET,
                                    String.format("%s/evaluates/**", apiPrefix)).permitAll()
                            .requestMatchers(POST,
                                    String.format("%s/evaluates/**", apiPrefix)).hasRole(Role.USER)
                            .requestMatchers(DELETE,
                                    String.format("%s/evaluates/**", apiPrefix)).hasRole(Role.ADMIN)
                            .anyRequest().authenticated();
                })
                .csrf(AbstractHttpConfigurer::disable);
        http.cors(new Customizer<CorsConfigurer<HttpSecurity>>() {
            @Override
            public void customize(CorsConfigurer<HttpSecurity> httpSecurityCorsConfigurer) {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(List.of("*"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
                configuration.setExposedHeaders(List.of("x-auth-token"));
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                httpSecurityCorsConfigurer.configurationSource(source);
            }
        });
        return http.build();
    }
}
