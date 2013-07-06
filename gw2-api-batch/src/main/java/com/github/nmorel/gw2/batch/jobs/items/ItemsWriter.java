package com.github.nmorel.gw2.batch.jobs.items;

import com.github.nmorel.gw2.batch.config.ApplicationDatabase;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import javax.inject.Inject;
import java.util.List;

public class ItemsWriter implements ItemWriter<DBObject>
{
    private static final Logger logger = LoggerFactory.getLogger(ItemsWriter.class);

    @Inject
    @ApplicationDatabase
    private DB db;

    @Override
    public void write( List<? extends DBObject> items ) throws Exception
    {
        logger.debug("Writing {} items", items.size());
        DBCollection collection = db.getCollection("items");
        for( DBObject item : items )
        {
            collection.update(new BasicDBObject("_id", item.get("_id")), item, true, false);
        }
        logger.debug("Wrote {} items", items.size());
    }
}
