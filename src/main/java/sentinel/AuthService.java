package sentinel;

import org.springframework.security.authentication.*;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthService(AuthenticationManager authenticationManager,
            UserRepository userRepository, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(), request.getPassword()));
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());
        return new LoginResponse(token, refreshToken,
            user.getUsername(), user.getFullName(),
            user.getRole().name(), "Login successful");
    }
}