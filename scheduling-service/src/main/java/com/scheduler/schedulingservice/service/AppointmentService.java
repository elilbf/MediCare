package com.scheduler.schedulingservice.service;

import com.scheduler.schedulingservice.dto.CreateAppointmentDto;
import com.scheduler.schedulingservice.dto.UpdateAppointmentDto;
import com.scheduler.schedulingservice.dto.AppointmentDto;
import com.scheduler.schedulingservice.dto.PatientDto;
import com.scheduler.schedulingservice.dto.DoctorDto;
import com.scheduler.schedulingservice.dto.UsuarioDto;
import com.scheduler.schedulingservice.entities.Appointment;
import com.scheduler.schedulingservice.repositories.AppointmentRepository;
import com.scheduler.schedulingservice.client.UserServiceClient;
import com.scheduler.schedulingservice.constants.UserRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public Optional<AppointmentDto> findById(Long id) {
        Optional<Appointment> opt = appointmentRepository.findById(id);
        if (opt.isEmpty()) return Optional.empty();
        Appointment appointment = opt.get();

        return Optional.of(mapToDto(appointment));
    }

    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
    public List<AppointmentDto> findAll() {
        return appointmentRepository.findAll().stream()
            .map(this::mapToDto)
            .toList();
    }

    public List<AppointmentDto> findByPatientId(Long patientId, String sinceDate, String untilDate) {
        LocalDateTime since = (sinceDate != null && !sinceDate.isBlank()) ? LocalDateTime.parse(sinceDate, formatter) : null;
        LocalDateTime until = (untilDate != null && !untilDate.isBlank()) ? LocalDateTime.parse(untilDate, formatter) : null;
        return appointmentRepository.findByPatientIdWithDateRange(patientId, since, until).stream()
            .map(this::mapToDto)
            .toList();
    }


    public List<AppointmentDto> findByDoctorId(Long doctorId, String sinceDate, String untilDate) {
        LocalDateTime since = (sinceDate != null && !sinceDate.isBlank()) ? LocalDateTime.parse(sinceDate, formatter) : null;
        LocalDateTime until = (untilDate != null && !untilDate.isBlank()) ? LocalDateTime.parse(untilDate, formatter) : null;
        return appointmentRepository.findByDoctorIdWithDateRange(doctorId, since, until).stream()
            .map(this::mapToDto)
            .toList();
    }

    public AppointmentDto createAppointment(CreateAppointmentDto input) {
        validateAppointmentInput(input);
        validateUserRoles(input.getPatientId(), input.getDoctorId());
        Appointment appointment = new Appointment();
        appointment.setPatientId(input.getPatientId());
        appointment.setDoctorId(input.getDoctorId());
        appointment.setAppointmentDate(LocalDateTime.parse(input.getAppointmentDate(), formatter));
        return mapToDto(appointmentRepository.save(appointment));
    }

    public Optional<AppointmentDto> updateAppointment(Long id, UpdateAppointmentDto input) {
        return appointmentRepository.findById(id)
            .map(appointment -> {
                if (input.getPatientId() != null) {
                    appointment.setPatientId(input.getPatientId());
                }
                if (input.getDoctorId() != null) {
                    appointment.setDoctorId(input.getDoctorId());
                }
                if (input.getAppointmentDate() != null) {
                    appointment.setAppointmentDate(LocalDateTime.parse(input.getAppointmentDate(), formatter));
                }
                return mapToDto(appointmentRepository.save(appointment));
            });
    }

    public boolean deleteAppointment(Long id) {
        if (appointmentRepository.existsById(id)) {
            appointmentRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private void validateAppointmentInput(CreateAppointmentDto input) {
        if (input.getPatientId() == null) {
            throw new IllegalArgumentException("Patient ID is required");
        }
        if (input.getDoctorId() == null) {
            throw new IllegalArgumentException("Doctor ID is required");
        }
        if (input.getAppointmentDate() == null) {
            throw new IllegalArgumentException("Appointment date is required");
        }

        LocalDateTime appointmentDateTime = LocalDateTime.parse(input.getAppointmentDate(), formatter);
        if (appointmentDateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Appointment date cannot be in the past");
        }
    }

    private void validateUserRoles(Long patientId, Long doctorId) {
        UsuarioDto patient = userServiceClient.buscarUsuarioPorId(patientId);
        if (patient == null) {
            throw new IllegalArgumentException("Paciente com ID " + patientId + " não encontrado ou serviço indisponível");
        }
        
        if (patient.getRoles() == null || !patient.getRoles().contains(UserRoles.PACIENTE)) {
            throw new IllegalArgumentException("Usuário com ID " + patientId + " não é um paciente");
        }
        
        UsuarioDto doctor = userServiceClient.buscarUsuarioPorId(doctorId);
        if (doctor == null) {
            throw new IllegalArgumentException("Médico com ID " + doctorId + " não encontrado ou serviço indisponível");
        }
        
        if (doctor.getRoles() == null || !doctor.getRoles().contains(UserRoles.MEDICO)) {
            throw new IllegalArgumentException("Usuário com ID " + doctorId + " não é um médico");
        }
    }

    private AppointmentDto mapToDto(Appointment appointment) {
        AppointmentDto dto = new AppointmentDto();
        dto.setId(appointment.getId());
        
        UsuarioDto patientData = userServiceClient.buscarUsuarioPorId(appointment.getPatientId());
        PatientDto patientDto = new PatientDto();
        patientDto.setId(appointment.getPatientId());
        if (patientData != null) {
            patientDto.setName(patientData.getNome());
            patientDto.setEmail(patientData.getEmail());
        } else {
            patientDto.setName("Paciente " + appointment.getPatientId() + " (Dados indisponíveis)");
            patientDto.setEmail("paciente" + appointment.getPatientId() + "@fallback.com");
        }
        dto.setPatient(patientDto);
        
        UsuarioDto doctorData = userServiceClient.buscarUsuarioPorId(appointment.getDoctorId());
        DoctorDto doctorDto = new DoctorDto();
        doctorDto.setId(appointment.getDoctorId());
        if (doctorData != null) {
            doctorDto.setName(doctorData.getNome());
            doctorDto.setEmail(doctorData.getEmail());
        } else {
            doctorDto.setName("Médico " + appointment.getDoctorId() + " (Dados indisponíveis)");
            doctorDto.setEmail("medico" + appointment.getDoctorId() + "@fallback.com");
        }
        dto.setDoctor(doctorDto);
        
        dto.setAppointmentDate(appointment.getAppointmentDate());
        dto.setCreatedDate(appointment.getCreatedDate());
        dto.setLastUpdateDate(appointment.getLastUpdateDate());
        
        return dto;
    }
}
