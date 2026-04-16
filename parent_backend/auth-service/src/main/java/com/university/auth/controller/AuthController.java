package com.university.auth.controller;

import com.university.auth.dto.LoginRequest;
import com.university.auth.dto.RegisterRequest;
import com.university.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

//    @PostMapping("/register")
//    public String register(@RequestBody RegisterRequest request) {
//        return authService.register(request);
//    }
    // 🔹 Register Parent
    @PostMapping("/register/parent")
    public String registerParent(@RequestBody RegisterRequest request) {
        return authService.register(request, "PARENT");
    }

    // 🔹 Register Mentor
    @PostMapping("/register/mentor")
    public String registerMentor(@RequestBody RegisterRequest request) {
        return authService.register(request, "MENTOR");
    }

    // 🔹 Register Admin (optional – protect later)
    @PostMapping("/register/admin")
    public String registerAdmin(@RequestBody RegisterRequest request) {
        return authService.register(request, "ADMIN");
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/secure")
    public String secureEndpoint() {
        return "You accessed a secured endpoint!";
    }

    @GetMapping("/admin")
    public String adminOnly() {
        return "Welcome Admin!";
    }

    @GetMapping("/mentor")
    public String mentorOnly() {
        return "Welcome Mentor!";
    }



}
