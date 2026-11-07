package taskflow.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import taskflow.infrastructure.security.JwtService;

@Service
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthenticationService(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) throws BadCredentialsException {
        Authentication authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(request.email(), request.password());
        Authentication authenticationResponse = authenticationManager.authenticate(authenticationRequest);
        UserDetails details = (UserDetails) authenticationResponse.getPrincipal();
        String jwt = jwtService.generateToken(details);
        return new LoginResponse(jwt);
    }
}
