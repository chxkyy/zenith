package com.zenith.admin.interceptor;

import com.alibaba.cola.dto.Response;
import com.alibaba.fastjson2.JSON;
import com.zenith.admin.config.RedisSessionRepository;
import com.zenith.admin.context.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final RedisSessionRepository sessionRepository;

    public AuthInterceptor(RedisSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        if (path.startsWith("/api/auth/login") || path.startsWith("/api/auth/register")) {
            return true;
        }

        HttpSession session = request.getSession(false);

        if (session == null) {
            sendUnauthorizedResponse(response, "NOT_LOGIN", "未登录或会话已过期");
            return false;
        }

        String sessionId = session.getId();
        if (sessionRepository.isKicked(sessionId)) {
            sendUnauthorizedResponse(response, "KICKED_OUT", "您已在其他设备登录");
            return false;
        }

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            sendUnauthorizedResponse(response, "NOT_LOGIN", "未登录或会话已过期");
            return false;
        }

        request.setAttribute("userId", userId);
        UserContext.setUserId(userId);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String errCode, String errMessage) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json; charset=UTF-8");

        Response errorResponse = Response.buildFailure(errCode, errMessage);
        String json = JSON.toJSONString(errorResponse);

        response.getWriter().write(json);
        response.getWriter().flush();
    }
}
