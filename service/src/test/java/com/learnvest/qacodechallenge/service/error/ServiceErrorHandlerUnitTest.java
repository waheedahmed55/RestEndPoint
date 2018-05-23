package com.learnvest.qacodechallenge.service.error;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnvest.qacodechallenge.commons.model.error.ErrorSummary;
import com.learnvest.qacodechallenge.service.config.UnitTestConfig;
import com.learnvest.qacodechallenge.service.config.WebMvcTestConfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { UnitTestConfig.class, ServiceErrorHandlerUnitTest.Config.class, WebMvcTestConfig.class })
public class ServiceErrorHandlerUnitTest {

    @Configuration
    @ComponentScan(basePackages = { "com.learnvest.qacodechallenge.service.error" }, resourcePattern = "**/ServiceErrorHandler.class")
    public static class Config extends WebMvcConfigurerAdapter {
    }

    @Autowired
    private WebApplicationContext ctx;

    private MockMvc mockMvc;

    private ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build();
        mapper = new ObjectMapper();
    }

    /**
     * The following test exercises the {@link ServiceErrorHandler#error} ()} endpoint.
     * @throws Exception via {@link org.springframework.test.web.servlet.MockMvc}
     */
    @Test
    public void error() throws Exception {
        RequestBuilder rb = get(ServiceErrorHandler.PATH).contentType(MediaType.APPLICATION_JSON);
        MockHttpServletResponse r = mockMvc.perform(rb).andReturn().getResponse();
        assertEquals("HTTP State Code", HttpServletResponse.SC_OK, r.getStatus());

        String content = r.getContentAsString();
        assertNotNull(content);
        assertFalse(content.isEmpty());

        ErrorSummary errorSummary = mapper.readValue(content, ErrorSummary.class);
        assertNotNull(errorSummary);
        assertEquals(HttpServletResponse.SC_OK, errorSummary.getStatus().intValue());
        assertNotNull(errorSummary.getTimestamp());
        assertNotNull(errorSummary.getError());
        assertNotNull(errorSummary.getMessage());
    }

}
