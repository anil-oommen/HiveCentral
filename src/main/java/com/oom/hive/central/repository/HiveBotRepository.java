package com.oom.hive.central.repository;


import com.oom.hive.central.repository.model.HiveBot;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HiveBotRepository extends MongoRepository<HiveBot, String> {

    HiveBot findByBotId(String botId);
}
