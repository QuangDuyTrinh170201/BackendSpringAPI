package com.example.BackendSpringAPI.components;

import com.example.BackendSpringAPI.exceptions.InvalidParamException;
import com.example.BackendSpringAPI.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.InvalidParameterException;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtTokenUtil {
    @Value("${jwt.expiration}")
    private int expiration; //save vào environment variable
    @Value("${jwt.secretKey}")
    private String secretKey;
    public String generateToken(User user) throws Exception{
        //properties => claims
        Map<String, Object> claims = new HashMap<>();
//        this.generateSecretKey();
        claims.put("email", user.getEmail());
        claims.put("userId", user.getId());
        try{
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(user.getEmail())
                    .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000L))
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                    .compact();
            return token;
        }catch (Exception e){
            //inject logger after
            throw new InvalidParamException("Cannot create jwt token, error: " +e.getMessage());
        }
    }

    private Key getSignInKey(){
        byte[] bytes = Decoders.BASE64.decode(secretKey); //Decoders.BASE64.decode("Jie84xS69pxHX5ISznjCODmrGpWJsoSO3uXd/8MZBSA=")
        return Keys.hmacShaKeyFor(bytes);//Keys.hmacShaKeyFor(Decoders.BASE64.decode("Jie84xS69pxHX5ISznjCODmrGpWJsoSO3uXd/8MZBSA="))
    }

    private String generateSecretKey(){
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[32];
        random.nextBytes(keyBytes);
        String secretKey = Encoders.BASE64.encode(keyBytes);
        return secretKey;
    }

    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = this.extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    //check expiration
    public boolean isTokenExpired(String token){
        Date expirationDate = this.extractClaim(token, Claims::getExpiration);
        return expirationDate.before(new Date());
    }

    public String extractEmail(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public String extractPhoneNumber(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean validateToken(String token, UserDetails userDetails){
        String email = extractEmail(token);
        return (email.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }
}
