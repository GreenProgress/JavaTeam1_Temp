package JavaProject.Backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    // Render가 확인하는 주소 (/api/health)
    @GetMapping("/api/health")
    public String healthCheck() {
        return "Server is up and running!";
    }
}