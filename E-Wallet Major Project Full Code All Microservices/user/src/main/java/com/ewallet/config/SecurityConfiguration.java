package com.ewallet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    /**
     *
     * 1. If the environment is DEV or STG then normal passwrod encoder or else use Bycrypted Password encoder
     *
     */
    @Bean
    public PasswordEncoder passwordEncoder() {

//        if(env == DEV || env == STG) {
//            return NoOpPasswordEncoder.getInstance();
//        }else{
//            return new BCryptPasswordEncoder();
//        }

        return NoOpPasswordEncoder.getInstance();

    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {

        security.csrf().disable();
        return security.build();

    }

}
