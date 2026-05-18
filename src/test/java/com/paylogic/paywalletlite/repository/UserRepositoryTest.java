package com.paylogic.paywalletlite.repository;

import com.paylogic.paywalletlite.config.root.RootConfig;
import com.paylogic.paywalletlite.domain.identity.User;
import com.paylogic.paywalletlite.domain.identity.enums.AccountStatus;
import com.paylogic.paywalletlite.domain.identity.enums.RoleType;
import com.paylogic.paywalletlite.repository.identity.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RootConfig.class)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveAndFindUser() {
        User user = new User();
        user.setRole(RoleType.CUSTOMER);
        user.setFirstName("Salma");
        user.setLastName("EL-GHEFYRY");
        user.setPhoneNumber("+212600000001");
        user.setPasswordHash("hashed_password");
        user.setPinHash("hashed_pin");
        user.setStatus(AccountStatus.ACTIVE);

        User saved = userRepository.save(user);
        assertNotNull(saved.getUserId());

        Optional<User> found = userRepository.findById(saved.getUserId());
        assertTrue(found.isPresent());
        assertEquals("Salma", found.get().getFirstName());
    }
}