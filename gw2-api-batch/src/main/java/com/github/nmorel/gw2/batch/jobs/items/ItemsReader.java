package com.github.nmorel.gw2.batch.jobs.items;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.*;

import javax.inject.Inject;
import java.io.InputStreamReader;
import java.util.Iterator;

/**
 *
 */
public class ItemsReader implements ItemReader<Integer>, ItemStream
{
    private static final Logger logger = LoggerFactory.getLogger(ItemsReader.class);

    @Inject
    private HttpTransport transport;

    private Iterator<String> iterator;

    @Override
    public void open( ExecutionContext executionContext ) throws ItemStreamException
    {
        try
        {
            logger.debug("Retrieving the ids of all items");
            HttpResponse response = transport.createRequestFactory()
                    .buildGetRequest(new GenericUrl("https://api.guildwars2.com/v1/items.json")).execute();
            Items items = new Gson().fromJson(new JsonReader(new InputStreamReader(response.getContent())), Items.class);
            iterator = items.getItems().iterator();
            logger.info("{} items to process", items.getItems().size());
        }
        catch( Exception e )
        {
            throw new ItemStreamException(e);
        }
    }

    @Override
    public Integer read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException
    {
        if( iterator.hasNext() )
        {
            return Integer.parseInt(iterator.next());
        }
        else
        {
            return null;
        }
    }

    @Override
    public void update( ExecutionContext executionContext ) throws ItemStreamException
    {
    }

    @Override
    public void close() throws ItemStreamException
    {
        iterator = null;
    }
}
