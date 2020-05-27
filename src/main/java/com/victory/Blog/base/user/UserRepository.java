package com.victory.Blog.base.user;

import com.victory.Blog.base.user.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {
    User findByEmail(String email);
}
