package org.gosvea.service;

public interface RedisService {


    void saveData(String key, String value);

     Object getData(String key);

     void deleteData(String key);

}
