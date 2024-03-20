package edu.unam.springsecurity.security.model;

import edu.unam.springsecurity.auth.model.UserInfo;
import edu.unam.springsecurity.auth.model.UserInfoRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {
    private Long id;
    private String name;
    private String email;
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;
    private UserInfo userInfo;

    public UserDetailsImpl(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public UserDetailsImpl(Long id, String name, String email, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(UserInfo user) {
        List<GrantedAuthority> authorities = user.getUseInfoRoles().stream().map(role ->
                new SimpleGrantedAuthority(role.getUsrRoleName())
        ).collect(Collectors.toList());
        return new UserDetailsImpl(
                user.getUseId(),
                user.getFullName(),
                user.getUseEmail(),
                authorities
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (null == userInfo.getUseInfoRoles()) {
            return Collections.emptySet();
        }
        Set<SimpleGrantedAuthority> grantedAuthorities = new HashSet<>();
        for (UserInfoRole role : userInfo.getUseInfoRoles()) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getUsrRoleName()));
        }
        return grantedAuthorities;
    }

    /**
     * getUsername
     * @return username
     */
    @Override
    public String getUsername() {
        return userInfo.getUseEmail();
    }

    /**
     * getPassword (OTP)
     * @return password
     */
    @Override
    public String getPassword() {
        return userInfo.getUsePasswd();
    }

    /**
     * getName
     * @return name
     */
    public String getName() {
        return userInfo.getUseFirstName();
    }

    /**
     * getEmail
     * @return email
     */
    public String getEmail() {
        return userInfo.getUseEmail();
    }

    /**
     * isEnabled
     * @return if user is enabled
     */
    @Override
    public boolean isEnabled() {
        return userInfo.getUseIdStatus() == 1;
    }

    /**
     * isAccountNonLocked
     * @return if user is locked
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * isAccountNonExpired
     * @return if account is not expired
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * isCredentialsNonExpired
     * @return if credential is not expired
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

}
