package com.github.nmorel.gw2.batch.jobs.items;

import com.mongodb.DBObject;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Configuration
public class ItemsContext
{
    @Bean
    ItemReader<String> itemReader()
    {
        return new ItemsReader();
    }

    @Bean
    @StepScope
    ItemProcessor<String, DBObject> itemProcess()
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
    public Step step1( StepBuilderFactory stepBuilderFactory, ItemReader<String> reader,
                       ItemWriter<DBObject> writer, ItemProcessor<String, DBObject> processor )
    {
        SimpleStepBuilder<String, DBObject> builder =
                stepBuilderFactory.get("step1")
                        .<String, DBObject>chunk(1)
                        .reader(reader)
                        .processor(processor)
                        .writer(writer);
        builder.throttleLimit(10);
        builder.taskExecutor(new SimpleAsyncTaskExecutor("items"));
        return builder.build();
    }

}
