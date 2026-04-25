package com.zenith.admin.interceptor;

import com.alibaba.cola.dto.Response;
import com.alibaba.fastjson2.JSON;
import com.zenith.admin.api.TokenService;
import com.zenith.admin.dto.data.OnlineUserDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        if (path.startsWith("/api/auth/login") || path.startsWith("/api/auth/register")) {
            return true;
        }

        String token = getTokenFromCookie(request);
        if (token == null) {
            sendUnauthorizedResponse(response);
            return false;
        }

        OnlineUserDTO onlineUser = tokenService.validateToken(token);
        if (onlineUser == null) {
            sendUnauthorizedResponse(response);
            return false;
        }

        request.setAttribute("userId", onlineUser.getUserId());
        request.setAttribute("token", token);

        return true;
    }

    private String getTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("ZENITH_TOKEN".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void sendUnauthorizedResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json; charset=UTF-8");

        Response errorResponse = Response.buildFailure("UNAUTHORIZED", "未授权访问");
        String json = JSON.toJSONString(errorResponse);

        response.getWriter().write(json);
        response.getWriter().flush();
    }
}
