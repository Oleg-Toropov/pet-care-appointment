package com.olegtoropoff.petcareappointment.controller;

import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.model.VetBiography;
import com.olegtoropoff.petcareappointment.response.CustomApiResponse;
import com.olegtoropoff.petcareappointment.service.vetbiography.IVetBiographyService;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import com.olegtoropoff.petcareappointment.utils.UrlMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping(UrlMapping.BIOGRAPHIES)
@RequiredArgsConstructor
public class VetBiographyController {
    private final IVetBiographyService vetBiographyService;

    @GetMapping(UrlMapping.GET_BIOGRAPHY_BY_VET_ID)
    public ResponseEntity<CustomApiResponse> getVetBiographyByVetId(@PathVariable Long vetId) {
        try {
            VetBiography vetBiography = vetBiographyService.getVetBiographyByVetId(vetId);
            return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.BIOGRAPHY_FOUND, vetBiography));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new CustomApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new CustomApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    @PostMapping(UrlMapping.SAVE_BIOGRAPHY)
    public ResponseEntity<CustomApiResponse> saveVetBiography(@PathVariable Long vetId, @RequestBody VetBiography request) {
        try {
            VetBiography vetBiography = vetBiographyService.saveVetBiography(request, vetId);
            return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.BIOGRAPHY_SAVED_SUCCESS, vetBiography));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new CustomApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new CustomApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    @PutMapping(UrlMapping.UPDATE_BIOGRAPHY)
    public ResponseEntity<CustomApiResponse> updateVetBiography(@PathVariable Long id, @RequestBody VetBiography request) {
        try {
            VetBiography vetBiography = vetBiographyService.updateVetBiography(request, id);
            return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.BIOGRAPHY_UPDATED_SUCCESS, vetBiography));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new CustomApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new CustomApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    @DeleteMapping(UrlMapping.DELETE_BIOGRAPHY)
    public ResponseEntity<CustomApiResponse>  deleteVetBiography(@PathVariable Long id) {
        try {
            vetBiographyService.deleteVetBiography(id);
            return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.BIOGRAPHY_DELETED_SUCCESS, null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new CustomApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new CustomApiResponse(FeedBackMessage.ERROR, null));
        }
    }
}
