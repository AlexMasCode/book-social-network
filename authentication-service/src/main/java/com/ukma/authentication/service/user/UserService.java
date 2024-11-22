package com.ukma.authentication.service.user;

import com.ukma.authentication.service.user.dto.UserDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserService{

    UserRepository userRepository;

    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
            .map(user -> new UserDto(user.getId(), user.getFullName(), user.getUsername()))
            .toList();
    }

    public User findByUsername(String username) {
        return userRepository.findByEmail(username).orElseThrow();
    }

    public void delete(String userId) {
        userRepository.deleteById(userId);
    }
}
