package com.edsonuso.collabapi.common.controller;

import com.edsonuso.collabapi.common.command.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Tag(name = "Utilidade", description = "Endpoints de diagnóstico e utilidade")
public class HealthController {

    @GetMapping("/health")
    @Operation(summary = "Verificar saúde da API", description = "Retorna o status atual da aplicação")
    public ResponseEntity<ApiResponse<Map<String,String>>> health() {
        return ResponseEntity.ok(ApiResponse.ok(Map.of("status", "UP")));
    }

}
