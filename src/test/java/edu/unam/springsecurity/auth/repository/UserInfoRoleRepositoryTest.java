package edu.unam.springsecurity.auth.repository;

import static org.assertj.core.api.Assertions.assertThat;

import edu.unam.springsecurity.auth.model.UserInfoRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
class UserInfoRoleRepositoryTest {
    private final TestEntityManager testEntityManager;
    private final UserInfoRoleRepository userInfoRoleRepository;

    @Autowired
    public UserInfoRoleRepositoryTest(TestEntityManager testEntityManager, UserInfoRoleRepository userInfoRoleRepository) {
        this.testEntityManager = testEntityManager;
        this.userInfoRoleRepository = userInfoRoleRepository;
    }

    @Test
    public void testCreateUser() {
        UserInfoRole role = new UserInfoRole();
        role.setUsrRoleName("USER");
        role.setUsrIdStatus(1);
        role.setUsrCreatedBy(1L);
        role.setUsrModifiedBy(1L);
        UserInfoRole savedRole = userInfoRoleRepository.save(role);
        UserInfoRole existRole = testEntityManager.find(UserInfoRole.class, savedRole.getUsrId());
        assertThat(role.getUsrId()).isEqualTo(existRole.getUsrId());
    }

    @Test
    public void testCreateAdmin() {
        UserInfoRole role = new UserInfoRole();
        role.setUsrRoleName("ADMIN");
        role.setUsrIdStatus(1);
        role.setUsrCreatedBy(1L);
        role.setUsrModifiedBy(1L);
        UserInfoRole savedRole = userInfoRoleRepository.save(role);
        UserInfoRole existRole = testEntityManager.find(UserInfoRole.class, savedRole.getUsrId());
        assertThat(role.getUsrId()).isEqualTo(existRole.getUsrId());
    }
}