from typing import Final
from psycopg2.extensions import connection

from utils.crud_operations import CrudOperations


class SqlQueries:

    def __init__(self):
        # Connect to DB
        self._con = self._get_db_connection()

        self._output_file_name: Final[str] = "output.txt"
        self._query_str: str = ""

        self._query_str = "List all events: "
        self.list_all_events(self._con)

        self._query_str = "List all events in a particular city: \"Lalganj\": "
        self.list_events_by_city(self._con, "Lalganj")

        self._query_str = "List all events in a particular year and month: \"December, 2022\": "
        self.list_events_by_year_and_month(self._con)

        self._query_str = "List all events in a particular date range: from \"2022-04-01\" to \"2022-06-30\": "
        self.list_events_by_date_range(self._con, "2022-04-01", "2022-06-30")

        self._query_str = "Find attendees registered for a specific event: event_name = \"eget nunc donec\": "
        self.find_attendees_for_event(self._con, "eget nunc donec")

        self._query_str = "Find attendees registered for a specific event before a certain date: event_name = \"eget nunc donec\" and date = \"2022-12-01\": "
        self.find_attendees_for_event_before_date(
            self._con, "eget nunc donec", "2022-12-01")

        self._query_str = "Count the number of attendees for each event: "
        self.count_attendees_for_each_event(self._con)

        self._query_str = "Update attendee information (e.g., email address) for a specific event: event_name = \"faucibus accumsan\": "
        self.update_attendee_info_for_event(
            self._con, "faucibus accumsan", "test@example.com")

        self._query_str = "Delete an event and its associated registrations: event_name = \"duis bibendum\": "
        self.delete_event_and_registrations(self._con, "duis bibendum")

        self._query_str = "Delete an attendee and its associated registrations: attendee_name = \"Roch Batters\": "
        self.delete_attendee_and_registrations(self._con, "Roch Batters")

        self._query_str = "List all attendees not registered for a specific event: event_name = \"eget nunc donec\": "
        self.list_attendees_not_registered_for_event(
            self._con, "eget nunc donec")

        self._query_str = "Find events without any registrations: "
        self.find_events_without_registrations(self._con)

        self._query_str = "List events that have more than 20 attendees registered: "
        self.find_events_with_more_than_n_attendees(self._con, 20)

        self._query_str = "Find events with no attendees registered after a certain date: date = \"2022-12-01\": "
        self.find_events_with_no_attendees_after_date(self._con, "2022-12-01")

        self._query_str = "Retrieve the latest 3 events based on registration date for a particular city: city = \"Kanayannur\": "
        self.list_latest_events_for_city(self._con, "Kanayannur", 3)

        self._query_str = "Generate a list of attendees who have attended more than 4 events: "
        self.list_attendees_with_more_than_n_events(self._con, 4)

        self._query_str = "Find the year & month combination with the highest number of events scheduled and list all events for that month: "
        self.find_year_and_month_with_highest_number_of_events(self._con)

        self._query_str = "Find the city with the highest number of events scheduled and list all events for that city: "
        self.find_city_with_highest_number_of_events(self._con)

        self._query_str = "Find the city with the highest number of events scheduled and list all the attendees who'll be attending these events with their dates of registration: "
        self.list_attendees_for_city_with_most_events(self._con)

        # Close database connection
        CrudOperations.close_connection(self._con)

    def _get_db_connection(self) -> connection:
        db_name: Final = "eventmgmt"
        user: Final = "postgres"
        password: Final = "admin"
        return CrudOperations.connect_to_db(db_name, user, password)

    def list_all_events(self, con: connection):
        query = f"""
            SELECT *
            FROM events
        """
        count = CrudOperations.execute_query(con, query)
        CrudOperations.print_query_result(
            self._query_str, self._output_file_name, count)

    def list_events_by_city(self, con: connection, city: str):
        query = f"""
            SELECT *
            FROM events
            WHERE event_location = '{city}'
        """
        count = CrudOperations.execute_query(con, query)
        CrudOperations.print_query_result(
            self._query_str, self._output_file_name, count)

    def list_events_by_year_and_month(self, con: connection):
        query = f"""
            SELECT *
            FROM events
            WHERE event_date BETWEEN '2022-12-01' AND '2022-12-31'
        """
        count = CrudOperations.execute_query(con, query)
        CrudOperations.print_query_result(
            self._query_str, self._output_file_name, count)

    def list_events_by_date_range(self, con: connection, start_date: str, end_date: str):
        query = f"""
            SELECT *
            FROM events
            WHERE event_date BETWEEN '{start_date}' AND '{end_date}'
        """
        count = CrudOperations.execute_query(con, query)
        CrudOperations.print_query_result(
            self._query_str, self._output_file_name, count)

    def find_attendees_for_event(self, con: connection, event_name: str):
        query = f"""
            SELECT attendees.attendee_name
            FROM attendees
            JOIN registrations
            ON attendees.attendee_id = registrations.attendee_id
            WHERE registrations.event_id = 
            (
                SELECT event_id
                FROM events
                WHERE event_name = '{event_name}'
            )
        """
        count = CrudOperations.execute_query(con, query)
        CrudOperations.print_query_result(
            self._query_str, self._output_file_name, count)

    def find_attendees_for_event_before_date(self, con: connection, event_name: str, date: str):
        query = f"""
            SELECT attendees.attendee_name
            FROM attendees
            JOIN registrations
            ON attendees.attendee_id = registrations.attendee_id
            JOIN events
            ON registrations.event_id = events.event_id
            WHERE events.event_name = '{event_name}'
            AND registrations.registered_at < '{date}'
        """
        count = CrudOperations.execute_query(con, query)
        CrudOperations.print_query_result(
            self._query_str, self._output_file_name, count)

    def count_attendees_for_each_event(self, con: connection):
        query = f"""
            SELECT events.event_name,
            COUNT(registrations.attendee_id)
            FROM events
            LEFT JOIN registrations
            ON events.event_id = registrations.event_id
            GROUP BY events.event_name
        """
        count = CrudOperations.execute_query(con, query)
        CrudOperations.print_query_result(
            self._query_str, self._output_file_name, count)

    def update_attendee_info_for_event(self, con: connection, event_name: str, new_email: str):
        query = f"""
            UPDATE attendees
            SET attendee_email = '{new_email}'
            WHERE attendee_id IN
            (
                SELECT attendee_id
                FROM registrations
                WHERE event_id IN
                (
                SELECT event_id
                FROM events
                WHERE event_name = '{event_name}'
                )
            )
        """
        count = CrudOperations.execute_query(con, query)
        CrudOperations.print_query_result(
            self._query_str, self._output_file_name, count)

    def delete_event_and_registrations(self, con: connection, event_name: str):
        query1 = f"""
            DELETE FROM registrations
            WHERE event_id =
            (
                SELECT event_id
                FROM events
                WHERE event_name = '{event_name}'
            )
        """
        query2 = f"""
            DELETE FROM events
            WHERE event_name = '{event_name}'
        """
        count = CrudOperations.execute_query(con, query1)
        CrudOperations.print_query_result(
            self._query_str, self._output_file_name, count)
        count = CrudOperations.execute_query(con, query2)
        CrudOperations.print_query_result(
            self._query_str, self._output_file_name, count)

    def delete_attendee_and_registrations(self, con: connection, attendee_name: str):
        query1 = f"""
            DELETE FROM registrations
            WHERE attendee_id =
            (
                SELECT attendee_id
                FROM attendees
                WHERE attendee_name = '{attendee_name}'
            )
        """
        query2 = f"""
            DELETE FROM attendees
            WHERE attendee_name = '{attendee_name}'
        """
        count = CrudOperations.execute_query(con, query1)
        CrudOperations.print_query_result(
            self._query_str, self._output_file_name, count)
        count = CrudOperations.execute_query(con, query2)
        CrudOperations.print_query_result(
            self._query_str, self._output_file_name, count)

    def list_attendees_not_registered_for_event(self, con: connection, event_name: str):
        query = f"""
            SELECT attendee_name
            FROM attendees
            WHERE attendee_id NOT IN
            (
                SELECT attendee_id
                FROM registrations
                WHERE event_id =
                (
                SELECT event_id
                FROM events
                WHERE event_name = '{event_name}'
                )
            )
        """
        count = CrudOperations.execute_query(con, query)
        CrudOperations.print_query_result(
            self._query_str, self._output_file_name, count)

    def find_events_without_registrations(self, con: connection):
        query = f"""
            SELECT event_name
            FROM events
            WHERE event_id
            NOT IN
            (
                SELECT event_id
                FROM registrations
            )
        """
        count = CrudOperations.execute_query(con, query)
        CrudOperations.print_query_result(
            self._query_str, self._output_file_name, count)

    def find_events_with_more_than_n_attendees(self, con: connection, attendee_threshold: int):
        query = f"""
            SELECT event_name
            FROM events
            WHERE event_id IN
            (
            SELECT event_id
            FROM registrations
            GROUP BY event_id
            HAVING COUNT(*) > {attendee_threshold}
            )
        """
        count = CrudOperations.execute_query(con, query)
        CrudOperations.print_query_result(
            self._query_str, self._output_file_name, count)

    def find_events_with_no_attendees_after_date(self, con: connection, date: str):
        query = f"""
            SELECT event_name
            FROM events
            WHERE event_id
            NOT IN
            (
                SELECT event_id
                FROM registrations
                WHERE registered_at > '{date}'
            )
        """
        count = CrudOperations.execute_query(con, query)
        CrudOperations.print_query_result(
            self._query_str, self._output_file_name, count)

    def list_latest_events_for_city(self, con: connection, city: str, number_of_events: int):
        query = f"""
            SELECT DISTINCT events.event_name,
            registrations.registered_at
            FROM events
            JOIN registrations
            ON events.event_id = registrations.event_id
            WHERE events.event_location = '{city}'
            ORDER BY registrations.registered_at
            DESC LIMIT {number_of_events}
        """
        count = CrudOperations.execute_query(con, query)
        CrudOperations.print_query_result(
            self._query_str, self._output_file_name, count)

    def list_attendees_with_more_than_n_events(self, con: connection, event_count: int):
        query = f"""
            SELECT attendees.attendee_name
            FROM attendees
            JOIN registrations
            ON attendees.attendee_id = registrations.attendee_id
            GROUP BY attendees.attendee_name
            HAVING COUNT(*) > {event_count}
        """
        count = CrudOperations.execute_query(con, query)
        CrudOperations.print_query_result(
            self._query_str, self._output_file_name, count)

    def find_year_and_month_with_highest_number_of_events(self, con: connection):
        query = f"""
            SELECT to_char(event_date, 'YYYY-MM') AS month_year,
            array_agg(event_name)
            FROM events
            GROUP BY month_year
            ORDER BY COUNT(*)
            DESC LIMIT 1
        """
        count = CrudOperations.execute_query(con, query)
        CrudOperations.print_query_result(
            self._query_str, self._output_file_name, count)

    def find_city_with_highest_number_of_events(self, con: connection):
        query = f"""
            SELECT event_location,
            array_agg(event_name)
            FROM events
            GROUP BY event_location
            ORDER BY COUNT(*)
            DESC LIMIT 1
        """
        count = CrudOperations.execute_query(con, query)
        CrudOperations.print_query_result(
            self._query_str, self._output_file_name, count)

    def list_attendees_for_city_with_most_events(self, con: connection):
        query = f"""
            WITH CityRank AS (
                SELECT event_location, COUNT(*) AS event_count
                FROM events
                GROUP BY event_location
                ORDER BY event_count DESC
                LIMIT 1
            )
            SELECT e.event_location, a.attendee_name, r.registered_at
            FROM events e
            JOIN registrations r
            ON e.event_id = r.event_id
            JOIN attendees a
            ON r.attendee_id = a.attendee_id
            WHERE e.event_location =
            (
                SELECT event_location
                FROM CityRank
            )
            ORDER BY r.registered_at
        """
        count = CrudOperations.execute_query(con, query)
        CrudOperations.print_query_result(
            self._query_str, self._output_file_name, count)
