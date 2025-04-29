package com.youssef.barber.interfaces;

import com.youssef.barber.models.Appointment;

public interface OnAppointmentCancelledListener {
    /**
     * Called when an appointment is successfully cancelled
     * @param appointment The cancelled appointment
     * @param position The position in the adapter (if applicable)
     */
    void onAppointmentCancelled(Appointment appointment, int position);

    /**
     * Called when appointment cancellation fails
     * @param errorMessage The error message describing the failure
     */
    void onCancellationFailed(String errorMessage);
}