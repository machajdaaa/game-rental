package cz.gamerental.service;

import cz.gamerental.model.Role;
import cz.gamerental.model.User;
import cz.gamerental.repository.RoleRepository;
import cz.gamerental.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void loadUserByUsername_existujiciUzivatel_vrati() {
        User user = new User();
        user.setUsername("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        User result = (User) userService.loadUserByUsername("testuser");

        assertEquals("testuser", result.getUsername());
    }

    @Test
    void loadByUserName_neexistujiciUzivatel_vyhodiVyjimku() {
        when(userRepository.findByUsername("nikdo")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("nikdo"));
    }

    @Test
    void registerUser_uspesna_registrace() {
        Role role = new Role();
        role.setName("ROLE_USER");

        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("heslo")).thenReturn("zahesovaneheslo");
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        User user = userService.registerUser("novak", "novak@email.cz", "heslo", "Jan Novák");

        assertEquals("novak", user.getUsername());
        assertEquals("zahesovaneheslo", user.getPassword());
        assertTrue(user.getRoles().contains(role));
        verify(userRepository, times(1)).save(any());
    }
}
