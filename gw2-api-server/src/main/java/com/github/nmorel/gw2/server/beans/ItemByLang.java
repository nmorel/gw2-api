package com.github.nmorel.gw2.server.beans;

/** @author Nicolas Morel */
public class ItemByLang
{
    private String name;

    private String description;

    private String type;

    private String level;

    private String rarity;

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public String getLevel()
    {
        return level;
    }

    public void setLevel( String level )
    {
        this.level = level;
    }

    public String getRarity()
    {
        return rarity;
    }

    public void setRarity( String rarity )
    {
        this.rarity = rarity;
    }
}
