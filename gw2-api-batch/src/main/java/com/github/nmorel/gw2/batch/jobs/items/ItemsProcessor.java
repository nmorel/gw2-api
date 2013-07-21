package com.github.nmorel.gw2.batch.jobs.items;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nmorel.gw2.batch.config.ApplicationDatabase;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStreamReader;

/**

 */
public class ItemsProcessor implements ItemProcessor<String, DBObject>
{
    private static final Logger logger = LoggerFactory.getLogger(ItemsProcessor.class);

    @Inject
    private HttpRequestFactory requestFactory;

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    @ApplicationDatabase
    private DB db;

    @Value( "${gw2.api.host}" )
    private String apiHost;

    @Value( "#{jobParameters['langs'].split(\",\")}" )
    private String[] langs;

    @Override
    public DBObject process( final String itemId ) throws Exception
    {
        if( null == itemId )
        {
            return null;
        }

        logger.debug("Processing item {}", itemId);

        DBObject obj = db.getCollection("items").findOne(itemId);
        if( null == obj )
        {
            obj = new BasicDBObject("_id", itemId);
        }

        for( String lang : langs )
        {
            findItemDetailsForLang(itemId, lang, obj);
        }


        logger.info("Item {} processed : {}", itemId, obj);
        return obj;
    }

    private void findItemDetailsForLang( String itemId, String lang, DBObject obj ) throws IOException
    {
        HttpResponse response = requestFactory
                .buildGetRequest(new GenericUrl(apiHost + "item_details.json").set("item_id", itemId).set("lang", lang))
                .execute();

        Item item = objectMapper.readValue(new InputStreamReader(response.getContent()), Item.class);

        DBObject res = BasicDBObjectBuilder.start("name", item.getName())
                .add("description", item.getDescription()).add("type", item.getType()).add("level", item.getLevel())
                .add("rarity", item.getRarity()).get();

        obj.put(lang, res);
    }
}
