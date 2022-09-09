package com.github.npospolita.sqlinjectionproof;

import com.github.npospolita.sqlinjectionproof.model.SomeObject;
import com.github.npospolita.sqlinjectionproof.repo.SomeObjectRepository;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.transaction.Transactional;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = {SqlInjectionProofIT.Initializer.class})
@AutoConfigureMockMvc
public class SqlInjectionProofIT {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected SomeObjectRepository someObjectRepository;

    @ClassRule
    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:11.1")
            .withDatabaseName("integration-tests-db")
            .withUsername("sa")
            .withPassword("sa");

    static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @Before
    public void init() {
        someObjectRepository.save(SomeObject.builder()
                        .id(1L)
                        .name("abc")
                .build());
        someObjectRepository.save(SomeObject.builder()
                        .id(2L)
                        .name("zabcde")
                .build());
        someObjectRepository.save(SomeObject.builder()
                        .id(3L)
                        .name("fgr")
                .build());
    }

    @Test
    @Transactional
    public void test() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/get")
                .queryParam("name", "abc")
                .queryParam("page", "0")
                .queryParam("size", "10"))
                .andDo(print())
                .andExpect(jsonPath("$.page.totalElements", is(2)));


        System.out.println();
    }
}
