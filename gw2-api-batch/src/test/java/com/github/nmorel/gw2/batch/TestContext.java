package com.github.nmorel.gw2.batch;

import com.github.nmorel.gw2.batch.config.CommonBatchContext;
import com.github.nmorel.gw2.batch.config.ConfigContext;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import java.security.GeneralSecurityException;

@Configuration
@EnableBatchProcessing
@Import( {ConfigContext.class, CommonBatchContext.class} )
@Profile( "test" )
public class TestContext
{
    @Bean
    HttpTransport httpTransport() throws GeneralSecurityException
    {
        return new NetHttpTransport.Builder().doNotValidateCertificate().build();
    }

}
