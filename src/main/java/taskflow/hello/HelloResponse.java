package taskflow.hello;

import java.time.Instant;

public record HelloResponse(
        String message,
        Instant timestamp
) {

}