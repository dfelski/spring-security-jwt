package jwtexample;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthorizationFilter.class);

    private final JWTVerifier jwtVerifier;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, @Value("${jwt.secret}") String secret) {
        super(authenticationManager);
        jwtVerifier = JWT.require(Algorithm.HMAC256(secret.getBytes()))
            .withIssuer(TokenConstants.TOKEN_ISSUER)
            .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {
        String token = request.getHeader(TokenConstants.TOKEN_HEADER);
        if (StringUtils.isEmpty(token) || !token.startsWith(TokenConstants.TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(request));
        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(TokenConstants.TOKEN_HEADER);
        if (!StringUtils.isEmpty(token)) {
            LOGGER.debug("decoding token '{}'", token);

            try {
                DecodedJWT jwt = jwtVerifier.verify(token.replace(TokenConstants.TOKEN_PREFIX, ""));

                String username = jwt.getSubject();
                String[] roles = jwt.getClaim("rol").asArray(String.class);

                LOGGER.debug("decoded user '{}' with {} authorities", username, roles.length);

                List<SimpleGrantedAuthority> authorities = Arrays.stream(roles)
                        .map(authority -> new SimpleGrantedAuthority(authority))
                        .collect(Collectors.toList());

                if (!StringUtils.isEmpty(username)) {
                    return new UsernamePasswordAuthenticationToken(username, null, authorities);
                }
            } catch(Exception e) {
                LOGGER.debug(e.getLocalizedMessage());
            }
        }
        return null;
    }

}
