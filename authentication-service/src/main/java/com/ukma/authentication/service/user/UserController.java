package com.ukma.authentication.service.user;

import com.ukma.authentication.service.user.dto.UserDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserController {

    UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {
        try {
            return ResponseEntity.ok(userService.findAll().stream()
                .map(user -> new UserDto(user.getId(), user.getUsername()))
                .toList());
        } catch (Exception exception) {
            return ResponseEntity.badRequest().build();
        }
    }
}
