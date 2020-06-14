package com.victory.Blog.security.service;

import com.victory.Blog.base.user.User;
import com.victory.Blog.base.user.UserRepository;
import com.victory.Blog.security.jwt.SignUpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.Calendar;

@RedisHash("User")
@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder bcryptEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User currentUser = userRepository.findByEmail(email);
        if (currentUser == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(currentUser.getEmail())
                .password(currentUser.getPassword())
                .roles("USER")
                .build();
    }

    public User save(SignUpRequest user) {
        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
        newUser.setFirstname(user.getFirstname());
        newUser.setLastname(user.getLastname());
        newUser.setConfirmed(false);
        newUser.setCreated_at(new Date(Calendar.getInstance().getTime().getTime()));
        return userRepository.save(newUser);
    }

    public void update(User user) {
        user.setPassword(bcryptEncoder.encode(user.getPassword()));
        user.setConfirmed(true);
        userRepository.updateUser(user.getPassword(), user.getId());
    }

}
