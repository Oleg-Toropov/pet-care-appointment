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

/**
 * Controller for managing veterinarian biographies. Provides endpoints to create, update, retrieve,
 * and delete biographies of veterinarians.
 */
@RestController
@RequestMapping(UrlMapping.BIOGRAPHIES)
@RequiredArgsConstructor
public class VetBiographyController {
    private final IVetBiographyService vetBiographyService;

    /**
     * Retrieves a veterinarian's biography by their ID.
     *
     * @param vetId the ID of the veterinarian
     * @return a {@link ResponseEntity} containing the veterinarian biography or an error message
     */
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

    /**
     * Saves a new biography for a veterinarian.
     *
     * @param vetId the ID of the veterinarian
     * @param request the biography details
     * @return a {@link ResponseEntity} containing the saved biography or an error message
     */
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

    /**
     * Updates an existing biography for a veterinarian.
     *
     * @param id the ID of the biography to update
     * @param request the updated biography details
     * @return a {@link ResponseEntity} containing the updated biography or an error message
     */
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

    /**
     * Deletes a biography by its ID.
     *
     * @param id the ID of the biography to delete
     * @return a {@link ResponseEntity} indicating the success of the operation or an error message
     */
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
