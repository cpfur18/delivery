package com.delivery.config;

import com.delivery.global.config.QueryDslConfig;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/** JPA 테스트 커스텀 어노테이션 */
@DataJpaTest
@EnableJpaAuditing
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({TestAuditorConfig.class, QueryDslConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public @interface CustomDataJpaTest {}
