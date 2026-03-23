package com.framework.base;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

public class baseTestUtilities {
    public WebDriver driver;



    public List<HashMap<String, String>> readJsonDataToMap(String filePath) throws IOException {
        //reading json data and adding it to a string variable
        String jsonData = FileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8);

        //converting string to HashMap using jackson databind
        ObjectMapper mapper = new ObjectMapper();
        // In this scenario we only have one data set inside the json data file. in case we have multiple data entries
        // in json data file sheet all of them will be return in form of List. that is why we have used hashMap inside a list.
        // and returning that list
        return mapper.readValue(jsonData, new TypeReference<List<HashMap<String, String>>>() {
        });
    }


}
