package com.rs.mv.rockit.beans;

import com.rs.mv.rockit.exception.APIErrorFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The Entry Point will not redirect to any sort of Login - it will return the 401
 */
@Component
public class HttpAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        APIErrorFactory errorFactory = new APIErrorFactory();
        errorFactory.setPrintStacktrace(false);
        response.getWriter().write(errorFactory.buildError(HttpStatus.UNAUTHORIZED.value(), authException.getMessage(), authException).getAsJSON());
    }
}