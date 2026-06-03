package com.paylogic.paywalletlite.config.security;

import com.paylogic.paywalletlite.domain.identity.DeviceSession;
import com.paylogic.paywalletlite.domain.identity.User;
import com.paylogic.paywalletlite.domain.identity.enums.RoleType;
import com.paylogic.paywalletlite.domain.identity.enums.SessionStatus;
import com.paylogic.paywalletlite.repository.identity.DeviceSessionRepository;
import com.paylogic.paywalletlite.repository.identity.UserRepository;
import com.paylogic.paywalletlite.security.crypto.HashUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final DeviceSessionRepository sessionRepository;
    private final UserRepository userRepository;

    @Autowired
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider,
                                   DeviceSessionRepository sessionRepository,
                                   UserRepository userRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            System.out.println(">>> URI          : " + request.getRequestURI());
            System.out.println(">>> CONTEXT PATH : " + request.getContextPath());
            System.out.println(">>> SERVLET PATH : " + request.getServletPath());
            System.out.println(">>> PATH INFO    : " + request.getPathInfo());

            System.out.println(">>> JWT FILTER EXECUTED");

            String authHeader = request.getHeader("Authorization");

            System.out.println(">>> AUTH HEADER: " + authHeader);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                System.out.println(">>> NO TOKEN");
                filterChain.doFilter(request, response);
                return;
            }

            String token = authHeader.substring(7);

            System.out.println(">>> TOKEN EXTRACTED");

            if (!jwtTokenProvider.validateToken(token)) {
                System.out.println(">>> INVALID TOKEN");
                filterChain.doFilter(request, response);
                return;
            }

            System.out.println(">>> TOKEN VALID");

            String tokenHash = HashUtil.sha256(token);

            Optional<DeviceSession> sessionOpt =
                    sessionRepository.findByTokenHash(tokenHash);

            System.out.println(">>> SESSION FOUND: " + sessionOpt.isPresent());

            if (!sessionOpt.isPresent()) {
                System.out.println(">>> SESSION NOT FOUND");
                filterChain.doFilter(request, response);
                return;
            }

            DeviceSession session = sessionOpt.get();

            System.out.println(">>> SESSION STATUS: " + session.getStatus());

            if (session.getStatus() != SessionStatus.ACTIVE) {
                System.out.println(">>> SESSION NOT ACTIVE");
                filterChain.doFilter(request, response);
                return;
            }

            String userId = jwtTokenProvider.getUserIdFromToken(token);

            System.out.println(">>> USER ID: " + userId);

            List<GrantedAuthority> authorities =
                    resolveAuthorities(UUID.fromString(userId));

            System.out.println(">>> AUTHORITIES: " + authorities);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            authorities
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

            System.out.println(">>> AUTHENTICATION SET SUCCESSFULLY");

        } catch (Exception e) {

            System.out.println(">>> JWT FILTER ERROR");
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Récupère les autorités (rôles) de l'utilisateur depuis la base.
     */
    private List<GrantedAuthority> resolveAuthorities(UUID userId) {
        Optional<User> userOpt = userRepository.findById(userId);

        if (!userOpt.isPresent()) {
            return Collections.emptyList();
        }

        User user = userOpt.get();
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Mapper RoleType → GrantedAuthority Spring Security
        RoleType role = user.getRole();
        if (role != null) {
            // Ajouter ROLE_ prefix (convention Spring Security)
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
        }

        return authorities;
    }
}