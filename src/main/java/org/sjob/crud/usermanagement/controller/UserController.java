package org.sjob.crud.usermanagement.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.sjob.crud.usermanagement.dto.reponse.DtoResponse;
import org.sjob.crud.usermanagement.dto.reponse.DtoUserResponse;
import org.sjob.crud.usermanagement.dto.request.DtoUserRequest;
import org.sjob.crud.usermanagement.entity.User;
import org.sjob.crud.usermanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User", description = "Api Management")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(operationId = "search", summary = "Find User by Name or Email", description = "",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "name", description = "User Name", allowEmptyValue = true),
                    @Parameter(in = ParameterIn.PATH, name = "email", description = "User Email", allowEmptyValue = true)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful Operation", content = @Content(schema = @Schema(implementation = List.class))),
                    @ApiResponse(responseCode = "404", description = "User not found")
            })
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> findUserByEmail(@RequestParam(required = false) String name,
                                                  @RequestParam(required = false) String email) {
        List<DtoUserResponse> result = new ArrayList<>();

        // Get By Email
        Optional.ofNullable(email)
                .map(userService::findByEmail)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .ifPresent(user -> result.add(DtoUserResponse.fromEntity(user)));
        // Get By Name
        Optional.ofNullable(name)
                .map(userService::findByName)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(users -> !users.isEmpty())
                .ifPresent(users -> users.forEach(user -> result.add(DtoUserResponse.fromEntity(user))));

        if (result.isEmpty()) {
            return new ResponseEntity<>(DtoResponse.builder()
                    .message("User not found")
                    .build(), HttpStatusCode.valueOf(HttpStatus.NOT_FOUND.value()));
        }
        return ResponseEntity.ok(result);
    }

    @Operation(operationId = "findUserByUuId", summary = "Find User by ID",
            parameters = {@Parameter(in = ParameterIn.PATH, name = "id", description = "User Id")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = DtoUserResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid User ID supplied"),
                    @ApiResponse(responseCode = "404", description = "User not found")})
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> findUserByUuId(@PathVariable String id) {

        Optional<DtoUserResponse> dtoUser = userService.findById(id).map(DtoUserResponse::fromEntity);
        if (dtoUser.isPresent()) {
            return ResponseEntity.ok(dtoUser.get());
        }
        return new ResponseEntity<>(DtoResponse.builder()
                .message("User not found")
                .build(), HttpStatusCode.valueOf(HttpStatus.NOT_FOUND.value()));
    }

    @Operation(operationId = "findAll", summary = "Find All Users",
            parameters = {@Parameter(in = ParameterIn.PATH, name = "id", description = "User Id")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = List.class)))})
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> allUsers() {

        Optional<List<User>> users = userService.findAll();

        if (users.isPresent()) {
            List<DtoUserResponse> dtoUserResponses = new ArrayList<>();
            users.get().forEach(user -> dtoUserResponses.add(DtoUserResponse.fromEntity(user)));
            return ResponseEntity.ok(dtoUserResponses);
        }
        return new ResponseEntity<>(DtoResponse.builder()
                .message("Users not found")
                .build(), HttpStatusCode.valueOf(HttpStatus.NOT_FOUND.value()));
    }

    @Operation(operationId = "deleteUserById", summary = "Delete User by ID",
            parameters = {@Parameter(in = ParameterIn.PATH, name = "id", description = "User Id")},
            responses = {
                    @ApiResponse(responseCode = "202", description = "Accepted"),
                    @ApiResponse(responseCode = "404", description = "User not found")})
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteUserByUuId(@PathVariable String id) {
        try {
            if (userService.deleteById(id)) {
                return ResponseEntity.noContent().build();
            }
            return new ResponseEntity<>(DtoResponse.builder()
                    .message("User not found")
                    .build(), HttpStatusCode.valueOf(HttpStatus.NOT_FOUND.value()));
        } catch (Exception ex) {
            return new ResponseEntity<>(DtoResponse.builder()
                    .message(ex.getMessage())
                    .build(), HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()));
        }
    }

    @Operation(operationId = "deleteUserById", summary = "Delete User by ID",
            parameters = {@Parameter(in = ParameterIn.PATH, name = "id", description = "User Id")},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created"),
                    @ApiResponse(responseCode = "400", description = "Bad request")})
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createUser(@Valid @RequestBody DtoUserRequest dtoUser, BindingResult errors) {

        if (errors.hasErrors()) {
            FieldError fieldError = errors.getFieldErrors().get(0);
            return new ResponseEntity<>(DtoResponse.builder()
                    .message(String.format("field: %s, error: %s", fieldError.getField(), fieldError.getDefaultMessage()))
                    .build(), HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()));
        }

        try {
            User newUser = userService.create(dtoUser.toEntity());
            DtoUserResponse newDtoUser = DtoUserResponse.fromEntity(newUser);
            return new ResponseEntity<>(newDtoUser, HttpStatusCode.valueOf(HttpStatus.CREATED.value()));
        } catch (Exception ex) {
            return new ResponseEntity<>(DtoResponse.builder()
                    .message(ex.getMessage())
                    .build(), HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()));
        }
    }

    @Operation(operationId = "deleteUserById", summary = "Delete User by ID",
            parameters = {@Parameter(in = ParameterIn.PATH, name = "id", description = "User Id")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ok"),
                    @ApiResponse(responseCode = "400", description = "Bad request")})
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateUser(@PathVariable String id, @Valid @RequestBody DtoUserRequest dtoUser, BindingResult errors) {

        if (errors.hasErrors()) {
            FieldError fieldError = errors.getFieldErrors().get(0);
            return new ResponseEntity<>(DtoResponse.builder()
                    .message(String.format("field: %s, error: %s", fieldError.getField(), fieldError.getDefaultMessage()))
                    .build(), HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()));
        }

        try {
            User newUser = userService.modify(id, dtoUser.toEntity());
            DtoUserResponse newDtoUser = DtoUserResponse.fromEntity(newUser);
            return new ResponseEntity<>(newDtoUser, HttpStatusCode.valueOf(HttpStatus.OK.value()));
        } catch (Exception ex) {
            return new ResponseEntity<>(DtoResponse.builder()
                    .message(ex.getMessage())
                    .build(), HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()));
        }
    }
}
