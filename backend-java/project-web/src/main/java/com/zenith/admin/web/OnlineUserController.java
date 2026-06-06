package com.zenith.admin.web;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.config.RedisSessionRepository;
import com.zenith.admin.config.RedisSessionRepository.RedisSession;
import com.zenith.admin.dto.cmd.ForceLogoutCmd;
import com.zenith.admin.dto.data.OnlineUserDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/online-users")
@RequiredArgsConstructor
public class OnlineUserController {

    private final RedisSessionRepository sessionRepository;

    @PostMapping("/force-logout")
    public Response forceLogout(@RequestBody ForceLogoutCmd cmd) {
        sessionRepository.kickSession(cmd.getSessionId());
        return Response.buildSuccess();
    }

    @GetMapping("/my-sessions")
    public SingleResponse<List<OnlineUserDTO>> getMySessions(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return SingleResponse.buildFailure("NOT_LOGIN", "未登录");
        }

        Long userId = null;
        Object userIdAttr = session.getAttribute("userId");
        if (userIdAttr instanceof Number) {
            userId = ((Number) userIdAttr).longValue();
        }
        if (userId == null) {
            return SingleResponse.buildFailure("NOT_LOGIN", "未登录");
        }

        String currentSessionId = session.getId();

        Map<String, RedisSession> userSessions = sessionRepository.findByUserId(userId);
        List<OnlineUserDTO> result = new ArrayList<>();

        for (RedisSession redisSession : userSessions.values()) {
            OnlineUserDTO dto = new OnlineUserDTO();
            dto.setSessionId(redisSession.getId());

            Long sessionUserId = null;
            Object sessionUserIdAttr = redisSession.getAttribute("userId");
            if (sessionUserIdAttr instanceof Number) {
                sessionUserId = ((Number) sessionUserIdAttr).longValue();
            }
            dto.setUserId(sessionUserId);
            dto.setUsername((String) redisSession.getAttribute("username"));
            dto.setIp((String) redisSession.getAttribute("ip"));
            dto.setUserAgent((String) redisSession.getAttribute("userAgent"));
            Object sessionLoginTimeAttr = redisSession.getAttribute("loginTime");
            if (sessionLoginTimeAttr instanceof Number) {
                dto.setLoginTime(((Number) sessionLoginTimeAttr).longValue());
            }
            dto.setCurrent(redisSession.getId().equals(currentSessionId));

            if (redisSession.getLastAccessedTime() != null) {
                dto.setLastAccessTime(redisSession.getLastAccessedTime().toEpochMilli());
            }

            dto.setLocation(parseLocation(dto.getIp()));
            dto.setBrowser(parseBrowser(dto.getUserAgent()));

            result.add(dto);
        }

        return SingleResponse.of(result);
    }

    @PostMapping("/kick-my-session")
    public Response kickMySession(@RequestBody ForceLogoutCmd cmd, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return Response.buildFailure("NOT_LOGIN", "未登录");
        }

        Long userId = null;
        Object userIdAttr = session.getAttribute("userId");
        if (userIdAttr instanceof Number) {
            userId = ((Number) userIdAttr).longValue();
        }
        if (userId == null) {
            return Response.buildFailure("NOT_LOGIN", "未登录");
        }

        String currentSessionId = session.getId();
        if (cmd.getSessionId().equals(currentSessionId)) {
            return Response.buildFailure("CANNOT_KICK_CURRENT", "不能踢出当前会话，请使用退出登录");
        }

        RedisSession targetSession = sessionRepository.findById(cmd.getSessionId());
        if (targetSession == null || !userId.equals(targetSession.getUserId())) {
            return Response.buildFailure("SESSION_NOT_FOUND", "会话不存在或不属于当前用户");
        }

        sessionRepository.kickSession(cmd.getSessionId());
        return Response.buildSuccess();
    }

    @GetMapping("/list")
    public MultiResponse<OnlineUserDTO> list(@RequestParam(required = false) String username) {
        log.info("=== ONLINE USERS DEBUG START ===");
        List<OnlineUserDTO> result = new ArrayList<>();

        List<RedisSession> sessions = sessionRepository.findAllActiveSessions();
        log.info("Total active sessions from Redis: {}", sessions.size());

        for (RedisSession session : sessions) {
            log.info("Processing session: {}", session.getId());
            log.info("  - Session attributes: {}", session.getAttributes());
            log.info("  - Session userId field: {}", session.getUserId());

            Long userId = null;
            Object userIdAttr = session.getAttribute("userId");
            log.info("  - userIdAttr from getAttribute: {} (type: {})", userIdAttr,
                    userIdAttr != null ? userIdAttr.getClass().getName() : "null");

            if (userIdAttr instanceof Number) {
                userId = ((Number) userIdAttr).longValue();
            }
            if (userId == null) {
                log.info("  - Skipping session (userId is null)");
                continue;
            }

            String sessionUsername = (String) session.getAttribute("username");

            if (StringUtils.hasText(username)
                    && sessionUsername != null
                    && !sessionUsername.toLowerCase().contains(username.toLowerCase())) {
                continue;
            }

            OnlineUserDTO dto = new OnlineUserDTO();
            dto.setSessionId(session.getId());
            dto.setUserId(userId);
            dto.setUsername(sessionUsername);
            dto.setIp((String) session.getAttribute("ip"));
            dto.setUserAgent((String) session.getAttribute("userAgent"));
            Object loginTimeAttr = session.getAttribute("loginTime");
            if (loginTimeAttr instanceof Number) {
                dto.setLoginTime(((Number) loginTimeAttr).longValue());
            }

            if (session.getLastAccessedTime() != null) {
                dto.setLastAccessTime(session.getLastAccessedTime().toEpochMilli());
            }

            dto.setLocation(parseLocation(dto.getIp()));
            dto.setBrowser(parseBrowser(dto.getUserAgent()));

            result.add(dto);
        }

        log.info("Final result size: {}", result.size());
        log.info("=== ONLINE USERS DEBUG END ===");
        return MultiResponse.of(result);
    }

    private String parseBrowser(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            return "-";
        }
        if (userAgent.contains("Chrome")) {
            int start = userAgent.indexOf("Chrome/") + 7;
            int end = userAgent.indexOf(".", start + 6);
            if (start > 6 && end > start) {
                return "Chrome " + userAgent.substring(start, end);
            }
            return "Chrome";
        }
        if (userAgent.contains("Safari") && !userAgent.contains("Chrome")) {
            int start = userAgent.indexOf("Version/") + 8;
            int end = userAgent.indexOf(" ", start);
            if (start > 7 && end > start) {
                return "Safari " + userAgent.substring(start, end);
            }
            return "Safari";
        }
        if (userAgent.contains("Firefox")) {
            int start = userAgent.indexOf("Firefox/") + 8;
            int end = userAgent.indexOf(".", start);
            if (start > 7 && end > start) {
                return "Firefox " + userAgent.substring(start, end);
            }
            return "Firefox";
        }
        if (userAgent.contains("Edg")) {
            return "Edge";
        }
        return "其他浏览器";
    }

    private String parseLocation(String ip) {
        if (ip == null || ip.isEmpty()) {
            return "-";
        }
        if (ip.equals("127.0.0.1") || ip.equals("0:0:0:0:0:0:0:1") || ip.equals("::1")) {
            return "本机";
        }
        if (ip.startsWith("192.168.") || ip.startsWith("10.") || ip.startsWith("172.")) {
            return "内网";
        }
        return "外网";
    }
}
