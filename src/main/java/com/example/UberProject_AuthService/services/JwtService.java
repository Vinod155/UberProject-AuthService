package com.example.UberProject_AuthService.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Service
public class JwtService implements CommandLineRunner {

    @Value("${jwt.expiry}")
    private int expiry;

    @Value("${jwt.secret}")
    private  String secret;

    //this method will return brand-new token to us based on payload
    private String createToken(Map<String,Object> payload,String email)
    {
        Date now=new Date();
        Date expiryDate=new Date(now.getTime()+expiry*1000L);
        return Jwts.builder()
                .claims(payload)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(expiryDate)
                .subject(email)//user
                .signWith(getSignKey())  //corresponding sign key
                .compact()
                ;
    }

    private Claims extractAllPayLoad(String token)
    {


        return Jwts.parser().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
    }

    private <T>T extractClaim(String token,Function<Claims,T> claimsResolver)
    {
        final Claims claims=extractAllPayLoad(token);
        return claimsResolver.apply(claims);
    }

    private String extractEmail(String token)
    {
        return extractClaim(token,Claims::getSubject);
    }

    private Date extractExpiration(String token)
    {
        return extractClaim(token, Claims::getExpiration);
    }

    //this method checks that the token expiry is before the current date or not
    //if true -->token expired
    private Boolean isTokenExpired(String token)
    {
       return extractExpiration(token).before(new Date());
    }

    private Key getSignKey()
    {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    private Boolean validateToken(String token,String email)
    {
        final String userEmailFetchedFromToken=extractEmail(token);
        return(userEmailFetchedFromToken.equals(email) && !isTokenExpired(token));
    }

    private String extractPhoneNumber(String token)
    {
        Claims claim=extractAllPayLoad(token);
        String number= (String) claim.get("phoneNumber");
        return number;
    }

    @Override
    public void run(String... args) throws Exception {
        Map<String,Object> ap=new HashMap<>();
        ap.put("email","a@b.com");
        ap.put("phoneNumber","999999999999");
        String result=createToken(ap,"vinnie");
        System.out.println(result);
        System.out.println(extractPhoneNumber(result));
    }
}
