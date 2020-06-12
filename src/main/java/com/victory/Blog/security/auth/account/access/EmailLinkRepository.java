package com.victory.Blog.security.auth.account.access;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailLinkRepository extends JpaRepository<EmailLink, Integer> {

   // @Query("SELECT t FROM email_link t WHERE t.hash = ?1 ")
    EmailLink findByHash(String hash);
}
