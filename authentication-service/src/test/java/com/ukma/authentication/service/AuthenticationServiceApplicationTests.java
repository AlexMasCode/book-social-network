package com.ukma.authentication.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ukma.authentication.service.auth.dto.AuthDto;
import com.ukma.authentication.service.user.User;
import com.ukma.authentication.service.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthenticationServiceApplicationTests {

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserRepository userRepository;

    @MockBean
    PasswordEncoder passwordEncoder;

    @MockBean
    UserDetailsService userDetailsService;

    MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .apply(springSecurity())
            .build();
    }

    @Test
    public void registrationWithCorrectDataIsSuccessful() throws Exception {
        AuthDto authDto = new AuthDto("Vova", "vova123");

        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(new User(authDto.getEmail(), authDto.getPassword()));

        mockMvc.perform(
                post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(authDto))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andExpect(jsonPath("$.token").isString());

        verify(userRepository, times(1)).findByUsername(any(String.class));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testThatRegistrationIsFailedIfUserWithSuchUsernameAlreadyExists() throws Exception {
        AuthDto authDto = new AuthDto("Vova", "vova123");

        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(new User(authDto.getEmail(), authDto.getPassword())));

        mockMvc.perform(
                post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(authDto))
            )
            .andExpect(status().isBadRequest());

        verify(userRepository, times(1)).findByUsername(any(String.class));
    }

    @Test
    public void testThatLoginIsSuccessfulIfUserRegistered() throws Exception {
        AuthDto authDto = new AuthDto("Vova", "vova123");

        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(new User(authDto.getEmail(), authDto.getPassword()));

        mockMvc.perform(
                post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(authDto))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andExpect(jsonPath("$.token").isString());


        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(new User(authDto.getEmail(), authDto.getPassword())));
        when(passwordEncoder.matches(any(String.class), any(String.class))).thenReturn(true);

        mockMvc.perform(
                post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(authDto))
            )
            .andExpect(status().isOk())
            .andReturn();

        verify(userRepository, times(2)).findByUsername(any(String.class));
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).matches(any(String.class), any(String.class));
    }

    @Test
    public void testThatLoginIsFailedIfUserIsNotRegistered() throws Exception {
        AuthDto authDto = new AuthDto("Vova", "vova123");

        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.empty());
        when(passwordEncoder.matches(any(String.class), any(String.class))).thenReturn(true);

        mockMvc.perform(
                post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(authDto))
            )
            .andExpect(status().isBadRequest());

        verify(userRepository, times(1)).findByUsername(any(String.class));
    }

    @Test
    public void testThatRegisteredUserCanAccessAuthenticatedResources() throws Exception {
        AuthDto authDto = new AuthDto("Vova", "vova123");

        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.empty());

        MvcResult mvcResult = mockMvc.perform(
                post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(authDto))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andExpect(jsonPath("$.token").isString())
            .andReturn();

        String jwt = objectMapper.readTree(mvcResult.getResponse().getContentAsString()).get("token").asText();

        List<User> expectedUserMockList = List.of(new User(), new User());
        when(userRepository.findAll()).thenReturn(expectedUserMockList);
        when(userDetailsService.loadUserByUsername(any(String.class))).thenReturn(new User(authDto.getEmail(), authDto.getPassword()));

        //accessing authenticated resource
        MvcResult mvcGetResult = mockMvc.perform(
                get("/api/users")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
            )
            .andExpect(status().isOk())
            .andReturn();

        List<User> actualUserMockList = objectMapper.readValue(mvcGetResult.getResponse().getContentAsString(), new TypeReference<>() {});

        assertThat(actualUserMockList).isNotEmpty();
        assertThat(actualUserMockList).hasSize(expectedUserMockList.size());

        verify(userRepository, times(1)).findByUsername(any(String.class));
    }

    @Test
    public void testThatUserCannnotAccessAuthenticatedResourcesWithoutJwt() throws Exception {
        AuthDto authDto = new AuthDto("Vova", "vova123");

        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.empty());

        MvcResult mvcResult = mockMvc.perform(
                post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(authDto))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andExpect(jsonPath("$.token").isString())
            .andReturn();


        List<User> expectedUserMockList = List.of(new User(), new User());
        when(userRepository.findAll()).thenReturn(expectedUserMockList);
        
        //accessing authenticated resource
        mockMvc.perform(
            get("/api/users")
        ).andExpect(status().is4xxClientError());

        verify(userRepository, times(1)).findByUsername(any(String.class));
    }

    @Test
    public void testThatUserCannnotAccessAuthenticatedResourcesWithIncorrectJwt() throws Exception {
        AuthDto authDto = new AuthDto("Vova", "vova123");

        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.empty());

        MvcResult mvcResult = mockMvc.perform(
                post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(authDto))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andExpect(jsonPath("$.token").isString())
            .andReturn();

        String jwt = objectMapper.readTree(mvcResult.getResponse().getContentAsString()).get("token").asText();

        List<User> expectedUserMockList = List.of(new User(), new User());
        when(userRepository.findAll()).thenReturn(expectedUserMockList);

        assertThatThrownBy(() -> {
            //accessing authenticated resource
            mockMvc.perform(
                    get("/api/users")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt + "s")

                )
                .andExpect(status().isForbidden());
        });

        verify(userRepository, times(1)).findByUsername(any(String.class));
    }

}
