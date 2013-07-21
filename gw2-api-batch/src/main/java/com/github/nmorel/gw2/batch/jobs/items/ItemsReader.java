package com.github.nmorel.gw2.batch.jobs.items;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;
import java.io.InputStreamReader;
import java.util.Iterator;

/**
 *
 */
public class ItemsReader implements ItemReader<String>, ItemStream
{
    private static final Logger logger = LoggerFactory.getLogger(ItemsReader.class);

    @Inject
    private HttpRequestFactory requestFactory;

    @Inject
    private ObjectMapper objectMapper;

    @Value( "${gw2.api.host}" )
    private String apiHost;

    private Iterator<String> iterator;

    @Override
    public void open( ExecutionContext executionContext ) throws ItemStreamException
    {
        try
        {
            logger.debug("Retrieving the ids of all items");
            HttpResponse response = requestFactory
                    .buildGetRequest(new GenericUrl(apiHost + "items.json")).execute();
            Items items = objectMapper.readValue(new InputStreamReader(response.getContent()), Items.class);
            iterator = items.getItems().iterator();
            logger.info("{} items to process", items.getItems().size());
        }
        catch( Exception e )
        {
            throw new ItemStreamException(e);
        }
    }

    @Override
    public synchronized String read() throws Exception
    {
        if( iterator.hasNext() )
        {
            return iterator.next();
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
