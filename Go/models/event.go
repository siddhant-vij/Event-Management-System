package models

import (
	"fmt"
	"time"
)

type Event struct {
	eventName     string
	eventDate     time.Time
	eventLocation string
}

func NewEvent(eventName, eventLocation string, eventDate time.Time) *Event {
	return &Event{
		eventName:     eventName,
		eventDate:     eventDate,
		eventLocation: eventLocation,
	}
}

func (e *Event) GetInsertEventQuery() string {
	return fmt.Sprintf("INSERT INTO events (event_name, event_date, event_location) VALUES ('%s', '%s', '%s');", e.eventName, e.eventDate.Format("2006-01-02"), e.eventLocation)
}
