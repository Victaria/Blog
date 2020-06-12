package com.victory.Blog.base.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update User u set u.password = ?1 where u.id = ?2") //set confirmed = true
    void updateUser(String password, int id);
}
