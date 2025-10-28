package com.example.todak_server.jwt;

import com.example.todak_server.entity.Member;
import com.example.todak_server.repository.MemberRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwt;
    private final MemberRepository memberRepository;

    public JwtAuthenticationFilter(JwtTokenProvider jwt, MemberRepository memberRepository) {
        this.jwt= jwt;
        this.memberRepository = memberRepository;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(SecurityContextHolder.getContext().getAuthentication() == null) {
            String h = request.getHeader("Authorization");
            if(h != null && h.regionMatches(true,0,"Bearer ",0,7)) {
                String token = h.substring(7).trim();
                if (jwt.validate(token)) {
                    Long mid = jwt.getMemberId(token);
                    if (mid != null) {
                        Member m = memberRepository.findById(mid).orElse(null);
                        if (m != null) {
                            var roles = Optional.ofNullable(jwt.getRoles(token))
                                    .orElseGet(() -> List.of("ROLE_USER"))
                                    .stream().map(SimpleGrantedAuthority::new).toList();
                            var auth = new UsernamePasswordAuthenticationToken(m, null, roles);
                            SecurityContextHolder.getContext().setAuthentication(auth);
                        }
                    }
                }
            }

        }
        filterChain.doFilter(request, response);

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String u = request.getRequestURI();
        return u.startsWith("/oauth2/") || u.startsWith("/login/") ||
                u.startsWith("/token")   || u.startsWith("/api/auth/") ||
                u.startsWith("/actuator/") || u.startsWith("/swagger") || u.startsWith("/v3");
    }
}
