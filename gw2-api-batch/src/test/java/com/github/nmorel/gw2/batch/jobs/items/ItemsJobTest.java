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
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Files;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * <p>
 * Test cases asserting on the example job's configuration.
 * </p>
 */
@ContextConfiguration( classes = {TestContext.class, ItemsContext.class} )
@RunWith( SpringJUnit4ClassRunner.class )
@ActiveProfiles("test")
public class ItemsJobTest
{
    @ClassRule
    public static WireMockClassRule wireMockRule = new WireMockClassRule(wireMockConfig().httpsPort(8091));

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @Autowired
    @BatchDatabase
    private DB batchDb;

    @Autowired
    @ApplicationDatabase
    private DB appliDb;

    @Before
    public void setup() throws IOException
    {
        cleanDb(batchDb);
        appliDb.dropDatabase();

        reset();
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
        List<String> itemsId = Arrays.asList("12546", "26706", "38875");
        List<String> langs = Arrays.asList("en", "fr");

        // init mock responses
        givenThat(get(urlEqualTo("/items.json")).willReturn(aResponse().withBody("{\"items\":[" + Joiner.on(',').join(itemsId) + "]}")));
        for( String itemId : itemsId )
        {
            for( String lang : langs )
            {
                givenThat(get(urlEqualTo("/item_details.json?item_id=" + itemId + "&lang=" + lang))
                        .willReturn(aResponse().withBody(getContentFile("/items/mock/item_" + itemId + "_" + lang + ".json"))));
            }
        }

        // execute batch
        final JobExecution jobExecution = jobLauncher
                .run(job, new JobParametersBuilder().addString("langs", Joiner.on(',').join(langs)).toJobParameters());
        assertEquals("Batch status not COMPLETED", BatchStatus.COMPLETED, jobExecution.getStatus());

        // verifying the result
        DBCollection itemsCollection = appliDb.getCollection("items");
        assertEquals(itemsId.size(), itemsCollection.count());

        for( String itemId : itemsId )
        {
            DBObject actual = itemsCollection.findOne(itemId);
            assertNotNull(actual);

            DBObject expected = (DBObject) JSON.parse(getContentFile("/items/expected/item_" + itemId + "_" + Joiner.on("").join(langs) + ".json"));
            assertEquals(expected, actual);
        }
    }

    private String getContentFile( String path ) throws IOException
    {
        return Files.toString(getFile(path), Charsets.UTF_8);
    }

    private File getFile( String path ) throws IOException
    {
        return new ClassPathResource(path).getFile();
    }
}
