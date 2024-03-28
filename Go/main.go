package main

import (
	"fmt"
	"os"

	"github.com/siddhant-vij/Event-Management-System/services"
)

func main() {
	// bash: psql --username=postgres
	// bash: Enter your password
	// ------------------------------------
	// psql: CREATE DATABASE eventmgmt;

	if len(os.Args) < 2 {
		fmt.Println("No arguments provided. Please use:\n--c (for create tables)\n--i (for insert data)\n--q (for sql queries)")
		return
	}
	if len(os.Args) > 2 {
		fmt.Println("Invalid number of arguments.")
		return
	}
	switch os.Args[1] {
	case "--c":
		services.CreateTables()
	case "--i":
		services.InsertData()
	case "--ci":
		services.CreateTables()
		services.InsertData()
	case "--q":
		services.SqlQueries()
	default:
		fmt.Println("Invalid argument.")
	}

	// psql: \c postgres;
	// psql: DROP DATABASE eventmgmt;
}
