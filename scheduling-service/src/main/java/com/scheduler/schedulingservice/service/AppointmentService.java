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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final Logger logger = LoggerFactory.getLogger(AppointmentService.class);

    // TODO: extrair lógica de permissão para um componente separado
    private boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return false;
        return authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(auth -> auth.equals("ROLE_" + role));
    }

    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new SecurityException("Usuário não autenticado");
        }
        try {
            return Long.parseLong(authentication.getName());
        } catch (NumberFormatException e) {
            throw new SecurityException("ID do usuário autenticado inválido: " + authentication.getName());
        }
    }

    private boolean canSeeAll() {
        return hasRole(UserRoles.MEDICO) || hasRole(UserRoles.ENFERMEIRO);
    }

    private boolean canCreate() {
        return hasRole(UserRoles.ENFERMEIRO);
    }

    private boolean canEdit() {
        return hasRole(UserRoles.MEDICO);
    }

    public Optional<AppointmentDto> findById(Long id) {
        Optional<Appointment> opt = appointmentRepository.findById(id);
        if (opt.isEmpty()) return Optional.empty();
        Appointment appointment = opt.get();
        Long userId = getAuthenticatedUserId();
        if (!canSeeAll() && !appointment.getPatientId().equals(userId)) {
            logger.warn("Acesso negado: Paciente {} tentou acessar consulta de outro paciente (consultaId: {})", userId, id);
            throw new SecurityException("Paciente [" + userId + "] só pode acessar suas próprias consultas");
        }

        return Optional.of(mapToDto(appointment));
    }

    public List<AppointmentDto> findAll() {
        if (!canSeeAll()) {
            Long userId = getAuthenticatedUserId();
            logger.warn("Acesso negado: Paciente {} tentou acessar todas as consultas", userId);
            throw new SecurityException("Paciente não pode acessar todas as consultas");
        }

        return appointmentRepository.findAll().stream()
            .map(this::mapToDto)
            .toList();
    }

    public List<AppointmentDto> findByPatientId(Long patientId) {
        Long userId = getAuthenticatedUserId();
        if (!canSeeAll() && !patientId.equals(userId)) {
            logger.warn("Acesso negado: Paciente {} tentou acessar consultas de outro paciente (patientId: {})", userId, patientId);
            throw new SecurityException("Paciente [" + userId + "] só pode acessar suas próprias consultas");
        }
        // Médicos e enfermeiros podem acessar qualquer paciente
        return appointmentRepository.findByPatientId(patientId).stream()
            .map(this::mapToDto)
            .toList();
    }

    public List<AppointmentDto> findByDoctorId(Long doctorId) {
        if (!canSeeAll()) {
            Long userId = getAuthenticatedUserId();
            logger.warn("Acesso negado: Paciente {} tentou acessar consultas de médico (doctorId: {})", userId, doctorId);
            throw new SecurityException("Paciente não pode acessar consultas de médicos");
        }
        // Médicos e enfermeiros podem acessar
        return appointmentRepository.findByDoctorId(doctorId).stream()
            .map(this::mapToDto)
            .toList();
    }

    public AppointmentDto createAppointment(CreateAppointmentDto input) {
        if (!canCreate()) {
            Long userId = getAuthenticatedUserId();
            logger.warn("Acesso negado: Usuário {} tentou registrar consulta sem permissão (roles insuficientes)", userId);
            throw new SecurityException("Apenas médicos ou enfermeiros podem registrar consultas");
        }
        validateAppointmentInput(input);
        validateUserRoles(input.getPatientId(), input.getDoctorId());
        Appointment appointment = new Appointment();
        appointment.setPatientId(input.getPatientId());
        appointment.setDoctorId(input.getDoctorId());
        appointment.setAppointmentDate(LocalDateTime.parse(input.getAppointmentDate(), formatter));
        return mapToDto(appointmentRepository.save(appointment));
    }

    public Optional<AppointmentDto> updateAppointment(Long id, UpdateAppointmentDto input) {
        if (!canEdit()) {
            Long userId = getAuthenticatedUserId();
            logger.warn("Acesso negado: Usuário {} tentou editar consulta {} sem permissão (roles insuficientes)", userId, id);
            throw new SecurityException("Apenas médicos ou enfermeiros podem editar consultas");
        }
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
        if (!canEdit() && !canCreate()) {
            Long userId = getAuthenticatedUserId();
            logger.warn("Acesso negado: Usuário {} tentou deletar consulta {} sem permissão (roles insuficientes)", userId, id);
            throw new SecurityException("Apenas médicos ou enfermeiros podem deletar consultas");
        }

        if (appointmentRepository.existsById(id)) {
            appointmentRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // TODO: should be @Valid in controller
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
