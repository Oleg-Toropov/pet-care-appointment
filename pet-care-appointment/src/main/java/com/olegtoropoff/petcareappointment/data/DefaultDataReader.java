package com.olegtoropoff.petcareappointment.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;


/**
 * Utility class for reading default user data from a JSON file.
 * This class provides a method to load and parse the default user data
 * stored in the file located at the classpath.
 */
public class DefaultDataReader {

    /**
     * Reads the default user data from a JSON file located in the classpath.
     * The file is expected to be named `default-users.json` and should
     * conform to the structure of the {@link DefaultUserData} class.
     *
     * @return an instance of {@link DefaultUserData} containing the loaded user data.
     * @throws IOException if there is an issue reading the file or parsing the JSON content.
     */
    public static DefaultUserData readDefaultUserData() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new ClassPathResource("data/default-users.json").getInputStream(), DefaultUserData.class);
    }
}