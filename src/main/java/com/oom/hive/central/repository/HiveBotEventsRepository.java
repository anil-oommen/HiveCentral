package com.oom.hive.central.repository;

import com.oom.hive.central.repository.model.HiveBot;
import com.oom.hive.central.repository.model.HiveBotEvent;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

public interface HiveBotEventsRepository extends MongoRepository<HiveBotEvent, String> {

    @Query(value="{$and: [ " +
            "  {botId: ?0 }, " +
            "  {key: {$in:?1} },  " +
            "  {time:{ $gt: ?2  }}" +
            "] }")
    Stream<HiveBotEvent> findInTimeSeries(
            String botId
            , String[] eventKeys
            , Date eventDate
            ,  Sort sort
    );

}
