package com.cpwr.gdo.simulator.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cpwr.gdo.simulator.dao.repository.security.AuthorityRepository;
import com.cpwr.gdo.simulator.dao.repository.security.UserRepository;
import com.cpwr.gdo.simulator.model.security.Authority;
import com.cpwr.gdo.simulator.model.security.AuthorityEnum;
import com.cpwr.gdo.simulator.model.security.User;

/**
 * The application security provider.
 */
@Service("authService")
public class AuthDetailsService implements UserDetailsService {
    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(AuthDetailsService.class);

    /** The ws service repository. */
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private StandardPasswordEncoder encoder;

    @PostConstruct
    private void checkInjection() {
        checkNotNull(userRepository, "UserRepository must not be null!");
        checkNotNull(authorityRepository, "AuthorityRepository must not be null!");
        checkNotNull(encoder, "Encoder must not be null!");
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        checkArgument(StringUtils.isNotBlank(username), "username must not be empty: %s", username);

        final User user = userRepository.findUserByUsername(StringUtils.trimToEmpty(username));

        if (user == null) {
            final String errMsg = "AuthDetailsService: username=" + username + " not found";
            LOG.error(errMsg);
            throw new UsernameNotFoundException(errMsg);
        }

        return user;
    }

    @Transactional
    public User createUser(final String fullName, final String email, final String username, final String password) {
        // checkArgument(fullName!=null, "userId must not be null: %s", userId);
        // checkArgument(email!=null, "authority must not be null: %s",
        // authority);
        checkArgument(StringUtils.isNotBlank(username), "username must not be empty: %s", username);
        checkArgument(StringUtils.isNotBlank(password), "password must not be empty: %s", password);

        User user = new User();

        user.setEmail(email);
        user.setFullName(fullName);
        user.setUsername(username);
        user.setPassword(encoder.encode(password));

        final Authority defaultAuth = authorityRepository.findByAuthority(AuthorityEnum.WS_USER.toString());

        user.addAuthority(defaultAuth);

        userRepository.saveAndFlush(user);

        LOG.debug("New user has been created: {}", username);

        return user;
    }

    @Transactional
    public void excludeUserFromRole(final Long userId, final AuthorityEnum authority) {
        checkArgument(userId != null, "userId must not be null: %s", userId);
        checkArgument(authority != null, "authority must not be null: %s", authority);

        User user = userRepository.findOne(userId);

        Iterator<GrantedAuthority> iterator = ((Set<GrantedAuthority>) user.getAuthorities()).iterator();

        while (iterator.hasNext()) {
            GrantedAuthority auth = iterator.next();
            if (StringUtils.equalsIgnoreCase(authority.toString(), auth.getAuthority())) {
                iterator.remove();
            }
        }

        userRepository.saveAndFlush(user);
    }
}
