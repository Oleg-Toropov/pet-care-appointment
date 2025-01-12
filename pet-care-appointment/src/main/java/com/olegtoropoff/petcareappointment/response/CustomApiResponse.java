package com.olegtoropoff.petcareappointment.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents a standardized response structure for API endpoints.
 * <p>
 * This class encapsulates a response message and additional data
 * returned from API calls to ensure consistency across the application.
 *
 * <p>
 * Example usage:
 * <pre>{@code
 * CustomApiResponse response = new CustomApiResponse("Operation successful", someData);
 * }</pre>
 */
@Data
@AllArgsConstructor
public class CustomApiResponse {

    /**
     * A message providing information about the result of the operation.
     * <p>
     * Example: "Operation successful" or "Error occurred while processing the request".
     */
    private String message;

    /**
     * The data associated with the response, such as a retrieved entity or a result object.
     * <p>
     * This can be any object type, allowing flexibility in the data returned by API calls.
     * Example: A user object, a list of items, or null in case of an error.
     */
    private Object data;
}
