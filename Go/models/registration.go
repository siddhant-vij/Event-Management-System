package models

import (
	"fmt"
	"time"
)

type Registration struct {
	eventID          int
	attendeeID       int
	registrationDate time.Time
}

func NewRegistration(eventID, attendeeID int, registrationDate time.Time) *Registration {
	return &Registration{
		eventID:          eventID,
		attendeeID:       attendeeID,
		registrationDate: registrationDate,
	}
}

func (r *Registration) GetInsertRegistrationQuery() string {
	return fmt.Sprintf("INSERT INTO registrations (event_id, attendee_id, registered_at) VALUES (%d, %d, '%s');", r.eventID, r.attendeeID, r.registrationDate.Format("2006-01-02 15:04:05"))
}
