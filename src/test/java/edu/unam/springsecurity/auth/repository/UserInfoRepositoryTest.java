package edu.unam.springsecurity.auth.repository;

import static org.assertj.core.api.Assertions.assertThat;

import edu.unam.springsecurity.auth.model.UserInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
class UserInfoRepositoryTest {
    private final TestEntityManager testEntityManager;
    private final UserInfoRepository userInfoRepository;

    @Autowired
    public UserInfoRepositoryTest(TestEntityManager testEntityManager, UserInfoRepository userInfoRepository) {
        this.testEntityManager = testEntityManager;
        this.userInfoRepository = userInfoRepository;
    }

    @Test
    public void testCreateUser() {
        UserInfo user = new UserInfo();
        user.setUseFirstName("Alfonso");
        user.setUseLastName("Rivero");
        user.setUseEmail("devil861109@gmail.com");
        user.setUsePasswd("asdf1234");
        user.setUseIdStatus(1);
        user.setUseCreatedBy(1L);
        user.setUseModifiedBy(1L);
        UserInfo savedUser = userInfoRepository.save(user);
        UserInfo existUser = testEntityManager.find(UserInfo.class, savedUser.getUseId());
        assertThat(user.getUseEmail()).isEqualTo(existUser.getUseEmail());
    }

    @Test
    public void testReadUser() {
        UserInfo user = new UserInfo();
        user.setUseEmail("devil861109@gmail.com");
        Optional<UserInfo> userInfo = userInfoRepository.findById(1L);
        if (userInfo.isPresent()) {
            UserInfo savedUser = userInfo.get();
            UserInfo existUser = testEntityManager.find(UserInfo.class, savedUser.getUseId());
            assertThat(user.getUseEmail()).isEqualTo(existUser.getUseEmail());
        } else
            assertThat(user.getUseEmail()).isEqualTo(null);

    }
}
