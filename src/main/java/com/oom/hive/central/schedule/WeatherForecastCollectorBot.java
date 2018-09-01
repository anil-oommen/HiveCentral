package com.oom.hive.central.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oom.hive.central.connector.MQTTOutboundGateway;
import com.oom.hive.central.model.HiveBotData;
import com.oom.hive.central.model.govsg.Forecast;
import com.oom.hive.central.model.govsg.Item;
import com.oom.hive.central.model.govsg.WeatherForecast;
import com.oom.hive.central.model.types.HiveBotDataType;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.net.URL;

@Configuration
@EnableScheduling
@Profile({"ModuleWeatherForecastCollect"})
public class WeatherForecastCollectorBot {

    @Autowired
    MQTTOutboundGateway outboundGateway;

    @Value("${ext.weather.bot.api.url}")
    private String api_url;

    @Value("${ext.weather.bot.api.area:NotSet}" )
    private String api_area;

    @Value("${ext.weather.bot.id}")
    private String botid;

    @Value("${ext.weather.bot.accesskey}")
    private String botAccesskey;


    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(WeatherForecastCollectorBot.class);

    @Scheduled(fixedDelay = 1000 * 60 * 5, initialDelay = 5000)
    public void gatherWeatherData() {


       /* URI targetUrl =
                UriComponentsBuilder
                        .fromUriString("https://api.data.gov.sg/v1/environment/2-hour-weather-forecast")
                .build().toUri();*/




        try {

            logger.info("WF2H Weather Data Collect [{}] {}" , api_area, api_url);
            ObjectMapper mapper = new ObjectMapper();
            WeatherForecast wf2h = mapper.readValue(new URL(
                    api_url),
                    WeatherForecast.class);

            String item__location_forcast = wf2h.getItems()
                    .stream()
                    .findAny()
                    .map(Item::getForecasts)
                    .get()
                    .stream()
                    .filter(fc -> fc.getArea().equals(api_area))
                    .findAny()
                    .map(Forecast::getForecast)
                    .orElse(null);
            logger.info("\t\tLocation Forecast(2h) \"{}\" ", item__location_forcast);

            if(item__location_forcast!=null){
                HiveBotData hiveBotData = new HiveBotData();
                hiveBotData.setHiveBotId(botid);
                hiveBotData.getDataMap().put("GovSG_WeatherArea",api_area);
                hiveBotData.getDataMap().put("GovSG_Weather2hForecast",item__location_forcast);
                hiveBotData.setStatus("AVAILABLE");
                hiveBotData.setAccessKey(botAccesskey);
                hiveBotData.setDataType(HiveBotDataType.SENSOR_DATA);
                outboundGateway.sendToMqtt(hiveBotData);
            }
/*
            if(ia !=null ){
                String fcLocation = ia.getForecasts()
                        .stream()
                        .filter(fc -> fc.getArea().equals("Jurong WestXXX"))
                        .findAny()
                        .map(Forecast::getArea)
                        .orElse(null)
                        ;
                logger.info("WF2H Location Data () {} ", fcLocation);
            }
*/


           /* DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;
            builder = builderFactory.newDocumentBuilder();
            org.w3c.dom.Document xmlDocument = null;
            xmlDocument = builder.parse(targetUrl.toString());



            XPath xPath = XPathFactory.newInstance().newXPath();
            String expression = "/channel/item/weatherForecast/area[@name='"+location+"']";
            NodeList nodeList = (NodeList) xPath.compile(expression)
                    .evaluate(xmlDocument, XPathConstants.NODESET);
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) nodeList.item(i);
                    nea_weather_code=el.getAttribute("forecast");
                    nea_weather_value = code_map.get(nea_weather_code);
                    logger.info("NEA Local Weather (" +nea_weather_code+") ("+nea_weather_value+")");
                }

            }

            HiveBotData hiveBotData = new HiveBotData();
            hiveBotData.setHiveBotId(botid);
            hiveBotData.setStatus("NO_DATA_AVAILABLE"); //Default not data
            if(nea_weather_code!=null){
                hiveBotData.getDataMap().put("NEA_WeatherCode",nea_weather_code);
                hiveBotData.setStatus("AVAILABLE");
            }

            if(nea_weather_value!=null){
                hiveBotData.getDataMap().put("NEA_Weather",nea_weather_value);
                hiveBotData.setStatus("AVAILABLE");
            }
            hiveBotData.setAccessKey(botAccesskey);
            hiveBotData.setDataType(HiveBotDataType.SENSOR_DATA);
            outboundGateway.sendToMqtt(hiveBotData);*/


            /*if (HiveBotDataType.SENSOR_DATA.equals(botData.getDataType())) {
                EnumSet<AppSettings.HiveSaveOperation> saveOperations =
                        EnumSet.of(AppSettings.HiveSaveOperation.SAVE_INFO,
                                AppSettings.HiveSaveOperation.ADD_DATAMAP,
                                AppSettings.HiveSaveOperation.EVENTLOG_DATAMAP,
                                AppSettings.HiveSaveOperation.BOT_IS_ALIVE
                        );
                reportingService.saveBot(botData, saveOperations);

            }

            botReportingService.saveBot(hiveBotData, EnumSet.of(
                    AppSettings.HiveSaveOperation.SAVE_INFO,
                    AppSettings.HiveSaveOperation.ADD_DATAMAP,
                    AppSettings.HiveSaveOperation.EVENTLOG_DATAMAP,
                    AppSettings.HiveSaveOperation.BOT_IS_ALIVE
                    )
            );*/

        } catch ( IOException e) {
            logger.error("NEA Data Collect Error",e);
        }

    }
}
