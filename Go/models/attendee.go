package models

import "fmt"

type Attendee struct {
	attendeeName   string
	atttendeeEmail string
}

func NewAttendee(attendeeName, atttendeeEmail string) *Attendee {
	return &Attendee{
		attendeeName:   attendeeName,
		atttendeeEmail: atttendeeEmail,
	}
}

func (a *Attendee) GetInsertAttendeeQuery() string {
	return fmt.Sprintf("INSERT INTO attendees (attendee_name, attendee_email) VALUES ('%s', '%s');", a.attendeeName, a.atttendeeEmail)
}
