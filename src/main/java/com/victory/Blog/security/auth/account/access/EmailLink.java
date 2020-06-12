package com.victory.Blog.security.auth.account.access;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;

@Entity
@Table(name = "email_link")
public class EmailLink {
    @Column(name = "hash")
    private String hash;

    @Id
    @Column(name = "user_id")
    private int userId;

    @Column(name = "expire_time")
    private Timestamp expireTime;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Timestamp getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Timestamp expireTime) {
        this.expireTime = expireTime;
    }

    public boolean isValid(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(Calendar.getInstance().getTime().getTime()));
        Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());
        System.out.println("now: " + timestamp + " expire: " + getExpireTime());

        if (timestamp.before(getExpireTime())){
            return true;
        } else {
            return false;
        }
    }
}
