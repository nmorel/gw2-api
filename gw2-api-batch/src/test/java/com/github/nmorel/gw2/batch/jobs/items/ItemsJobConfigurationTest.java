/*
 * Copyright 2006-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.nmorel.gw2.batch.jobs.items;

import com.github.nmorel.gw2.batch.TestContext;
import com.github.nmorel.gw2.batch.config.ApplicationDatabase;
import com.github.nmorel.gw2.batch.config.BatchDatabase;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * <p>
 * Test cases asserting on the example job's configuration.
 * </p>
 */
@ContextConfiguration(classes = {TestContext.class, ItemsContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class ItemsJobConfigurationTest
{
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @Autowired
    private JobOperator jobOperator;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    @BatchDatabase
    private DB batchDb;

    @Autowired
    @ApplicationDatabase
    private DB appliDb;

    @Before
    public void setup()
    {
        cleanDb(batchDb);
        appliDb.dropDatabase();
    }

    private void cleanDb( DB db )
    {
        Set<String> names = db.getCollectionNames();
        names.remove("system.indexes");
        for( String name : names )
        {
            DBCollection collection = db.getCollection(name);
            List<DBObject> indexes = collection.getIndexInfo();
            collection.drop();
            collection = db.getCollection(name);
            for( DBObject index : indexes )
            {
                collection.ensureIndex((DBObject) index.get("key"));
            }
        }
    }

    /**
     * <p>
     * Creates a new {@link JobExecution} using a {@link JobLauncher}.
     * </p>
     *
     * @throws Exception if any {@link Exception}'s occur
     */
    @Test
    public void testLaunchJobWithJobLauncher() throws Exception
    {
        final JobExecution jobExecution = jobLauncher.run(job, new JobParameters());
        assertEquals("Batch status not COMPLETED", BatchStatus.COMPLETED, jobExecution.getStatus());
    }
}
