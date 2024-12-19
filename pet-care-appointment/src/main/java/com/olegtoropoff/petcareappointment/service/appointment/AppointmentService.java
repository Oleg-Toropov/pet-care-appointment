package com.olegtoropoff.petcareappointment.service.appointment;

import com.olegtoropoff.petcareappointment.dto.AppointmentDto;
import com.olegtoropoff.petcareappointment.dto.EntityConverter;
import com.olegtoropoff.petcareappointment.dto.PetDto;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService implements IAppointmentService {
    private static final int MAX_ACTIVE_APPOINTMENTS = 2;

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final IPetService petService;
    private final EntityConverter<Appointment, AppointmentDto> entityConverter;
    private final EntityConverter<Pet, PetDto> petEntityConverter;
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public Appointment createAppointment(BookAppointmentRequest request, Long senderId, Long recipientId) {
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
            return appointmentRepository.save(appointment);
        }
        throw new ResourceNotFoundException(FeedBackMessage.SENDER_RECIPIENT_NOT_FOUND);
    }

    private int countActiveAppointments(Long senderId) {
        return appointmentRepository.countByPatientIdAndStatusNotIn(
                senderId,
                List.of(AppointmentStatus.COMPLETED, AppointmentStatus.CANCELLED, AppointmentStatus.NOT_APPROVED)
        );
    }

    @Override
    public Appointment updateAppointment(Long id, AppointmentUpdateRequest request) {
        Appointment existingAppointment = getAppointmentById(id);
        if (!Objects.equals(existingAppointment.getStatus(), AppointmentStatus.WAITING_FOR_APPROVAL)) {
            throw new IllegalStateException(FeedBackMessage.APPOINTMENT_UPDATE_NOT_ALLOWED);
        }
        existingAppointment.setAppointmentDate(LocalDate.parse(request.getAppointmentDate()));
        existingAppointment.setAppointmentTime(LocalTime.parse(request.getAppointmentTime()));
        existingAppointment.setReason(request.getReason());
        return appointmentRepository.save(existingAppointment);
    }

    @Override
    public Appointment addPetForAppointment(Long id, Pet pet) {
        Appointment existingAppointment = getAppointmentById(id);
        if (!Objects.equals(existingAppointment.getStatus(), AppointmentStatus.WAITING_FOR_APPROVAL)) {
            throw new IllegalStateException(FeedBackMessage.OPERATION_NOT_ALLOWED);
        }

        pet.setAppointment(existingAppointment);
        Pet savedPet = petService.savePetForAppointment(pet);
        existingAppointment.getPets().add(savedPet);
        return appointmentRepository.save(existingAppointment);
    }

    @Override
    public Page<AppointmentDto> getAllAppointments(Pageable pageable) {
        return appointmentRepository.findAll(pageable)
                .map(appointment -> modelMapper.map(appointment, AppointmentDto.class));
    }

    @Override
    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.APPOINTMENT_NOT_FOUND));
    }

    @Override
    public void deleteAppointment(Long id) {
        appointmentRepository.findById(id)
                .ifPresentOrElse(appointmentRepository::delete, () -> {
                    throw new ResourceNotFoundException(FeedBackMessage.APPOINTMENT_NOT_FOUND);
                });
    }

    @Override
    public List<AppointmentDto> getUserAppointments(Long userId) {
        List<Appointment> appointments = appointmentRepository.findAllByUserId(userId);
        return appointments.stream()
                .map(appointment -> {
                    AppointmentDto appointmentDto = entityConverter.mapEntityToDto(appointment, AppointmentDto.class);
                    List<PetDto> petDtos = appointment.getPets().stream()
                            .map(pet -> petEntityConverter.mapEntityToDto(pet, PetDto.class)).toList();
                    appointmentDto.setPets(petDtos);
                    return appointmentDto;
                }).toList();
    }

    @Override
    public Appointment cancelAppointment(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .filter(appointment -> appointment.getStatus().equals(AppointmentStatus.WAITING_FOR_APPROVAL))
                .map(appointment -> {
                    appointment.setStatus(AppointmentStatus.CANCELLED);
                    return appointmentRepository.saveAndFlush(appointment);
                }).orElseThrow(() -> new IllegalStateException(FeedBackMessage.APPOINTMENT_UPDATE_NOT_ALLOWED));
    }

    @Override
    public Appointment approveAppointment(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .filter(appointment -> appointment.getStatus().equals(AppointmentStatus.WAITING_FOR_APPROVAL))
                .map(appointment -> {
                    appointment.setStatus(AppointmentStatus.APPROVED);
                    return appointmentRepository.saveAndFlush(appointment);
                }).orElseThrow(() -> new IllegalStateException(FeedBackMessage.OPERATION_NOT_ALLOWED));
    }

    @Override
    public Appointment declineAppointment(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .filter(appointment -> appointment.getStatus().equals(AppointmentStatus.WAITING_FOR_APPROVAL))
                .map(appointment -> {
                    appointment.setStatus(AppointmentStatus.NOT_APPROVED);
                    return appointmentRepository.saveAndFlush(appointment);
                }).orElseThrow(() -> new IllegalStateException(FeedBackMessage.OPERATION_NOT_ALLOWED));
    }

    @Override
    public long countAppointments() {
        return appointmentRepository.count();
    }

    @Override
    public List<Map<String, Object>> getAppointmentSummary() {
        return appointmentRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(Appointment::getStatus, Collectors.counting()))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() > 0)
                .map(entry -> createStatusSummaryMap(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private Map<String, Object> createStatusSummaryMap(AppointmentStatus status, Long value) {
        Map<String, Object> summaryMap = new HashMap<>();
        summaryMap.put("name", status);
        summaryMap.put("value", value);
        return summaryMap;
    }

    @Override
    public List<Long> getAppointmentIds() {
        List<Appointment> appointments = appointmentRepository.findAll();
        return  appointments.stream()
                .map(Appointment::getId)
                .collect(Collectors.toList());
    }

    @Override
    public void setAppointmentStatus(Long appointmentId){
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
