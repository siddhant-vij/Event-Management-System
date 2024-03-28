package utils

import (
	"context"
	"fmt"
	"os"

	"github.com/jackc/pgx/v5"
)

func ConnectToDB(dbName, user, password, host, port string) (*pgx.Conn, error) {
	url := fmt.Sprintf("postgres://%s:%s@%s:%s/%s", user, password, host, port, dbName)
	return pgx.Connect(context.Background(), url)
}

func CloseDB(conn *pgx.Conn) {
	conn.Close(context.Background())
}

func ExecuteQuery(conn *pgx.Conn, query string) (pgx.Rows, error) {
	return conn.Query(context.Background(), query)
}

func ExecuteUpdateQuery(conn *pgx.Conn, query string) (int, error) {
	ct, err := conn.Exec(context.Background(), query)
	if err != nil {
		return -1, err
	}
	return int(ct.RowsAffected()), nil
}

func PrintQueryResultToFile(queryStr string, rows pgx.Rows, filePath string) error {
	file, err := os.OpenFile(filePath, os.O_APPEND|os.O_WRONLY|os.O_CREATE, 0644)
	if err != nil {
		return err
	}
	defer file.Close()

	_, err = file.WriteString(queryStr + ":\n")
	if err != nil {
		return err
	}

	for rows.Next() {
		values, err := rows.Values()
		if err != nil {
			return err
		}
		resultString := ""
		for _, value := range values {
			resultString += fmt.Sprintf("%v", value) + "|"
		}
		resultString = resultString[:len(resultString)-1]

		_, err = file.WriteString(resultString + "\n")
		if err != nil {
			return err
		}
	}
	_, err = file.WriteString("\n\n\n\n\n")
	if err != nil {
		return err
	}
	return nil
}

func PrintRowsAffectedToFile(queryStr string, rowsAffected int, filePath string) error {
	file, err := os.OpenFile(filePath, os.O_APPEND|os.O_WRONLY|os.O_CREATE, 0644)
	if err != nil {
		return err
	}
	defer file.Close()

	_, err = file.WriteString(queryStr + ":\n")
	if err != nil {
		return err
	}

	_, err = file.WriteString(fmt.Sprintf("Rows Affected: %d\n\n\n\n\n\n", rowsAffected))
	if err != nil {
		return err
	}
	return nil
}