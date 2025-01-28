package com.olegtoropoff.petcareappointment.service.appointment;

import com.olegtoropoff.petcareappointment.dto.AppointmentDto;
import com.olegtoropoff.petcareappointment.dto.EntityConverter;
import com.olegtoropoff.petcareappointment.enums.AppointmentStatus;
import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.model.Appointment;
import com.olegtoropoff.petcareappointment.model.Pet;
import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.repository.AppointmentRepository;
import com.olegtoropoff.petcareappointment.repository.UserRepository;
import com.olegtoropoff.petcareappointment.request.AppointmentUpdateRequest;
import com.olegtoropoff.petcareappointment.request.BookAppointmentRequest;
import com.olegtoropoff.petcareappointment.service.pet.IPetService;
import com.olegtoropoff.petcareappointment.service.veterinarian.VeterinarianService;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Service class for managing appointments.
 * Provides functionality to create, update, cancel, approve, and manage appointments
 * along with handling pets associated with appointments.
 */
@Service
@RequiredArgsConstructor
public class AppointmentService implements IAppointmentService {
    private static final int MAX_ACTIVE_APPOINTMENTS = 2;

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final IPetService petService;
    private final EntityConverter<Appointment, AppointmentDto> entityConverter;
    private final ModelMapper modelMapper;

    /**
     * Creates a new appointment and associates pets with it.
     * Validates the sender and recipient, and ensures the sender does not exceed the maximum number of active appointments.
     *
     * @param request     the appointment and pet details.
     * @param senderId    the ID of the patient (sender).
     * @param recipientId the ID of the veterinarian (recipient).
     * @return the created appointment.
     */
    @Transactional
    @Override
    public AppointmentDto createAppointment(BookAppointmentRequest request, Long senderId, Long recipientId) {
        Optional<User> sender = userRepository.findById(senderId);

        if (sender.isPresent() && sender.get().getUserType().equals("VET")) {
            throw new IllegalStateException(FeedBackMessage.VET_APPOINTMENT_NOT_ALLOWED);
        }

        Optional<User> recipient = userRepository.findById(recipientId);

        if (sender.isPresent() && recipient.isPresent()) {
            int activeAppointmentsCount = countActiveAppointments(senderId);
            if (activeAppointmentsCount >= MAX_ACTIVE_APPOINTMENTS) {
                throw new IllegalStateException(FeedBackMessage.TOO_MANY_ACTIVE_APPOINTMENTS);
            }

            Appointment appointment = request.getAppointment();
            List<Pet> pets = request.getPets();
            pets.forEach(pet -> pet.setAppointment(appointment));
            List<Pet> savedPets = petService.savePetForAppointment(pets);

            appointment.setPets(savedPets);
            appointment.addPatient(sender.get());
            appointment.addVeterinarian(recipient.get());
            appointment.setAppointmentNo();
            appointment.setStatus(AppointmentStatus.WAITING_FOR_APPROVAL);

            Appointment createdAppointment = appointmentRepository.save(appointment);
            return entityConverter.mapEntityToDto(createdAppointment, AppointmentDto.class);
        }
        throw new ResourceNotFoundException(FeedBackMessage.SENDER_RECIPIENT_NOT_FOUND);
    }

    /**
     * Counts the number of active appointments for a specific patient.
     * An active appointment is defined as one whose status is neither COMPLETED, CANCELLED, nor NOT_APPROVED.
     *
     * @param senderId the ID of the patient whose active appointments are being counted.
     * @return the count of active appointments for the given patient.
     */
    private int countActiveAppointments(Long senderId) {
        return appointmentRepository.countByPatientIdAndStatusNotIn(
                senderId,
                List.of(AppointmentStatus.COMPLETED, AppointmentStatus.CANCELLED, AppointmentStatus.NOT_APPROVED)
        );
    }

    /**
     * Updates an existing appointment's date, time, and reason.
     *
     * @param id      the ID of the appointment to update.
     * @param request the updated appointment details.
     * @return the updated appointment.
     */
    @Override
    public AppointmentDto updateAppointment(Long id, AppointmentUpdateRequest request) {
        Appointment existingAppointment = getAppointmentById(id);
        if (!Objects.equals(existingAppointment.getStatus(), AppointmentStatus.WAITING_FOR_APPROVAL)) {
            throw new IllegalStateException(FeedBackMessage.APPOINTMENT_UPDATE_NOT_ALLOWED);
        }
        existingAppointment.setAppointmentDate(LocalDate.parse(request.getAppointmentDate()));
        existingAppointment.setAppointmentTime(LocalTime.parse(request.getAppointmentTime()));
        existingAppointment.setReason(request.getReason());
        Appointment updatedAppointment = appointmentRepository.save(existingAppointment);
        return entityConverter.mapEntityToDto(updatedAppointment, AppointmentDto.class);
    }

    /**
     * Adds a new pet to an existing appointment.
     *
     * @param id  the ID of the appointment.
     * @param pet the pet details to add.
     * @return the updated appointment.
     */
    @Override
    public AppointmentDto addPetForAppointment(Long id, Pet pet) {
        Appointment existingAppointment = getAppointmentById(id);
        if (!Objects.equals(existingAppointment.getStatus(), AppointmentStatus.WAITING_FOR_APPROVAL)) {
            throw new IllegalStateException(FeedBackMessage.OPERATION_NOT_ALLOWED);
        }

        pet.setAppointment(existingAppointment);
        Pet savedPet = petService.savePetForAppointment(pet);
        existingAppointment.getPets().add(savedPet);
        Appointment savedAppointment = appointmentRepository.save(existingAppointment);
        return entityConverter.mapEntityToDto(savedAppointment, AppointmentDto.class);
    }

    /**
     * Retrieves all appointments with pagination support.
     *
     * @param pageable the pagination details.
     * @return a page of appointment DTOs.
     */
    @Override
    public Page<AppointmentDto> getAllAppointments(Pageable pageable) {
        return appointmentRepository.findAll(pageable)
                .map(appointment -> modelMapper.map(appointment, AppointmentDto.class));
    }

    /**
     * Searches for appointments based on a search term with pagination support.
     *
     * @param search   the search term used to filter appointments. It is case-insensitive and may match
     *                 fields such as patient email, veterinarian email, or appointment number
     * @param pageable the pagination and sorting information
     * @return a paginated list of appointments matching the search criteria, mapped to AppointmentDto objects
     */
    @Override
    public Page<AppointmentDto> searchAppointments(String search, Pageable pageable) {
        return appointmentRepository.searchAppointments(search.toLowerCase(), pageable)
                .map(appointment -> modelMapper.map(appointment, AppointmentDto.class));
    }

    /**
     * Retrieves an appointment by its ID.
     *
     * @param id the ID of the appointment.
     * @return the appointment.
     */
    @Override
    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.APPOINTMENT_NOT_FOUND));
    }

    /**
     * Retrieves an appointment by its ID and maps it to a DTO.
     * This method fetches an appointment entity from the database using its ID
     * and converts it to a {@code AppointmentDto} using the {@code entityConverter}.
     *
     * @param id the unique identifier of the appointment to retrieve.
     * @return the {@code AppointmentDto} containing the appointment details.
     * @throws ResourceNotFoundException if the appointment with the given ID is not found.
     */
    @Override
    public AppointmentDto getAppointmentDtoById(Long id) {
        Appointment appointment = getAppointmentById(id);
        return entityConverter.mapEntityToDto(appointment, AppointmentDto.class);
    }

    /**
     * Deletes an appointment by its ID.
     *
     * @param id the ID of the appointment to delete.
     */
    @Override
    public void deleteAppointment(Long id) {
        appointmentRepository.findById(id)
                .ifPresentOrElse(appointmentRepository::delete, () -> {
                    throw new ResourceNotFoundException(FeedBackMessage.APPOINTMENT_NOT_FOUND);
                });
    }

    /**
     * Retrieves all appointments for a specific user.
     *
     * @param userId the ID of the user.
     * @return a list of appointment DTOs.
     */
    @Override
    public List<AppointmentDto> getUserAppointments(Long userId) {
        List<Appointment> appointments = appointmentRepository.findAllByUserId(userId);
        return appointments.stream()
                .map(appointment -> entityConverter.mapEntityToDto(appointment, AppointmentDto.class)).toList();
    }

    /**
     * Cancels an appointment that is waiting for approval.
     *
     * @param appointmentId the ID of the appointment to cancel.
     * @return the updated appointment with CANCELLED status.
     */
    @Override
    public AppointmentDto cancelAppointment(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .filter(appointment -> appointment.getStatus().equals(AppointmentStatus.WAITING_FOR_APPROVAL))
                .map(appointment -> {
                    appointment.setStatus(AppointmentStatus.CANCELLED);
                    Appointment updatedAppointment =  appointmentRepository.saveAndFlush(appointment);
                    return entityConverter.mapEntityToDto(updatedAppointment, AppointmentDto.class);
                }).orElseThrow(() -> new IllegalStateException(FeedBackMessage.APPOINTMENT_UPDATE_NOT_ALLOWED));
    }

    /**
     * Approves an appointment that is currently waiting for approval.
     *
     * @param appointmentId the ID of the appointment to approve.
     * @return the updated appointment with APPROVED status.
     * @throws IllegalStateException if the operation is not allowed due to the current appointment status.
     */
    @Override
    public AppointmentDto approveAppointment(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .filter(appointment -> appointment.getStatus().equals(AppointmentStatus.WAITING_FOR_APPROVAL))
                .map(appointment -> {
                    appointment.setStatus(AppointmentStatus.APPROVED);
                    Appointment updatedAppointment =  appointmentRepository.saveAndFlush(appointment);
                    return entityConverter.mapEntityToDto(updatedAppointment, AppointmentDto.class);
                }).orElseThrow(() -> new IllegalStateException(FeedBackMessage.OPERATION_NOT_ALLOWED));
    }

    /**
     * Declines an appointment that is currently waiting for approval.
     *
     * @param appointmentId the ID of the appointment to decline.
     * @return the updated appointment with NOT_APPROVED status.
     * @throws IllegalStateException if the operation is not allowed due to the current appointment status.
     */
    @Override
    public AppointmentDto declineAppointment(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .filter(appointment -> appointment.getStatus().equals(AppointmentStatus.WAITING_FOR_APPROVAL))
                .map(appointment -> {
                    appointment.setStatus(AppointmentStatus.NOT_APPROVED);
                    Appointment updatedAppointment = appointmentRepository.saveAndFlush(appointment);
                    return entityConverter.mapEntityToDto(updatedAppointment, AppointmentDto.class);
                }).orElseThrow(() -> new IllegalStateException(FeedBackMessage.OPERATION_NOT_ALLOWED));
    }

    /**
     * Counts the total number of appointments in the system.
     *
     * @return the total number of appointments.
     */
    @Override
    public long countAppointments() {
        return appointmentRepository.count();
    }

    /**
     * Retrieves a summary of appointments grouped by their status.
     * The summary includes the count of appointments for each status.
     *
     * @return a list of maps, where each map contains the status and the count.
     */
    @Override
    public List<Map<String, Object>> getAppointmentSummary() {
        return appointmentRepository.getAppointmentSummary();
    }

    /**
     * Retrieves the IDs of all appointments in the system.
     *
     * @return a list of appointment IDs.
     */
    @Override
    public List<Long> getAppointmentIds() {
        return appointmentRepository.findAllIds();
    }

    /**
     * Updates the status of an appointment based on the current date and time.
     * The status transitions are handled as follows:
     * - APPROVED -> UP_COMING if the appointment is upcoming.
     * - UP_COMING -> ON_GOING if the appointment is currently taking place.
     * - ON_GOING -> COMPLETED if the appointment has ended.
     * - WAITING_FOR_APPROVAL -> NOT_APPROVED if the appointment time has passed without approval.
     *
     * @param appointmentId the ID of the appointment to update.
     */
    @Override
    public void setAppointmentStatus(Long appointmentId) {
        Appointment appointment = getAppointmentById(appointmentId);
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        LocalTime appointmentEndTime = appointment.getAppointmentTime()
                .plusMinutes(VeterinarianService.APPOINTMENT_DURATION_MINUTES).truncatedTo(ChronoUnit.MINUTES);
        switch (appointment.getStatus()) {
            case APPROVED:
                if (currentDate.isBefore(appointment.getAppointmentDate()) ||
                    (currentDate.equals(appointment.getAppointmentDate()) && currentTime.isBefore(appointment.getAppointmentTime()))) {
                    appointment.setStatus(AppointmentStatus.UP_COMING);
                }
                break;

            case UP_COMING:
                if (currentDate.equals(appointment.getAppointmentDate()) &&
                    currentTime.isAfter(appointment.getAppointmentTime()) && currentTime.isBefore(appointmentEndTime)) {
                    appointment.setStatus(AppointmentStatus.ON_GOING);
                }
                break;
            case ON_GOING:
                if (currentDate.isAfter(appointment.getAppointmentDate()) ||
                    (currentDate.equals(appointment.getAppointmentDate()) && currentTime.isAfter(appointmentEndTime))) {
                    appointment.setStatus(AppointmentStatus.COMPLETED);
                }
                break;

            case WAITING_FOR_APPROVAL:
                if (currentDate.isAfter(appointment.getAppointmentDate()) ||
                    (currentDate.equals(appointment.getAppointmentDate()) && currentTime.isAfter(appointment.getAppointmentTime()))) {
                    appointment.setStatus(AppointmentStatus.NOT_APPROVED);
                }
                break;
        }
        appointmentRepository.save(appointment);
    }
}
