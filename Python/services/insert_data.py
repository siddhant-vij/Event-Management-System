from typing import Final

from psycopg2.extensions import connection

from utils.csv_reader import CsvReader
from utils.crud_operations import CrudOperations


class InsertData:
    _csv_reader: CsvReader = CsvReader()

    def __init__(self) -> None:
        self._read_data_and_generate_query()
        self.con = self._get_db_connection()
        self._insert_data()
        self._close_connection()

    def _get_db_connection(self) -> connection:
        db_name: Final = "eventmgmt"
        user: Final = "postgres"
        password: Final = "admin"
        return CrudOperations.connect_to_db(db_name, user, password)

    def _read_data_and_generate_query(self) -> None:
        self._csv_reader.read_events()
        self._csv_reader.read_attendees()
        self._csv_reader.read_registrations()

        self.query: str = ""

        for event in self._csv_reader.events:
            self.query += event.get_insert_event_query()
        for attendee in self._csv_reader.attendees:
            self.query += attendee.get_insert_attendee_query()
        for registration in self._csv_reader.registrations:
            self.query += registration.get_insert_registration_query()

    def _insert_data(self) -> None:
        count: Final[int] = CrudOperations.execute_query(
            self.con,
            self.query)
        self.con.commit()
        if count != -1:
            print("Data inserted successfully")

    def _close_connection(self) -> None:
        CrudOperations.close_connection(self.con)

