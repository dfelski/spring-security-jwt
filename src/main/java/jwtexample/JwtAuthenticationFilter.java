package jwtexample;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

   private static final String AUTHENTICATION_URL = "/api/login";

    private final AuthenticationManager authenticationManager;
    private final byte[] secret;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, String secret) {
        this.authenticationManager = authenticationManager;
        setFilterProcessesUrl(AUTHENTICATION_URL);
        this.secret = secret.getBytes();
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getParameter("username"),
                        request.getParameter("password")));
    }


    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain filterChain, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        List<String> roles = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Algorithm algorithm = Algorithm.HMAC256(secret);
        String token = JWT.create()
                .withSubject(user.getUsername())
                .withIssuer(TokenConstants.TOKEN_ISSUER)
                .withArrayClaim("rol", roles.toArray(new String[roles.size()]))
                .withExpiresAt(new Date(System.currentTimeMillis() + TokenConstants.EXPIRATION_TIME))
                .sign(algorithm);

        response.addHeader(TokenConstants.TOKEN_HEADER, TokenConstants.TOKEN_PREFIX + token);
    }

}
