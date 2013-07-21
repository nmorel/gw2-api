package com.github.nmorel.gw2.server.beans;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/** @author Nicolas Morel */
@Document(collection = "items")
public class Item
{
    @Id
    private String id;

    @Field("en")
    private ItemByLang english;

    @Field( "fr" )
    private ItemByLang french;

    @Field( "de" )
    private ItemByLang deutsch;

    @Field( "es" )
    private ItemByLang spanish;

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public ItemByLang getEnglish()
    {
        return english;
    }

    public void setEnglish( ItemByLang english )
    {
        this.english = english;
    }

    public ItemByLang getFrench()
    {
        return french;
    }

    public void setFrench( ItemByLang french )
    {
        this.french = french;
    }

    public ItemByLang getDeutsch()
    {
        return deutsch;
    }

    public void setDeutsch( ItemByLang deutsch )
    {
        this.deutsch = deutsch;
    }

    public ItemByLang getSpanish()
    {
        return spanish;
    }

    public void setSpanish( ItemByLang spanish )
    {
        this.spanish = spanish;
    }
}
