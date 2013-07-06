package com.github.nmorel.gw2.batch.config;

import com.github.nmorel.gw2.batch.jobs.items.ItemsContext;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.ApplicationContextFactory;
import org.springframework.batch.core.configuration.support.GenericApplicationContextFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableBatchProcessing( modular = true )
@Import( {ConfigContext.class, CommonBatchContext.class} )
public class DefaultBatchContext
{
    @Bean
    public ApplicationContextFactory personModule()
    {
        return new GenericApplicationContextFactory(ItemsContext.class);
    }
}
