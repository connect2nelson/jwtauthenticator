package com.fun.abm.JWTAuthenticator.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;


public class JwtPayload {
    public static final Logger LOGGER = LoggerFactory.getLogger(JwtPayload.class);

    String userId;
    User user;
    Date exp;

    public JwtPayload() {
    }

    public JwtPayload(User user, Date exp) {
        this.user = user;
        this.exp = exp;
    }

    public JwtPayload(User user, Date exp, String idpUserId) {
        this.userId = idpUserId;
        this.user = user;
        this.exp = exp;
    }

    @JsonIgnore
    public boolean isValid() {
        if (Objects.isNull(exp)) {
            LOGGER.info("No expiry in jwt payload [{}]", this);
            return false;
        }
        if (StringUtils.isEmpty(userId)) {
            LOGGER.info("No user id in jwt payload [{}]", this);
            return false;
        }
        Date utcNow = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
        if (exp.before(utcNow)) {
            LOGGER.info("Token is expired [{}]", this);
            return false;
        }
        return true;
    }
}
