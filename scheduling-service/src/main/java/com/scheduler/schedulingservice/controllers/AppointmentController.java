package com.scheduler.schedulingservice.controllers;

import com.scheduler.schedulingservice.dto.CreateAppointmentDto;
import com.scheduler.schedulingservice.dto.UpdateAppointmentDto;
import com.scheduler.schedulingservice.dto.AppointmentDto;
import com.scheduler.schedulingservice.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import jakarta.validation.Valid;
import java.util.List;

@Controller
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @QueryMapping
    public AppointmentDto getAppointment(@Argument Long id) {
        return appointmentService.findById(id).orElse(null);
    }

    @QueryMapping
    public List<AppointmentDto> getAllAppointments() {
        return appointmentService.findAll();
    }

    @QueryMapping
    public List<AppointmentDto> getAppointmentsByPatient(@Argument Long patientId, @Argument String sinceDate, @Argument String untilDate) {
        return appointmentService.findByPatientId(patientId, sinceDate, untilDate);
    }

    @QueryMapping
    public List<AppointmentDto> getAppointmentsByDoctor(@Argument Long doctorId, @Argument String sinceDate, @Argument String untilDate) {
        return appointmentService.findByDoctorId(doctorId, sinceDate, untilDate);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ENFERMEIRO', 'MEDICO')")
    public AppointmentDto createAppointment(@Valid @Argument CreateAppointmentDto input) {
        return appointmentService.createAppointment(input);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ENFERMEIRO', 'MEDICO')")
    public AppointmentDto updateAppointment(@Argument Long id, @Valid @Argument UpdateAppointmentDto input) {
        return appointmentService.updateAppointment(id, input).orElse(null);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ENFERMEIRO', 'MEDICO')")
    public Boolean deleteAppointment(@Argument Long id) {
        return appointmentService.deleteAppointment(id);
    }
}
