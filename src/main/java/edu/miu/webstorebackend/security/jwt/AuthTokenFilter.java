package edu.miu.webstorebackend.security.jwt;

import edu.miu.webstorebackend.security.services.spring.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    private JwtTokenUtil jwtTokenUtil;

    private UserDetailsServiceImpl userDetailsService;

    //private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Autowired
    public AuthTokenFilter(JwtTokenUtil jwtTokenUtil, UserDetailsServiceImpl userDetailsServiceImpl) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsServiceImpl;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = parseJwt(request);
            if(token != null & jwtTokenUtil.validateToken(token)) {
                String username = jwtTokenUtil.getUserNameFromJwtToken(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if(headerAuth != null) {
            if(headerAuth.startsWith("Bearer ") && !headerAuth.isEmpty()) {
                return headerAuth.substring(7, headerAuth.length());
            }
        }
        return null;
    }
}
