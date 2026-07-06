package ma.sana3.adapter.web.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ma.sana3.application.auth.InvalidTokenException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JjwtTokenService tokenService;

    JwtAuthenticationFilter(JjwtTokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            try {
                JjwtTokenService.AccessTokenClaims claims = tokenService.parseAccessToken(header.substring(7));
                var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + claims.role().name()));
                var authentication = new UsernamePasswordAuthenticationToken(claims.userId(), null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (InvalidTokenException ignored) {
                // leave request unauthenticated; downstream authorization will reject it
            }
        }
        filterChain.doFilter(request, response);
    }
}
