package com.edsonuso.collabapi.user.command;

import io.swagger.v3.oas.annotations.media.Schema;

public class UserCommands {

    private UserCommands() {}

    @Schema(description = "Resposta de verificação de disponibilidade de username")
    public record UsernameAvailabilityResponse(
            @Schema(description = "Indica se o username está disponível")
            boolean available
    ) {}
}
