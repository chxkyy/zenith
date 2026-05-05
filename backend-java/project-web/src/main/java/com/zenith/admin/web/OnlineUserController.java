package com.zenith.admin.web;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import com.zenith.admin.config.CustomJdbcSessionRepository;
import com.zenith.admin.config.CustomJdbcSessionRepository.CustomSession;
import com.zenith.admin.dto.data.ForceLogoutCmd;
import com.zenith.admin.dto.data.OnlineUserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.session.Session;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/online-users")
@RequiredArgsConstructor
public class OnlineUserController {

    private final CustomJdbcSessionRepository sessionRepository;

    @GetMapping("/list")
    public MultiResponse<OnlineUserDTO> list(@RequestParam(required = false) String username) {
        List<OnlineUserDTO> result = new ArrayList<>();

        List<String> allSessionIds = sessionRepository.findAllActiveSessionIds();

        for (String sessionId : allSessionIds) {
            CustomSession session = sessionRepository.findById(sessionId);
            if (session == null || session.isExpired()) {
                continue;
            }

            Long userId = session.getAttribute("userId");
            if (userId == null) {
                continue;
            }

            String sessionUsername = session.getAttribute("username");

            if (StringUtils.hasText(username)
                    && sessionUsername != null
                    && !sessionUsername.toLowerCase().contains(username.toLowerCase())) {
                continue;
            }

            OnlineUserDTO dto = new OnlineUserDTO();
            dto.setSessionId(session.getId());
            dto.setUserId(userId);
            dto.setUsername(sessionUsername);
            dto.setIp(session.getAttribute("ip"));
            dto.setUserAgent(session.getAttribute("userAgent"));

            Object loginTimeAttr = session.getAttribute("loginTime");
            if (loginTimeAttr instanceof Long lt) {
                dto.setLoginTime(lt / 1000);
            }

            if (session.getLastAccessedTime() != null) {
                dto.setLastAccessTime(session.getLastAccessedTime().toEpochMilli() / 1000);
            }

            dto.setLocation(parseLocation(dto.getIp()));
            dto.setBrowser(parseBrowser(dto.getUserAgent()));

            result.add(dto);
        }

        return MultiResponse.of(result);
    }

    @PostMapping("/force-logout")
    public Response forceLogout(@RequestBody ForceLogoutCmd cmd) {
        sessionRepository.deleteById(cmd.getSessionId());
        return Response.buildSuccess();
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
}
