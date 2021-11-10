package com.ainnotate.aidas.cucumber;

import com.ainnotate.aidas.AinnotateserviceApp;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

@CucumberContextConfiguration
@SpringBootTest(classes = AinnotateserviceApp.class)
@WebAppConfiguration
public class CucumberTestContextConfiguration {}
