package com.olegtoropoff.petcareappointment.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;


/**
 * Utility class for reading default user data from a JSON file.
 * This class provides a method to load and parse the default user data
 * stored in the file located at the classpath.
 */
public class DefaultDataReader {

    private static final ObjectMapper objectMapper = createObjectMapper();

    /**
     * Creates and configures a shared instance of ObjectMapper with required modules.
     *
     * @return a configured ObjectMapper instance.
     */
    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    /**
     * Reads default user data from the JSON file.
     *
     * @return an instance of {@link DefaultUserData} containing the loaded user data.
     * @throws IOException if there is an issue reading the file or parsing the JSON content.
     */
    public static DefaultUserData readDefaultUserData() throws IOException {
        return readData("data/default-users.json", DefaultUserData.class);
    }

    /**
     * Reads default appointment data from the JSON file.
     *
     * @return an instance of {@link DefaultAppointmentData} containing the loaded appointment data.
     * @throws IOException if there is an issue reading the file or parsing the JSON content.
     */
    public static DefaultAppointmentData readDefaultAppointmentData() throws IOException {
        return readData("data/default-appointments.json", DefaultAppointmentData.class);
    }

    /**
     * Reads default review data from the JSON file.
     *
     * @return an instance of {@link DefaultReviewData} containing the loaded review data.
     * @throws IOException if there is an issue reading the file or parsing the JSON content.
     */
    public static DefaultReviewData readDefaultReviewData() throws IOException {
        return readData("data/default-reviews.json", DefaultReviewData.class);
    }

    /**
     * Generic method to read and parse data from a JSON file.
     *
     * @param <T>        the type of data to be parsed.
     * @param filePath   the path to the JSON file in the classpath.
     * @param valueType  the class type to which the JSON should be mapped.
     * @return an instance of the specified type containing the parsed data.
     * @throws IOException if there is an issue reading the file or parsing the JSON content.
     */
    private static <T> T readData(String filePath, Class<T> valueType) throws IOException {
        try (var inputStream = new ClassPathResource(filePath).getInputStream()) {
            return objectMapper.readValue(inputStream, valueType);
        }
    }
}