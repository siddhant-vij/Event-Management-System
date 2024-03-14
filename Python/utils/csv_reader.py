from typing import List, Type, TypeVar
from datetime import datetime
import csv

from models.attendee import Attendee
from models.event import Event
from models.registration import Registration


T = TypeVar("T", bound=Event | Attendee | Registration)

EVENTS_CSV_FILE = "Problem/events.csv"
ATTENDEES_CSV_FILE = "Problem/attendees.csv"
REGISTRATIONS_CSV_FILE = "Problem/registrations.csv"


class CsvReader:
    def __init__(self):
        self._events: List[Event] = []
        self._attendees: List[Attendee] = []
        self._registrations: List[Registration] = []

    @property
    def events(self) -> List[Event]:
        return self._events

    @property
    def attendees(self) -> List[Attendee]:
        return self._attendees

    @property
    def registrations(self) -> List[Registration]:
        return self._registrations

    def _read_data(self, csv_file: str, data_class: Type[T]) -> List[T]:
        with open(csv_file, "r", newline='') as f:
            reader = csv.reader(f)
            next(reader)  # skip the header
            result = []
            for row in reader:
                if issubclass(data_class, Attendee):
                    data = Attendee(
                        row[0].strip(),
                        row[1].strip(),
                    )
                elif issubclass(data_class, Event):
                    data = Event(
                        row[0].strip(),
                        datetime.strptime(
                            row[1].strip(),
                            "%m/%d/%Y %H:%M").date(),
                        row[2].strip(),
                    )
                else:
                    data = Registration(
                        int(row[0].strip()),
                        int(row[1].strip()),
                        datetime.strptime(
                            row[2].strip(),
                            "%m/%d/%Y %H:%M").date(),
                    )
                result.append(data)
            return result

    def read_events(self) -> None:
        self._events = self._read_data(EVENTS_CSV_FILE, Event)

    def read_attendees(self) -> None:
        self._attendees = self._read_data(ATTENDEES_CSV_FILE, Attendee)

    def read_registrations(self) -> None:
        self._registrations = self._read_data(
            REGISTRATIONS_CSV_FILE, Registration)
