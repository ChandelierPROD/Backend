package com.example.prodolymp.controllers;

import com.example.prodolymp.models.ReasonModel;
import com.example.prodolymp.models.ThemesModel;
import com.example.prodolymp.models.UserModel;
import com.example.prodolymp.models.enums.Role;
import com.example.prodolymp.repositories.ThemesRepositories;
import com.example.prodolymp.service.ThemesService;
import com.example.prodolymp.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/themes")
@RequiredArgsConstructor
public class ThemesController {
    private final ThemesService themesService;
    private final TokenService tokenService;

    @Operation(summary = "Получить все темы")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Посты получены", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ThemesModel.class))),
            @ApiResponse(responseCode = "401", description = "Неверный токен", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReasonModel.class)))    })
    @GetMapping("/")
    public ResponseEntity<Object> getAllThemes(
            @Parameter(description = "Bearer токен авторизации", required = true, example = "Bearer <ваш_токен>", schema = @Schema(type = "string"))
            @RequestHeader("Authorization") String token){
        ReasonModel reason = new ReasonModel();
        if(token != null && token.startsWith("Bearer ")){
            String jwtToken = token.substring(7);
            if(tokenService.validateToken(jwtToken)){
                Optional<UserModel> user = tokenService.getUserByToken(jwtToken);
                if(user.isPresent()){
                    List<ThemesModel> themesList = themesService.getAllThemes();
                    return ResponseEntity.status(HttpStatus.OK).body(themesList);
                }else {
                    reason.setReason("Error when receiving the user profile");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reason);
                }
            }
        }
        reason.setReason("Invalid token");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reason);
    }

    @Operation(summary = "Создать новый курс")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Посты получены", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ThemesModel.class))),
            @ApiResponse(responseCode = "400", description = "Неверный входные данные", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReasonModel.class))),
            @ApiResponse(responseCode = "401", description = "Неверный токен", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReasonModel.class))),
            @ApiResponse(responseCode = "403", description = "Аккаунт пользователя не является админом", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReasonModel.class))) })
    @PostMapping("/new")
    private ResponseEntity<Object> createNewTheme(
            @Parameter(description = "Bearer токен авторизации", required = true, example = "Bearer <ваш_токен>", schema = @Schema(type = "string"))
            @RequestHeader("Authorization") String token,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для создания новой темы",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    name = "Пример запроса",
                                    value = "{\n" +
                                            "  \"title\": \"Название темы\",\n" +
                                            "  \"category\": \"Категория темы\",\n" +
                                            "  \"description\": \"Описание темы\",\n" +
                                            "  \"author\": \"Автор темы\",\n" +
                                            "  \"points\": \"Количество поинтов\"\n" +
                                            "}"
                            )
                    )
            )
            @RequestBody Map<String, Object> request) {
        ReasonModel reason = new ReasonModel();
        if(token != null && token.startsWith("Bearer ")){
            String jwtToken = token.substring(7);
            if(tokenService.validateToken(jwtToken)){
                if(request != null){
                    Optional<UserModel> user = tokenService.getUserByToken(jwtToken);
                    if(user.isPresent()){
                        System.out.println(user.get().getRoles());
                        if(user.get().getRoles().contains(Role.ROLE_ADMIN)){
                            String title = (String) request.get("title");
                            String category = (String) request.get("category");
                            String description = (String) request.get("description");
                            String author = (String) request.get("author");
                            Integer points = (Integer) request.get("points");

                            ThemesModel theme = themesService.createTheme(title, category, description, author, points);

                            if(theme == null){
                                reason.setReason("Bad title, category, description, author");
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(reason);
                            }

                            return ResponseEntity.status(HttpStatus.OK).body(theme);
                        }else{
                            reason.setReason("The user is not an administrator");
                            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(reason);
                        }
                    }else {
                        reason.setReason("Error when receiving the user profile");
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reason);
                    }
                }else {
                    reason.setReason("Invalid date body");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(reason);
                }
            }
        }
        reason.setReason("Invalid token");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reason);
    }
}