package com.zenith.admin.security;

import com.zenith.admin.api.system.PermissionService;
import com.zenith.admin.api.system.UserService;
import com.zenith.admin.dto.system.data.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;
    private final PermissionService permissionService;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        UserDTO user = userService.getByLoginId(loginId);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + loginId);
        }

        List<String> permissions = permissionService.getUserPermissions(user.getId());

        List<SimpleGrantedAuthority> authorities = permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return User.builder()
                .username(loginId)
                .password("")
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(user.getStatus() != 1)
                .credentialsExpired(false)
                .disabled(user.getStatus() != 1)
                .build();
    }
}
