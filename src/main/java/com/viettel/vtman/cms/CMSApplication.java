package com.viettel.vtman.cms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
@EnableScheduling
public class CMSApplication {
    private static final Logger LOGGER = LogManager.getLogger(CMSApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(CMSApplication.class, args);

        LOGGER.debug("Debug Message Logged !!!");
        LOGGER.info("Info Message Logged !!!");
//        LOGGER.error("Error Message Logged !!!", new NullPointerException("NullError"));
    }

}
