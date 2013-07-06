package com.github.nmorel.gw2.batch.jobs.items;

import com.mongodb.DBObject;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ItemsContext
{
    @Bean
    ItemReader<Integer> itemReader()
    {
        return new ItemsReader();
    }

    @Bean
    ItemProcessor<Integer, DBObject> itemProcess()
    {
        return new ItemsProcessor();
    }

    @Bean
    ItemWriter<DBObject> itemWriter()
    {
        return new ItemsWriter();
    }

    @Bean
    public Job itemsJob( JobBuilderFactory jobs, Step s1 )
    {
        return jobs.get("itemsJob")
                .incrementer(new RunIdIncrementer())
                .flow(s1)
                .end()
                .build();
    }

    @Bean
    public Step step1( StepBuilderFactory stepBuilderFactory, ItemReader<Integer> reader,
                       ItemWriter<DBObject> writer, ItemProcessor<Integer, DBObject> processor )
    {
        return stepBuilderFactory.get("step1")
                .<Integer, DBObject>chunk(1)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

}
