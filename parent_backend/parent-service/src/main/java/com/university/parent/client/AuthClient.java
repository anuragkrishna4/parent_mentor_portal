package com.university.parent.client;

import com.university.parent.config.FeignConfig; // Ensure this is imported
import com.university.parent.dto.RegisterRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(
        name = "auth-service",
        url = "http://localhost:8080",
        configuration = FeignConfig.class
)
public interface AuthClient {


    @PostMapping("/auth/register/parent")
    String registerParent(@RequestBody RegisterRequest request);
}