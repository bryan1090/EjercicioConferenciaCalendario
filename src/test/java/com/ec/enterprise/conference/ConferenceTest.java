package com.ec.enterprise.conference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.ec.enterprise.conference.controller.ConferenceController;
import com.ec.enterprise.conference.service.ConferenceServiceImpl;

@SpringBootTest
// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

@AutoConfigureMockMvc
public class ConferenceTest {

    @Autowired
    private ConferenceController conferenceController;

    @MockBean
    private ConferenceServiceImpl conferenceService;

    @Autowired
    private MockMvc mockMvc;

    private static final String input = "\r\n" + //
            "Writing Fast Tests Against Enterprise Rails 60min\r\n" + //
            "\r\n" + //
            "Overdoing it in Python 45min\r\n" + //
            "\r\n" + //
            "Lua for the Masses 30min\r\n" + //
            "\r\n" + //
            "Ruby Errors from Mismatched Gem Versions 45min\r\n" + //
            "\r\n" + //
            "Common Ruby Errors 45min\r\n" + //
            "\r\n" + //
            "Rails for Python Developers lightning\r\n" + //
            "\r\n" + //
            "Communicating Over Distance 60min\r\n" + //
            "\r\n" + //
            "Accounting-Driven Development 45min\r\n" + //
            "\r\n" + //
            "Woah 30min\r\n" + //
            "\r\n" + //
            "Sit Down and Write 30min\r\n" + //
            "\r\n" + //
            "Pair Programming vs Noise 45min\r\n" + //
            "\r\n" + //
            "Rails Magic 60min\r\n" + //
            "\r\n" + //
            "Ruby on Rails: Why We Should Move On 60min\r\n" + //
            "\r\n" + //
            "Clojure Ate Scala (on my project) 45min\r\n" + //
            "\r\n" + //
            "Programming in the Boondocks of Seattle 30min\r\n" + //
            "\r\n" + //
            "Ruby vs. Clojure for Back-End Development 30min\r\n" + //
            "\r\n" + //
            "Ruby on Rails Legacy App Maintenance 60min\r\n" + //
            "\r\n" + //
            "A World Without HackerNews 30min\r\n" + //
            "\r\n" + //
            "User Interface CSS in Rails Apps 30min";

    private static final String exp_res = "Track 1:\r\n" + //
            "09:00 a. m. Writing Fast Tests Against Enterprise Rails\r\n" + //
            "10:00 a. m. Overdoing it in Python\r\n" + //
            "10:45 a. m. Lua for the Masses\r\n" + //
            "11:15 a. m. Ruby Errors from Mismatched Gem Versions\r\n" + //
            "12:00 p. m. Lunch\r\n" + //
            "01:00 p. m. Common Ruby Errors\r\n" + //
            "01:45 p. m. Communicating Over Distance\r\n" + //
            "02:45 p. m. Accounting-Driven Development\r\n" + //
            "03:30 p. m. Woah\r\n" + //
            "04:00 p. m. Networking Event\r\n" + //
            "Track 2:\r\n" + //
            "09:00 a. m. Sit Down and Write\r\n" + //
            "09:30 a. m. Pair Programming vs Noise\r\n" + //
            "10:15 a. m. Rails Magic\r\n" + //
            "11:15 a. m. Clojure Ate Scala (on my project)\r\n" + //
            "12:00 p. m. Lunch\r\n" + //
            "01:00 p. m. Ruby on Rails: Why We Should Move On\r\n" + //
            "02:00 p. m. Programming in the Boondocks of Seattle\r\n" + //
            "02:30 p. m. Ruby vs. Clojure for Back-End Development\r\n" + //
            "03:00 p. m. Ruby on Rails Legacy App Maintenance\r\n" + //
            "04:00 p. m. Networking Event\r\n" + //
            "Track 3:\r\n" + //
            "09:00 a. m. A World Without HackerNews\r\n" + //
            "09:30 a. m. User Interface CSS in Rails Apps\r\n" + //
            "10:00 a. m. Rails for Python Developers lightning\r\n" + //
            "12:00 p. m. Lunch\r\n" + //
            "04:00 p. m. Networking Event\r\n" + //
            "";

    @Test
    public void itReturnsOutputCorrectly() throws Exception {
        when(conferenceService.scheduler(input)).thenReturn(exp_res);
        ResponseEntity<String> result = conferenceController.schedule(input);
        assertNotNull(result.getBody());
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(exp_res, result.getBody());
        verify(conferenceService).scheduler(input);
    }

    @Test
    public void returnErrorWhenInputIsEmpty() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/conference/schedule");
        request.content("");
        request.contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Test
    public void itContainsTrack() throws Exception {
        when(conferenceService.scheduler(input)).thenReturn(exp_res);
        ResponseEntity<String> result = conferenceController.schedule(input);
        assertNotNull(result.getBody());
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody().contains("Track 1:"));
        assertTrue(result.getBody().contains("Track 2:"));

    }

    @Test
    public void itStartsNetworkingAtFour() throws Exception {
        when(conferenceService.scheduler(input)).thenReturn(exp_res);
        ResponseEntity<String> result = conferenceController.schedule(input);
        assertNotNull(result.getBody());
        assertEquals(HttpStatus.OK, result.getStatusCode());

        String regex = "(\\d{2}):(\\d{2}).*Networking Event";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(result.getBody());
        if (matcher.find()) {
            String num = matcher.group(1);
            assertEquals("04", num);
        }

    }

}
