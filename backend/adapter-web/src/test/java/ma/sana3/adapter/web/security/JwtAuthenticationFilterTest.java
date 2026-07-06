package ma.sana3.adapter.web.security;

import jakarta.servlet.FilterChain;
import ma.sana3.domain.user.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class JwtAuthenticationFilterTest {

    private static final String SECRET = "test-only-secret-key-must-be-at-least-32-bytes-long";

    private final JjwtTokenService tokenService = new JjwtTokenService(SECRET, 15, 7);
    private final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(tokenService);

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void setsAuthenticationForValidBearerToken() throws Exception {
        UUID userId = UUID.randomUUID();
        String accessToken = tokenService.generateAccessToken(userId, "user@example.com", Role.ARTISAN).token();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + accessToken);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertTrue(authentication.getPrincipal().equals(userId));
        assertTrue(authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ARTISAN")));
        verify(chain).doFilter(request, response);
    }

    @Test
    void leavesRequestUnauthenticatedWhenHeaderMissing() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(request, response);
    }

    @Test
    void leavesRequestUnauthenticatedWhenTokenInvalid() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer garbage-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(request, response);
    }
}
