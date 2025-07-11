package com.simple.domain.configuration

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaRepositories(basePackages = ["com.simple.domain.repository"])
@EntityScan(basePackages = ["com.simple.domain.entity"])
class DbConfiguration
