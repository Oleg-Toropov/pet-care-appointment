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
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class AppointmentServiceTest {

    @InjectMocks
    private AppointmentService appointmentService;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private IPetService petService;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @Spy
    private EntityConverter<Appointment, AppointmentDto> entityConverter = new EntityConverter<>(new ModelMapper());

    @Test
    void getUserAppointments_WhenValid_ReturnsAppointmentDtoList() {
        Long userId = 1L;
        Appointment appointment = new Appointment();
        appointment.setId(1L);
        Pet pet = new Pet();
        pet.setId(1L);
        pet.setName("Buddy");
        appointment.setPets(List.of(pet));
        when(appointmentRepository.findAllByUserId(userId)).thenReturn(List.of(appointment));

        List<AppointmentDto> result = appointmentService.getUserAppointments(userId);

        assertEquals(1, result.size());
        assertNotNull(result.get(0).getPets());
        assertEquals(pet.getName(), result.get(0).getPets().get(0).getName());
        verify(appointmentRepository, times(1)).findAllByUserId(userId);
        verify(entityConverter, times(1)).mapEntityToDto(appointment, AppointmentDto.class);
    }

    @Test
    void createAppointment_WhenValidRequest_ReturnsAppointment() {
        Long senderId = 1L;
        User sender = new User();
        sender.setId(senderId);
        sender.setUserType("PATIENT");

        Long recipientId = 2L;
        User recipient = new User();
        recipient.setId(recipientId);
        recipient.setUserType("VET");

        Appointment appointment = new Appointment();
        appointment.setAppointmentDate(LocalDate.now());
        appointment.setAppointmentTime(LocalTime.now());
        List<Pet> pets = List.of(new Pet());

        BookAppointmentRequest request = new BookAppointmentRequest();
        request.setAppointment(appointment);
        request.setPets(pets);

        when(userRepository.findById(senderId)).thenReturn(Optional.of(sender));
        when(userRepository.findById(recipientId)).thenReturn(Optional.of(recipient));
        when(petService.savePetForAppointment(pets)).thenReturn(pets);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        AppointmentDto result = appointmentService.createAppointment(request, senderId, recipientId);

        assertNotNull(result);
        assertEquals(appointment.getAppointmentDate(), result.getAppointmentDate());
        assertEquals(appointment.getAppointmentTime(), result.getAppointmentTime());
        verify(userRepository, times(2)).findById(anyLong());
        verify(petService, times(1)).savePetForAppointment(pets);
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void createAppointment_WhenSenderIsVet_ThrowsException() {
        Long senderId = 1L;
        Long recipientId = 2L;
        User sender = new User();
        sender.setId(senderId);
        sender.setUserType("VET");
        when(userRepository.findById(senderId)).thenReturn(Optional.of(sender));

        BookAppointmentRequest request = new BookAppointmentRequest();

        Exception exception = assertThrows(IllegalStateException.class, () ->
                appointmentService.createAppointment(request, senderId, recipientId));

        assertEquals(FeedBackMessage.VET_APPOINTMENT_NOT_ALLOWED, exception.getMessage());
        verify(userRepository, times(1)).findById(senderId);
    }

    @Test
    void getAppointmentById_WhenAppointmentExists_ReturnsAppointment() {
        Long appointmentId = 1L;
        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        Appointment result = appointmentService.getAppointmentById(appointmentId);

        assertNotNull(result);
        assertEquals(appointment, result);
        verify(appointmentRepository, times(1)).findById(appointmentId);
    }

    @Test
    void getAppointmentById_WhenAppointmentDoesNotExist_ThrowsException() {
        Long appointmentId = 1L;
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                appointmentService.getAppointmentById(appointmentId));

        assertEquals(FeedBackMessage.APPOINTMENT_NOT_FOUND, exception.getMessage());
        verify(appointmentRepository, times(1)).findById(appointmentId);
    }

    @Test
    void getAllAppointments_WhenCalled_ReturnsPageOfAppointments() {
        Pageable pageable = mock(Pageable.class);
        Appointment appointment = new Appointment();
        Page<Appointment> appointmentsPage = new PageImpl<>(List.of(appointment));
        when(appointmentRepository.findAll(pageable)).thenReturn(appointmentsPage);

        Page<AppointmentDto> result = appointmentService.getAllAppointments(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(appointmentRepository, times(1)).findAll(pageable);
        verify(modelMapper, times(1)).map(appointment, AppointmentDto.class);
    }

    @Test
    void deleteAppointment_WhenAppointmentExists_DeletesAppointment() {
        Long appointmentId = 1L;
        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        appointmentService.deleteAppointment(appointmentId);

        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(appointmentRepository, times(1)).delete(appointment);
    }

    @Test
    void deleteAppointment_WhenAppointmentDoesNotExist_ThrowsException() {
        Long appointmentId = 1L;

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                appointmentService.deleteAppointment(appointmentId));

        assertEquals(FeedBackMessage.APPOINTMENT_NOT_FOUND, exception.getMessage());
        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(appointmentRepository, never()).delete(any(Appointment.class));
    }

    @Test
    void setAppointmentStatus_WhenStatusApprovedAndAppointmentInFuture_UpdatesToUpComing() {
        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setStatus(AppointmentStatus.APPROVED);
        appointment.setAppointmentDate(LocalDate.now().plusDays(1));
        appointment.setAppointmentTime(LocalTime.of(15, 0));

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        appointmentService.setAppointmentStatus(1L);

        assertEquals(AppointmentStatus.UP_COMING, appointment.getStatus());
        verify(appointmentRepository, times(1)).save(appointment);
    }

    @Test
    void setAppointmentStatus_WhenStatusUpComingAndAppointmentOngoing_UpdatesToOnGoing() {
        Appointment appointment = new Appointment();
        appointment.setId(2L);
        appointment.setStatus(AppointmentStatus.UP_COMING);
        appointment.setAppointmentDate(LocalDate.now());
        appointment.setAppointmentTime(LocalTime.now().minusMinutes(15));

        when(appointmentRepository.findById(2L)).thenReturn(Optional.of(appointment));

        appointmentService.setAppointmentStatus(2L);

        assertEquals(AppointmentStatus.ON_GOING, appointment.getStatus());
        verify(appointmentRepository, times(1)).save(appointment);
    }

    @Test
    void setAppointmentStatus_WhenStatusOnGoingAndAppointmentCompleted_UpdatesToCompleted() {
        Appointment appointment = new Appointment();
        appointment.setId(3L);
        appointment.setStatus(AppointmentStatus.ON_GOING);
        appointment.setAppointmentDate(LocalDate.now());
        appointment.setAppointmentTime(LocalTime.now().minusMinutes(45));

        when(appointmentRepository.findById(3L)).thenReturn(Optional.of(appointment));

        appointmentService.setAppointmentStatus(3L);

        assertEquals(AppointmentStatus.COMPLETED, appointment.getStatus());
        verify(appointmentRepository, times(1)).save(appointment);
    }

    @Test
    void setAppointmentStatus_WhenStatusWaitingForApprovalAndAppointmentExpired_UpdatesToNotApproved() {
        Appointment appointment = new Appointment();
        appointment.setId(4L);
        appointment.setStatus(AppointmentStatus.WAITING_FOR_APPROVAL);
        appointment.setAppointmentDate(LocalDate.now().minusDays(1));
        appointment.setAppointmentTime(LocalTime.now());

        when(appointmentRepository.findById(4L)).thenReturn(Optional.of(appointment));

        appointmentService.setAppointmentStatus(4L);

        assertEquals(AppointmentStatus.NOT_APPROVED, appointment.getStatus());
        verify(appointmentRepository, times(1)).save(appointment);
    }

    @Test
    void setAppointmentStatus_WhenStatusApprovedAndAppointmentPast_StaysApproved() {
        Appointment appointment = new Appointment();
        appointment.setId(5L);
        appointment.setStatus(AppointmentStatus.APPROVED);
        appointment.setAppointmentDate(LocalDate.now().minusDays(1));
        appointment.setAppointmentTime(LocalTime.now());

        when(appointmentRepository.findById(5L)).thenReturn(Optional.of(appointment));

        appointmentService.setAppointmentStatus(5L);

        assertEquals(AppointmentStatus.APPROVED, appointment.getStatus());
        verify(appointmentRepository, times(1)).save(appointment);
    }

    @Test
    void setAppointmentStatus_WhenAppointmentNotFound_ThrowsResourceNotFoundException() {
        when(appointmentRepository.findById(6L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> appointmentService.setAppointmentStatus(6L));

        assertEquals(FeedBackMessage.APPOINTMENT_NOT_FOUND, exception.getMessage());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void updateAppointment_WhenValid_ReturnsUpdatedAppointment() {
        Long appointmentId = 1L;
        AppointmentUpdateRequest request = new AppointmentUpdateRequest();
        request.setAppointmentDate("2024-12-31");
        request.setAppointmentTime("12:30");
        request.setReason("Routine check-up");

        Appointment existingAppointment = new Appointment();
        existingAppointment.setId(appointmentId);
        existingAppointment.setStatus(AppointmentStatus.WAITING_FOR_APPROVAL);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(existingAppointment);

        AppointmentDto updatedAppointmentDto = appointmentService.updateAppointment(appointmentId, request);

        assertEquals(LocalDate.parse(request.getAppointmentDate()), updatedAppointmentDto.getAppointmentDate());
        assertEquals(LocalTime.parse(request.getAppointmentTime()), updatedAppointmentDto.getAppointmentTime());
        assertEquals(request.getReason(), updatedAppointmentDto.getReason());
        verify(appointmentRepository, times(1)).save(existingAppointment);
    }

    @Test
    void updateAppointment_WhenNotWaitingForApproval_ThrowsException() {
        Long appointmentId = 1L;

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(new Appointment()));

        Exception exception = assertThrows(IllegalStateException.class, () -> appointmentService.updateAppointment(appointmentId, new AppointmentUpdateRequest()));

        assertEquals(FeedBackMessage.APPOINTMENT_UPDATE_NOT_ALLOWED, exception.getMessage());
    }

    @Test
    void addPetForAppointment_WhenValid_ReturnsUpdatedAppointment() {
        Long appointmentId = 1L;
        Pet pet = new Pet();
        pet.setName("Buddy");

        Appointment existingAppointment = new Appointment();
        existingAppointment.setStatus(AppointmentStatus.WAITING_FOR_APPROVAL);
        existingAppointment.setPets(new ArrayList<>());

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingAppointment));
        when(petService.savePetForAppointment(pet)).thenReturn(pet);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(existingAppointment);

        AppointmentDto updatedAppointmentDto = appointmentService.addPetForAppointment(appointmentId, pet);

        assertEquals(updatedAppointmentDto.getPets().get(0).getName(), pet.getName());
        verify(petService, times(1)).savePetForAppointment(pet);
    }

    @Test
    void addPetForAppointment_WhenNotWaitingForApproval_ThrowsException() {
        Long appointmentId = 1L;

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(new Appointment()));

        Exception exception = assertThrows(IllegalStateException.class, () -> appointmentService.addPetForAppointment(appointmentId, new Pet()));

        assertEquals(FeedBackMessage.OPERATION_NOT_ALLOWED, exception.getMessage());
    }

    @Test
    void cancelAppointment_WhenValid_ChangesStatusToCancelled() {
        Long appointmentId = 1L;
        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setStatus(AppointmentStatus.WAITING_FOR_APPROVAL);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.saveAndFlush(appointment)).thenReturn(appointment);

        AppointmentDto result = appointmentService.cancelAppointment(appointmentId);

        assertEquals(AppointmentStatus.CANCELLED, result.getStatus());
        verify(appointmentRepository, times(1)).saveAndFlush(appointment);
    }

    @Test
    void cancelAppointment_WhenInvalid_ThrowsException() {
        Long appointmentId = 1L;

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalStateException.class, () -> appointmentService.cancelAppointment(appointmentId));

        assertEquals(FeedBackMessage.APPOINTMENT_UPDATE_NOT_ALLOWED, exception.getMessage());
    }

    @Test
    void approveAppointment_WhenValid_ChangesStatusToApproved() {
        Long appointmentId = 1L;
        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setStatus(AppointmentStatus.WAITING_FOR_APPROVAL);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.saveAndFlush(appointment)).thenReturn(appointment);

        AppointmentDto result = appointmentService.approveAppointment(appointmentId);

        assertEquals(AppointmentStatus.APPROVED, result.getStatus());
        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(appointmentRepository, times(1)).saveAndFlush(appointment);
    }

    @Test
    void declineAppointment_WhenValid_ChangesStatusToNotApproved() {
        Long appointmentId = 1L;
        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setStatus(AppointmentStatus.WAITING_FOR_APPROVAL);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.saveAndFlush(appointment)).thenReturn(appointment);

        AppointmentDto result = appointmentService.declineAppointment(appointmentId);

        assertEquals(AppointmentStatus.NOT_APPROVED, result.getStatus());
        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(appointmentRepository, times(1)).saveAndFlush(appointment);
    }

    @Test
    void getAppointmentIds_WhenAppointmentsExist_ReturnsListOfIds() {
        Long id1 = 1L, id2 = 2L, id3 = 3L;

        when(appointmentRepository.findAllIds()).thenReturn(List.of(id1, id2, id3));

        List<Long> result = appointmentService.getAppointmentIds();

        assertEquals(3, result.size());
        assertEquals(List.of(id1, id2, id3), result);
        verify(appointmentRepository, times(1)).findAllIds();
    }
}
