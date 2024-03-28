package services

import (
	"fmt"

	"github.com/jackc/pgx/v5"
	"github.com/siddhant-vij/Event-Management-System/utils"
)

func InsertData() {
	query := readDataAndGeneratorQuery()
	
	connection, err := utils.ConnectToDB(dbName, user, password, host, port)
	if err != nil {
		panic(err)
	}

	insertData(query, connection)

	utils.CloseDB(connection)
}

func readDataAndGeneratorQuery() string {
	query := ""
	csvReader := utils.NewCsvReader()
	csvReader.ReadDateFromCSV("events")
	csvReader.ReadDateFromCSV("attendees")
	csvReader.ReadDateFromCSV("registrations")

	for _, event := range csvReader.GetEvents() {
		query += event.GetInsertEventQuery()
	}
	for _, attendee := range csvReader.GetAttendees() {
		query += attendee.GetInsertAttendeeQuery()
	}
	for _, registration := range csvReader.GetRegistrations() {
		query += registration.GetInsertRegistrationQuery()
	}

	return query
}

func insertData(query string, conn *pgx.Conn) {
	_, err := utils.ExecuteUpdateQuery(conn, query)
	if err != nil {
		panic(err)
	}
	fmt.Println("Data inserted successfully")
}
