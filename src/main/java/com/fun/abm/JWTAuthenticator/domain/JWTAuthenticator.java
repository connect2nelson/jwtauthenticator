package com.fun.abm.JWTAuthenticator.domain;


import com.fun.abm.JWTAuthenticator.annotation.Authenticated;
import com.fun.abm.JWTAuthenticator.model.JwtPayload;
import com.fun.abm.JWTAuthenticator.util.JsonUtils;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class JWTAuthenticator implements HandlerInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(HandlerInterceptor.class);

    @Value("${authorization.jwt.publicKey:''}")
    private String secret;

    private SignatureVerifier getSignatureVerifier() {
        return new RsaVerifier(secret);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HandlerMethod hm = (HandlerMethod) handler;
        Method method = hm.getMethod();
        if (!method.isAnnotationPresent(Authenticated.class)) {
            LOGGER.debug("Skiping authentication for un authenticated endpoint");
            return true;
        }

        String authorizationToken = getAuthorizationToken(request);
        if (Strings.isNullOrEmpty(authorizationToken)) {
            LOGGER.error("Rejecting request to authenticated endpoint without Authorization header.");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        Jwt jwt;
        try {
            jwt = JwtHelper.decodeAndVerify(authorizationToken, getSignatureVerifier());
        } catch (Exception e) {
            LOGGER.error("Rejecting request with invalid authorization token :", e);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        JwtPayload jwtPayload;
        try {
            jwtPayload = JsonUtils.fromJson(jwt.getClaims(), JwtPayload.class);
        } catch (Exception e) {
            LOGGER.error("Error while converting Json to JwtPayload : JSON[{}]", jwt.getClaims());
            LOGGER.error("Rejecting request as token data has malformed JSON");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        if (!jwtPayload.isValid()) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            LOGGER.error("Rejecting request as token is invalid [{}]", jwtPayload);
            return false;
        }

        request.setAttribute("JwtPayload", jwtPayload);
        LOGGER.info("Successfully authenticated request");
        return true;
    }

    private String getAuthorizationToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (Strings.isNullOrEmpty(authorizationHeader)) {
            LOGGER.error("Authorization header is empty");
            return null;
        }
        String[] authHeaderValues = authorizationHeader.split(" ");
        if (authHeaderValues.length != 2) {
            LOGGER.error("Authorization header is malformed");
            return null;
        }
        String authType = authHeaderValues[0];
        if (!"Bearer".equals(authType)) {
            LOGGER.error("Authorization type is not Bearer token");
            return null;
        }

        String authToken = authHeaderValues[1];

        return authToken;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
