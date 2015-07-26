package com.mauriciotogneri.betfair.models;

import java.util.List;

public class Config
{
    public ConfigLogin login;
    public List<ConfigMonitor> monitors;

    public class ConfigLogin
    {
        public String username;
        public String password;
        public String appKey;
    }

    public class ConfigMonitor
    {
        public boolean enabled;
        public String eventType;
        public boolean inPlay;
        public String[] marketTypes;
    }
}