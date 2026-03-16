package com.segurosbolivar.polizasapp.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "x-api-key";

    private final AppProperties appProperties;

    public ApiKeyFilter(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        if (path.startsWith("/h2-console")) {
            filterChain.doFilter(request, response);
            return;
        }

        String apiKey = request.getHeader(API_KEY_HEADER);
        if (apiKey == null || !apiKey.equals(appProperties.getApiKey())) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"error\": \"API key inválida o ausente\", \"status\": 401}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}