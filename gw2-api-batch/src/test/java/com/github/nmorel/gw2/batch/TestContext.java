package com.github.nmorel.gw2.batch;

import com.github.nmorel.gw2.batch.config.CommonBatchContext;
import com.github.nmorel.gw2.batch.config.ConfigContext;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableBatchProcessing
@Import({ConfigContext.class, CommonBatchContext.class})
public class TestContext
{

    @Bean
    JobLauncherTestUtils jobLauncherTestUtils()
    {
        return new JobLauncherTestUtils();
    }
}
