package jwtexample;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

    @GetMapping("/api/private")
    public String privateHello() {
        return "Hi, I'm private\n";
    }

    @GetMapping("/api/public")
    public String publicHello(){
        return "Hi, I'm public\n";
    }
}
