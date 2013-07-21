package com.github.nmorel.gw2.server.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/** @author Nicolas Morel */
@Configuration
@EnableMongoRepositories( basePackages = "com.github.nmorel.gw2.server.repositories" )
@EnableWebMvc
@ComponentScan( basePackages = "com.github.nmorel.gw2.server.controllers" )
public class ServerContext extends AbstractMongoConfiguration
{
    @Override
    protected String getDatabaseName()
    {
        return "appli";
    }

    @Override
    public Mongo mongo() throws Exception
    {
        return new MongoClient("localhost", 27017);
    }
}
