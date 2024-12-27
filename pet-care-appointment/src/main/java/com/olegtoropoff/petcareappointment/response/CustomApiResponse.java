package com.olegtoropoff.petcareappointment.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomApiResponse {
    private String message;
    private Object data;
}
