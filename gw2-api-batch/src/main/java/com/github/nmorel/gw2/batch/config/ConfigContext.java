package com.github.nmorel.gw2.batch.config;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.net.UnknownHostException;

@Configuration
@PropertySource( "classpath:batch.properties" )
public class ConfigContext
{

    @Autowired
    Environment env;

    @Bean
    static PropertyPlaceholderConfigurer configurer()
    {
        return new PropertyPlaceholderConfigurer();
    }

    @Bean
    @ApplicationDatabase
    DB applicationDatabase() throws UnknownHostException
    {
        MongoClient client = new MongoClient(env.getProperty("appli.mongodb.host"), env.getProperty("appli.mongodb.port", int.class));
        return client.getDB(env.getProperty("appli.mongodb.name"));
    }

    @Bean
    @BatchDatabase
    DB batchDatabase() throws UnknownHostException
    {
        MongoClient client = new MongoClient(env.getProperty("batch.mongodb.host"), env.getProperty("batch.mongodb.port", int.class));
        return client.getDB(env.getProperty("batch.mongodb.name"));
    }

}
