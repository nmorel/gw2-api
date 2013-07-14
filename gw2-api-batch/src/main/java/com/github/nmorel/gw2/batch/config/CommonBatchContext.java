package com.github.nmorel.gw2.batch.config;

import com.github.nmorel.spring.batch.mongodb.configuration.annotation.MongoDbBatchConfigurer;
import com.github.nmorel.spring.batch.mongodb.explore.support.MongoDbJobExplorerFactoryBean;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.mongodb.DB;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.GeneralSecurityException;

@Configuration
public class CommonBatchContext
{
    @Bean
    public MongoDbJobExplorerFactoryBean jobExplorerFactoryBean( @BatchDatabase DB db )
    {
        MongoDbJobExplorerFactoryBean factory = new MongoDbJobExplorerFactoryBean();
        factory.setDb(db);
        return factory;
    }

    @Bean
    public BatchConfigurer batchConfigurer( final @BatchDatabase DB db )
    {
        return new MongoDbBatchConfigurer(db);
    }

    @Bean
    JobOperator jobOperator( final JobLauncher jobLauncher, final JobExplorer jobExplorer,
                             final JobRepository jobRepository, final JobRegistry jobRegistry )
    {
        return new SimpleJobOperator()
        {{
                setJobLauncher(jobLauncher);
                setJobExplorer(jobExplorer);
                setJobRepository(jobRepository);
                setJobRegistry(jobRegistry);
            }};
    }

    @Bean
    JobRegistryBeanPostProcessor jobRegisterBeanPostProcess( final JobRegistry jobRegistry )
    {
        return new JobRegistryBeanPostProcessor()
        {{
                setJobRegistry(jobRegistry);
            }};
    }

    @Bean
    HttpTransport httpTransport() throws GeneralSecurityException
    {
        return new NetHttpTransport.Builder().build();
    }

    @Bean
    HttpRequestFactory httpRequestFactory() throws GeneralSecurityException
    {
        return httpTransport().createRequestFactory();
    }

}
