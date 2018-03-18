package com.oom.hive.central.schedule;

import com.oom.hive.central.AppSettings;
import com.oom.hive.central.model.HiveBotData;
import com.oom.hive.central.service.BotReportingService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.net.URI;
import java.util.EnumSet;
import java.util.Map;

@Configuration
@EnableScheduling
@Profile({"ModuleNEACollect","Production"})
public class NEADataCollectionJob {


    // http://api.nea.gov.sg/api/WebAPI/?dataset=2hr_nowcast&keyref=781CF461BB6606AD1260F4D81345157F059A2EFA2D1C36B6
    // https://www.nea.gov.sg/docs/default-source/api/developer's-guide.pdf


    @Autowired
    BotReportingService botReportingService;

    @Value("#{${nea.weather.code.map}}")
    private Map<String,String> code_map;

    @Value("${nea.weather.dataset}")
    private String dataset;

    @Value("${nea.weather.location}")
    private String location;

    @Value("${nea.weather.keyref}")
    private String keyref;

    @Value("${nea.weather.botid}")
    private String botid;



    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(NEADataCollectionJob.class);

    @Scheduled(fixedDelay = 1000 * 60 * 5, initialDelay = 5000)
    public void gatherAndSourceNEAData() {


        String nea_weather_code = null;
        String nea_weather_value = null;

        URI targetUrl = UriComponentsBuilder.fromUriString("http://api.nea.gov.sg/api/WebAPI/")
                .queryParam("dataset", dataset)
                .queryParam("keyref", keyref)
                .build().toUri();


        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
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
            botReportingService.saveBot(hiveBotData, EnumSet.of(
                    AppSettings.HiveSaveOperation.SAVE_INFO,
                    AppSettings.HiveSaveOperation.ADD_DATAMAP,
                    AppSettings.HiveSaveOperation.EVENTLOG_DATAMAP,
                    AppSettings.HiveSaveOperation.BOT_IS_ALIVE
                    )
            );

        } catch (SAXException e) {
            logger.error("NEA Data Collect Error",e);
        } catch (IOException e) {
            logger.error("NEA Data Collect Error",e);
        } catch (ParserConfigurationException e) {
            logger.error("NEA Data Collect Error",e);
        } catch (XPathExpressionException e) {
            logger.error("NEA Data Collect Error",e);
        }

    }

}
