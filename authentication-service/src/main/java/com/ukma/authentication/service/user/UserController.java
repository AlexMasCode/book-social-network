package com.ukma.authentication.service.user;

import com.ukma.authentication.service.user.dto.UserDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserController {

    UserService userService;
    JmsTemplate jmsTemplate;

    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {
        try {
            return ResponseEntity.ok(userService.findAll());
        } catch (Exception exception) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id) {
        userService.delete(id);

        jmsTemplate.convertAndSend("user.deletion", id);
    }
}
