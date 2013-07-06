package com.github.nmorel.gw2.batch.jobs.items;

import com.github.nmorel.gw2.batch.config.ApplicationDatabase;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStreamReader;

/**

 */
public class ItemsProcessor implements ItemProcessor<Integer, DBObject>
{
    private static final Logger logger = LoggerFactory.getLogger(ItemsProcessor.class);

    private static final String[] LANGS = new String[]{"en", "fr", "de", "es"};

    @Inject
    private HttpTransport transport;

    @Inject
    @ApplicationDatabase
    private DB db;

    @Override
    public DBObject process( final Integer itemId ) throws Exception
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

        for( String lang : LANGS )
        {
            findItemDetailsForLang(itemId, lang, obj);
        }


        logger.info("Item {} processed : {}", itemId, obj);
        return obj;
    }

    private void findItemDetailsForLang( int itemId, String lang, DBObject obj ) throws IOException
    {
        HttpResponse response = transport.createRequestFactory()
                .buildGetRequest(new GenericUrl("https://api.guildwars2.com/v1/item_details.json").set("item_id", itemId).set("lang", lang))
                .execute();

        Item item = new Gson().fromJson(new JsonReader(new InputStreamReader(response.getContent())), Item.class);

        DBObject res = new BasicDBObjectBuilder().start("name", item.getName())
                .add("description", item.getDescription()).add("type", item.getType()).add("level", item.getLevel())
                .add("rarity", item.getRarity()).get();

        obj.put(lang, res);
    }
}
