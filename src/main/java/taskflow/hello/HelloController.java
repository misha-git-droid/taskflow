package taskflow.hello;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
public class HelloController {
    @GetMapping("/api/v1/hello")
    public HelloResponse hello(@RequestParam(value = "name", defaultValue = "World") String value) {
        return new HelloResponse("Hello, " + value, Instant.now());
    }
}