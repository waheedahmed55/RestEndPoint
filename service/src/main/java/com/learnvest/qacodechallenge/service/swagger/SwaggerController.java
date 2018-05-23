package com.learnvest.qacodechallenge.service.swagger;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.swagger.annotations.ApiOperation;

@Controller
public class SwaggerController {

    @Autowired
    ServletContext servletContext;

    @RequestMapping(value = "/swaggerui", method = RequestMethod.GET)
    @ApiOperation(value = "Redirects to Swagger-UI")
    public String swaggerui() {
        return "redirect:webjars/swagger-ui/2.1.8-M1/index.html?url=" + servletContext.getContextPath() + "/v2/api-docs/";
    }

}
