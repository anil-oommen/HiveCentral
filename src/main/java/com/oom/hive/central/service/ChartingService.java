package com.oom.hive.central.service;

import com.oom.hive.central.model.ChartJSData;

public interface ChartingService {

    ChartJSData getEventsForTimeSeries(
            String hiveBotId,
            String[] eventKeys,
            int flashBackMinutes, int intervalMinutes);
}
