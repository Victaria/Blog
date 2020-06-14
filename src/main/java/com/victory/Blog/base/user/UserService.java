package com.victory.Blog.base.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    @Autowired
    UserRepository userRepository;

    public User getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User getById(int id) {
        return userRepository.findById(id).get();
    }
}
