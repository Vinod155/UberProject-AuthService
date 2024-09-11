package com.example.UberProject_AuthService.filters;

import com.example.UberProject_AuthService.services.JwtService;
import com.example.UberProject_AuthService.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter  extends OncePerRequestFilter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    private final JwtService jwtService;

    private final RequestMatcher uriMatcher=new AntPathRequestMatcher("/api/v1/auth/validate", HttpMethod.GET.name());

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token=null;
         if(request.getCookies()!=null)
         {
             for(Cookie cookie:request.getCookies())
             {
                 if(cookie.getName().equals("JwtToken"))
                 {
                     token=cookie.getValue();
                 }
             }
         }
        System.out.println("incoming token "+token);
         if(token==null)
         {
             //the user has not provided jwt token then request should not move forward
             response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
             return ;
         }
         String email=jwtService.extractEmail(token);

         if(email!=null)
         {
             UserDetails userDetails=userDetailsService.loadUserByUsername(email);
             if(jwtService.validateToken(token,userDetails.getUsername()))
             {
                 UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken=new UsernamePasswordAuthenticationToken(userDetails,null);
                 usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                 SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);//this will hold the userdetails and we can get this object easily for our usage
             }
         }

         filterChain.doFilter(request,response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request){
        RequestMatcher matcher=new NegatedRequestMatcher(uriMatcher);
        return matcher.matches(request);
    }
}
