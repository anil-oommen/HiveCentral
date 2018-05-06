package com.oom.hive.central.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oom.hive.central.HiveCentralMain;
import com.oom.hive.central.model.HiveBotData;
import com.oom.hive.central.model.Instruction;
import com.oom.hive.central.service.BotReportingService;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpSession;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

@RunWith(SpringRunner.class)
//@SpringBootTest
//@WebMvcTest(value = BotController.class, secure = false)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = HiveCentralMain.class)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-unit.test.properties")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BotControllerTest extends  BaseControllerTest{

    @Autowired
    private MockMvc mockMvc;

    static final String LOGIN_USER_ACCOUNT = "guest";
    static final String LOGIN_USER_ACCOUNT_PASSWORD = "pass123";

    @Test
    public void test0010CheckAccessPublic() throws Exception {
        HiveBotData hiveBotData = new HiveBotData();
        hiveBotData.setHiveBotId(botId);
        hiveBotData.setHiveBotVersion(botVersion);

        String responseBody =  mockMvc.perform(post("/api/session/public/check.access")
                .contentType(APPLICATION_JSON)
                .content(json(hiveBotData))
        ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.statusCode", Matchers.is(0)))
                .andExpect(jsonPath("$.message", Matchers.is("Ack:")))
                .andReturn().getResponse().getContentAsString();
    }


    @Test
    public void test0020CheckAccessSecureWithLogin() throws Exception {
        HiveBotData hiveBotData = new HiveBotData();
        hiveBotData.setHiveBotId(botId);
        hiveBotData.setHiveBotVersion(botVersion);

        String responseBody =  mockMvc.perform(post("/api/session/secure/check.access")
                .contentType(APPLICATION_JSON)
                .content(json(hiveBotData))
        ).andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        //Test With Wrong account

        mockMvc.perform(post("/app/security/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username",LOGIN_USER_ACCOUNT)
                .param("password","wrongpassword")
                .param("noforms","true")
        ).andExpect(status().isUnauthorized());

        //Test with Correct Account.
        MockHttpServletRequest request = mockMvc.perform(post("/app/security/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username",LOGIN_USER_ACCOUNT)
                .param("password",LOGIN_USER_ACCOUNT_PASSWORD)
                .param("noforms","true")
        ).andExpect(status().is2xxSuccessful()).andReturn().getRequest();
        HttpSession session = request.getSession();


        //Check Access is working now
        session = mockMvc.perform(post("/api/session/secure/check.access")
                .contentType(APPLICATION_JSON)
                .content(json(hiveBotData))
                .session((MockHttpSession) session)
        ).andExpect(status().is2xxSuccessful())
                .andReturn().getRequest().getSession();


        //Logout of Connected Session.
        mockMvc.perform(post("/app/security/logout")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("noforms","true")
                .session((MockHttpSession) session)
        ).andExpect(status().is2xxSuccessful());



        // Secure Access should be blocked now
        mockMvc.perform(post("/api/session/secure/check.access")
                .contentType(APPLICATION_JSON)
                .content(json(hiveBotData))
        ).andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();
    }



    @Test
    public void test0030GetAllClientsPublic() throws Exception {
        HiveBotData hiveBotData = new HiveBotData();
        hiveBotData.setHiveBotId(botId);
        hiveBotData.setHiveBotVersion(botVersion);

        String responseBody =  mockMvc.perform(get("/api/hivecentral/public/all.clients")
                .contentType(APPLICATION_JSON)
                .content(json(hiveBotData))
        ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.ackSuccess", Matchers.is("OK")))
                .andExpect(jsonPath("$.message", Matchers.is("List of Clients")))
                .andReturn().getResponse().getContentAsString();

    }

    @Test
    public void test0040GetAllScheduledPublic() throws Exception {
        HiveBotData hiveBotData = new HiveBotData();
        hiveBotData.setHiveBotId(botId);
        hiveBotData.setHiveBotVersion(botVersion);

        String responseBody =  mockMvc.perform(get("/api/hivecentral/public/all.scheduled")
                .contentType(APPLICATION_JSON)
                .content(json(hiveBotData))
        ).andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();

    }

    @Test
    public void test0050GetInfoPublic() throws Exception {
        HiveBotData hiveBotData = new HiveBotData();
        hiveBotData.setHiveBotId(botId);
        hiveBotData.setHiveBotVersion(botVersion);
        hiveBotData.setAccessKey(accessKey);

        String responseBody =  mockMvc.perform(post("/api/hivecentral/public/get.info")
                .contentType(APPLICATION_JSON)
                .content(json(hiveBotData))
        ).andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();

    }



    @Test
    public void test0060RegisterNewBotSecure() throws Exception {
        HiveBotData hiveBotData = new HiveBotData();
        hiveBotData.setHiveBotId(botId);
        hiveBotData.setHiveBotVersion(botVersion);

        MockHttpServletRequest request = mockMvc.perform(post("/app/security/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username",LOGIN_USER_ACCOUNT)
                .param("password",LOGIN_USER_ACCOUNT_PASSWORD)
                .param("noforms","true")
        ).andExpect(status().is2xxSuccessful()).andReturn().getRequest();
        HttpSession session = request.getSession();


        //Login to Controller
        request  =  mockMvc.perform(post("/api/hivecentral/secure/register.new")
                .contentType(APPLICATION_JSON)
                .content(json(hiveBotData))
                .session((MockHttpSession) session)
        ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.message", Matchers.is("Hello. 'MOCKED.BOT.001'. Welcome to HiveCentral. Use AccessKey for further xchange.")))
                .andExpect(jsonPath("$.accessKey", Matchers.is("8036e8b4844ffa957362e21b49aea18508b70dad07b22d4460c6649fd7f4d779")))
                .andReturn().getRequest();


        //Logout of Connected Session.
        mockMvc.perform(post("/app/security/logout")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("noforms","true")
                .session((MockHttpSession) session)
        ).andExpect(status().is2xxSuccessful());


    }

    /*
    @Test
    public void test02GetInfo() throws Exception {
        HiveBotData hiveBotData = new HiveBotData();
        hiveBotData.setHiveBotId(botId);
        hiveBotData.setHiveBotVersion(botVersion);
        hiveBotData.setAccessKey("invalid");


        String responseBody =  mockMvc.perform(post("/hivecentral/iot.bot/xchange/get_info")
                .contentType(APPLICATION_JSON)
                .content(json(hiveBotData))
        ).andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status", Matchers.is("ERR.UNAUTHORISED")))
                .andExpect(jsonPath("$.hiveBotId", Matchers.is("MOCKED.BOT.001")))
                .andReturn().getResponse().getContentAsString();

        hiveBotData.setAccessKey(accessKey);
        responseBody =  mockMvc.perform(post("/hivecentral/iot.bot/xchange/get_info")
                .contentType(APPLICATION_JSON)
                .content(json(hiveBotData))
        ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.message", Matchers.is("Hello MOCKED.BOT.001.Data Retrieve Done")))
                .andExpect(jsonPath("$.status", Matchers.is("ACK")))
                .andReturn().getResponse().getContentAsString();
    }


    @Test
    public void test03SaveInfo() throws Exception {
        HiveBotData hiveBotData = new HiveBotData();
        hiveBotData.setHiveBotId(botId);
        hiveBotData.setHiveBotVersion(botVersion);
        hiveBotData.setAccessKey(accessKey);
        String responseBody;


        hiveBotData.setEnabledFunctions("FUNCTION1");
        responseBody =  mockMvc.perform(post("/hivecentral/iot.bot/xchange/save_basic")
                .contentType(APPLICATION_JSON)
                .content(json(hiveBotData))
        ).andExpect(status().is2xxSuccessful())
        .andExpect(jsonPath("$.message", Matchers.is("Hello MOCKED.BOT.001. Data Info Saved. ")))
        .andExpect(jsonPath("$.status", Matchers.is("ACK")))
        .andExpect(jsonPath("$.enabledFunctions", Matchers.is("FUNCTION1")))
        .andReturn().getResponse().getContentAsString();

        hiveBotData.setEnabledFunctions("FUNCTION1+FUNCTION2");
        responseBody =  mockMvc.perform(post("/hivecentral/iot.bot/xchange/save_basic")
                .contentType(APPLICATION_JSON)
                .content(json(hiveBotData))
        ).andExpect(status().is2xxSuccessful())
        .andExpect(jsonPath("$.message", Matchers.is("Hello MOCKED.BOT.001. Data Info Saved. ")))
        .andExpect(jsonPath("$.status", Matchers.is("ACK")))
        .andExpect(jsonPath("$.enabledFunctions", Matchers.is("FUNCTION1+FUNCTION2")))
        .andReturn().getResponse().getContentAsString();

        //Not Change to Function:
        hiveBotData.setEnabledFunctions(null);
        responseBody =  mockMvc.perform(post("/hivecentral/iot.bot/xchange/save_basic")
                .contentType(APPLICATION_JSON)
                .content(json(hiveBotData))
        ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.message", Matchers.is("Hello MOCKED.BOT.001. Data Info Saved. ")))
                .andExpect(jsonPath("$.status", Matchers.is("ACK")))
                .andExpect(jsonPath("$.enabledFunctions", Matchers.is("FUNCTION1+FUNCTION2")))
                .andReturn().getResponse().getContentAsString();


    }

    @Test
    public void test04SaveInstruction() throws Exception {
        HiveBotData hiveBotData = new HiveBotData();
        hiveBotData.setHiveBotId(botId);
        hiveBotData.setHiveBotVersion(botVersion);
        hiveBotData.setAccessKey(accessKey);
        String responseBody;


        //Test , Clear Instructions
        hiveBotData.getInstructions().clear();
        responseBody =  mockMvc.perform(post("/hivecentral/iot.bot/xchange/save_set_instructions")
                .contentType(APPLICATION_JSON)
                .content(json(hiveBotData))
        ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.message", Matchers.is("Hello MOCKED.BOT.001. Data Info Saved. ")))
                .andExpect(jsonPath("$.status", Matchers.is("ACK")))
                .andExpect(jsonPath("$.instructions", Matchers.hasSize(0)))
                .andReturn().getResponse().getContentAsString();

        //Test, Add 2 Instructions
        hiveBotData.getInstructions().clear();
        hiveBotData.getInstructions().add(new Instruction(10001,"CMD1","now()","args1",false));
        hiveBotData.getInstructions().add(new Instruction(10002,"CMD2","time()","args2",false));
        responseBody =  mockMvc.perform(post("/hivecentral/iot.bot/xchange/save_set_instructions")
                .contentType(APPLICATION_JSON)
                .content(json(hiveBotData))
        ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.message", Matchers.is("Hello MOCKED.BOT.001. Data Info Saved. ")))
                .andExpect(jsonPath("$.status", Matchers.is("ACK")))
                .andExpect(jsonPath("$.instructions", Matchers.hasSize(2)))
                .andReturn().getResponse().getContentAsString();


        //Test, Add 2 More Instructions
        hiveBotData.getInstructions().clear();
        hiveBotData.getInstructions().add(new Instruction(10003,"CMD3","now()","args3",false));
        hiveBotData.getInstructions().add(new Instruction(10004,"CMD4","time()","args4",false));
        responseBody =  mockMvc.perform(post("/hivecentral/iot.bot/xchange/save_add_instructions")
                .contentType(APPLICATION_JSON)
                .content(json(hiveBotData))
        ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.message", Matchers.is("Hello MOCKED.BOT.001. Data Info Saved. ")))
                .andExpect(jsonPath("$.status", Matchers.is("ACK")))
                .andExpect(jsonPath("$.instructions", Matchers.hasSize(4)))
                .andReturn().getResponse().getContentAsString();

        //Test, Set 1 Only Instructions
        hiveBotData.getInstructions().clear();
        hiveBotData.getInstructions().add(new Instruction(10005,"CMD5","now()","args5",false));
        responseBody =  mockMvc.perform(post("/hivecentral/iot.bot/xchange/save_set_instructions")
                .contentType(APPLICATION_JSON)
                .content(json(hiveBotData))
        ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.message", Matchers.is("Hello MOCKED.BOT.001. Data Info Saved. ")))
                .andExpect(jsonPath("$.status", Matchers.is("ACK")))
                .andExpect(jsonPath("$.instructions", Matchers.hasSize(1)))
                .andReturn().getResponse().getContentAsString();

    }

    @Test
    public void test05SaveDataMap() throws Exception {
        HiveBotData hiveBotData = new HiveBotData();
        hiveBotData.setHiveBotId(botId);
        hiveBotData.setHiveBotVersion(botVersion);
        hiveBotData.setAccessKey(accessKey);
        String responseBody;


        //Test , Initilize with 1 Instruction ,
        hiveBotData.getInstructions().clear();
        hiveBotData.getInstructions().add(new Instruction(10006,"CMD6","now()","args6",false));
        hiveBotData.getDataMap().put("DataKey1","Value1");
        responseBody =  mockMvc.perform(post("/hivecentral/iot.bot/xchange/save_set_instructions_set_datamap")
                .contentType(APPLICATION_JSON)
                .content(json(hiveBotData))
        ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.message", Matchers.is("Hello MOCKED.BOT.001. Data Info Saved. ")))
                .andExpect(jsonPath("$.status", Matchers.is("ACK")))
                .andExpect(jsonPath("$.dataMap.DataKey1", Matchers.is("Value1")))
                //.andExpect(jsonPath("$.dataMap.[\"data.map.size\"]", Matchers.is("1")))
                //.andExpect(jsonPath("$.dataMap.[\"instruction.coll.size\"]", Matchers.is("1")))
                .andReturn().getResponse().getContentAsString();

        //Test , Add New DataMap Value
        hiveBotData.getInstructions().clear();
        hiveBotData.getDataMap().clear();
        hiveBotData.getDataMap().put("DataKey2","Value2");
        responseBody =  mockMvc.perform(post("/hivecentral/iot.bot/xchange/save_add_datamap")
                .contentType(APPLICATION_JSON)
                .content(json(hiveBotData))
        ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.message", Matchers.is("Hello MOCKED.BOT.001. Data Info Saved. ")))
                .andExpect(jsonPath("$.status", Matchers.is("ACK")))
                .andExpect(jsonPath("$.dataMap.DataKey1", Matchers.is("Value1")))
                .andExpect(jsonPath("$.dataMap.DataKey2", Matchers.is("Value2")))
                //.andExpect(jsonPath("$.dataMap.[\"data.map.size\"]", Matchers.is("2")))
                //.andExpect(jsonPath("$.dataMap.[\"instruction.coll.size\"]", Matchers.is("1")))
                .andReturn().getResponse().getContentAsString();


        //Test , Emptying the DataMap Value
        hiveBotData.getInstructions().clear();
        hiveBotData.getDataMap().clear();
        responseBody =  mockMvc.perform(post("/hivecentral/iot.bot/xchange/save_set_datamap")
                .contentType(APPLICATION_JSON)
                .content(json(hiveBotData))
        ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.message", Matchers.is("Hello MOCKED.BOT.001. Data Info Saved. ")))
                .andExpect(jsonPath("$.status", Matchers.is("ACK")))
                .andExpect(jsonPath("$.dataMap.DataKey1").doesNotExist())
                .andExpect(jsonPath("$.dataMap.DataKey2").doesNotExist())
                //.andExpect(jsonPath("$.dataMap.[\"data.map.size\"]", Matchers.is("0")))
                //.andExpect(jsonPath("$.dataMap.[\"instruction.coll.size\"]", Matchers.is("1")))
                .andReturn().getResponse().getContentAsString();

    }

    @Test
    public void test06InstructionExecuted() throws Exception {
        HiveBotData hiveBotData = new HiveBotData();
        hiveBotData.setHiveBotId(botId);
        hiveBotData.setHiveBotVersion(botVersion);
        hiveBotData.setAccessKey(accessKey);
        String responseBody;


        //Test , Initilize with 2 Instruction ,
        hiveBotData.getInstructions().clear();
        hiveBotData.getInstructions().add(new Instruction(20007,"EXECUTE20007","now()","args7",false));
        hiveBotData.getInstructions().add(new Instruction(20008,"EXECUTE20008","cron()","args7",false));
        hiveBotData.getDataMap().put("DataKey1","Value1");
        responseBody =  mockMvc.perform(post("/hivecentral/iot.bot/xchange/save_set_instructions_set_datamap")
                .contentType(APPLICATION_JSON)
                .content(json(hiveBotData))
        ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.message", Matchers.is("Hello MOCKED.BOT.001. Data Info Saved. ")))
                .andExpect(jsonPath("$.status", Matchers.is("ACK")))
                .andExpect(jsonPath("$.dataMap.DataKey1", Matchers.is("Value1")))
                //.andExpect(jsonPath("$.dataMap.[\"data.map.size\"]", Matchers.is("1")))
                //.andExpect(jsonPath("$.dataMap.[\"instruction.coll.size\"]", Matchers.is("2")))
                .andReturn().getResponse().getContentAsString();

        //Test , Execute 1st instruction
        /* Not Longer Supported
        responseBody =  mockMvc.perform(post("/hivecentral/iot.bot/xchange/instruction_executed?" +
                "exe.instruction.command=EXECUTE20007&" +
                "exe.instruction.id=20007&" +
                "exe.instruction.result=CompletedMockExecute"
                )
                .contentType(APPLICATION_JSON)
                .content(json(hiveBotData))
        ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.message", Matchers.is("Ok MOCKED.BOT.001. Marked Instruction as Completed. Check remaining instructions.")))
                .andExpect(jsonPath("$.status", Matchers.is("ACK")))
                .andExpect(jsonPath("$.dataMap.DataKey1", Matchers.is("Value1")))
                .andExpect(jsonPath("$.dataMap.[\"data.map.size\"]", Matchers.is("1")))
                .andExpect(jsonPath("$.dataMap.[\"instruction.coll.size\"]", Matchers.is("1")))
                .andReturn().getResponse().getContentAsString();

        //Test , Execute 2nd instruction
        responseBody =  mockMvc.perform(post("/hivecentral/iot.bot/xchange/instruction_executed?" +
                        "exe.instruction.command=EXECUTE20008&" +
                        "exe.instruction.id=20008&" +
                        "exe.instruction.result=CompletedMockExecute"
                )
                        .contentType(APPLICATION_JSON)
                        .content(json(hiveBotData))
        ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.message", Matchers.is("Ok MOCKED.BOT.001. Marked Instruction as Completed. Check remaining instructions.")))
                .andExpect(jsonPath("$.status", Matchers.is("ACK")))
                .andExpect(jsonPath("$.dataMap.DataKey1", Matchers.is("Value1")))
                .andExpect(jsonPath("$.dataMap.[\"data.map.size\"]", Matchers.is("1")))
                .andExpect(jsonPath("$.dataMap.[\"instruction.coll.size\"]", Matchers.is("0")))
                .andReturn().getResponse().getContentAsString();
        * /
    }
    */


    @Test
    public void test99DummyPlaceholder() {
        Assert.assertEquals(1,1);
    }

}
