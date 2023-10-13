package com.reswizard.Controllers;

import com.reswizard.Models.User;
import com.reswizard.Services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.ui.Model;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class MainPagesControllerTest {
    private MainPagesController mainPagesController;

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mainPagesController = new MainPagesController(userService);
    }

    @Test
    public void testSayHello() {
        String viewName = mainPagesController.sayHello();
        assertEquals("MainPage", viewName);
    }

    @Test
    public void testHelloUser() throws Exception {
        User user = new User();
        user.setUsername("Daniil Ivanchyk");
        user.setPassword("Romromrom");
        user.setRole("ROLE_USER");


        mockMvc.perform(MockMvcRequestBuilders.get("/helloUser")
                        .with(SecurityMockMvcRequestPostProcessors.user((UserDetails) user)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("MainUserPage"));
    }
}