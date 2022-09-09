package com.github.npospolita.sqlinjectionproof;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class SqlInjectionProofApplication {

    public static void main(String[] args) {
        SpringApplication.run(SqlInjectionProofApplication.class, args);
    }

}
