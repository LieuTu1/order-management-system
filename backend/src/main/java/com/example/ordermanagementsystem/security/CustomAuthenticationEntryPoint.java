package com.example.ordermanagementsystem.security;

import com.example.ordermanagementsystem.dto.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
import java.io.IOException;


@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        apiResponse.setMessage("Unauthorized");
        apiResponse.setData(null);

        // ObjectMapper -> JSON
        String json = objectMapper.writeValueAsString(apiResponse);
        // Set status code THẬT SỰ lên response
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        // Báo cho client biết body trả về là JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        // GHI nội dung apiResponse (convert thành chuỗi JSON) vào response
        response.getWriter().write(json);
    }
}
