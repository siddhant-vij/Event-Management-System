package utils

import (
	"encoding/csv"
	"fmt"
	"os"
	"strconv"
	"time"

	"github.com/siddhant-vij/Event-Management-System/models"
)

const (
	EVENTS_CSV_FILE        = "Problem/events.csv"
	ATTENDEES_CSV_FILE     = "Problem/attendees.csv"
	REGISTRATIONS_CSV_FILE = "Problem/registrations.csv"
)

type CsvReader struct {
	events        []*models.Event
	attendees     []*models.Attendee
	registrations []*models.Registration
}

func NewCsvReader() *CsvReader {
	return &CsvReader{
		events:        make([]*models.Event, 0),
		attendees:     make([]*models.Attendee, 0),
		registrations: make([]*models.Registration, 0),
	}
}

func (c *CsvReader) GetEvents() []*models.Event {
	return c.events
}

func (c *CsvReader) GetAttendees() []*models.Attendee {
	return c.attendees
}

func (c *CsvReader) GetRegistrations() []*models.Registration {
	return c.registrations
}

func (c *CsvReader) ReadDateFromCSV(tableName string) {
	switch tableName {
	case "events":
		c.readFromCSV(EVENTS_CSV_FILE, c.readEventsFromCSV)
	case "attendees":
		c.readFromCSV(ATTENDEES_CSV_FILE, c.readAttendeesFromCSV)
	case "registrations":
		c.readFromCSV(REGISTRATIONS_CSV_FILE, c.readRegistrationsFromCSV)
	default:
		fmt.Printf("invalid table name: %s\n", tableName)
	}
}

func (c *CsvReader) readFromCSV(csvFilePath string, reader func(br *csv.Reader) error) {
	file, err := os.Open(csvFilePath)
	if err != nil {
		panic(err)
	}
	defer file.Close()

	err = reader(csv.NewReader(file))
	if err != nil {
		panic(err)
	}
}

func (c *CsvReader) readEventsFromCSV(br *csv.Reader) error {
	records, err := br.ReadAll()
	if err != nil {
		return err
	}

	for _, record := range records {
		if len(record) == 0 || record[0] == "event_name" {
			continue
		}
		eventName := record[0]
		eventDate, err := ParseDate(record[1])
		if err != nil {
			return err
		}
		eventLocation := record[2]

		event := models.NewEvent(eventName, eventLocation, eventDate)

		c.events = append(c.events, event)
	}

	return nil
}

func (c *CsvReader) readAttendeesFromCSV(br *csv.Reader) error {
	records, err := br.ReadAll()
	if err != nil {
		return err
	}

	for _, record := range records {
		if len(record) == 0 || record[0] == "attendee_name" {
			continue
		}
		attendeeName := record[0]
		attendeeEmail := record[1]

		attendee := models.NewAttendee(attendeeName, attendeeEmail)

		c.attendees = append(c.attendees, attendee)
	}

	return nil
}

func (c *CsvReader) readRegistrationsFromCSV(br *csv.Reader) error {
	records, err := br.ReadAll()
	if err != nil {
		return err
	}

	for _, record := range records {
		if len(record) == 0 || record[0] == "event_id" {
			continue
		}
		event_id, err := strconv.Atoi(record[0])
		if err != nil {
			return err
		}
		attendee_id, err := strconv.Atoi(record[1])
		if err != nil {
			return err
		}
		registration_date, err := ParseDate(record[2])
		if err != nil {
			return err
		}

		registration := models.NewRegistration(event_id, attendee_id, registration_date)

		c.registrations = append(c.registrations, registration)
	}

	return nil
}

func ParseDate(dateString string) (time.Time, error) {
	layouts := []string{
		"01/02/2006 15:04",
		"01/02/2006 5:04",
		"01/2/2006 15:04",
		"01/2/2006 5:04",
		"1/02/2006 15:04",
		"1/02/2006 5:04",
		"1/2/2006 15:04",
		"1/2/2006 5:04",
	}
	for _, layout := range layouts {
		if date, err := time.Parse(layout, dateString); err == nil {
			return date, nil
		}
	}
	return time.Time{}, fmt.Errorf("invalid date format: %s", dateString)
}
