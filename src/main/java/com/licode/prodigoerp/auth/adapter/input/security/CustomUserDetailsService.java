package com.licode.prodigoerp.auth.adapter.input.security;

import com.licode.prodigoerp.auth.application.port.output.LoadUserPort;
import com.licode.prodigoerp.auth.application.port.output.PermissionPersistencePort;
import com.licode.prodigoerp.auth.application.port.output.RoleQueryPort;
import com.licode.prodigoerp.auth.domain.model.User;
import com.licode.prodigoerp.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

   private final LoadUserPort loadUserPort;
   private final RoleQueryPort roleQueryPort;
   private final PermissionPersistencePort permissionPersistencePort;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{

        User user = loadUserPort.findUserByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found with username: " + username));

        return buildPrincipal(user);
    }

    public UserPrincipal buildPrincipal(User user){

        List<String> roles = roleQueryPort.findActiveRoleNames(user.getId());
        List<String> permissions = permissionPersistencePort.findActivePermissionCodes(user.getId());


        // concatenating the roles and permissions in one collection to be used as granted authority
        Collection<GrantedAuthority> authorities = Stream.concat(
                roles.stream().map(r -> "ROLE_" + r),
                permissions.stream().map(p -> "PERM_" + p)
        ).map(SimpleGrantedAuthority::new).collect(Collectors.toSet());

        return new UserPrincipal(user, authorities);
    }

}
