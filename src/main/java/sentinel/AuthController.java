package sentinel;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(new ApiResponse(true, "Login successful", response));
        } catch (Exception e) {
            return ResponseEntity.status(401)
                .body(new ApiResponse(false, "Invalid username or password", null));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse> getCurrentUser() {
        return ResponseEntity.ok(new ApiResponse(true, "Authenticated", null));
    }
}