from typing import Any, Final
from datetime import date


class Event:
    def __init__(self, event_name: str, event_date: date, event_location: str) -> None:
        self._event_name: Final[str] = self._validate_type(
            "event_name", event_name, str)
        self._event_date: Final[date] = self._validate_type(
            "event_date", event_date, date)
        self._event_location: Final[str] = self._validate_type(
            "event_location", event_location, str)

    @staticmethod
    def _validate_type(param_name: str, value: Any, expected_type: type) -> None:
        if not isinstance(value, expected_type):
            raise TypeError(
                f"{param_name} must be a {expected_type}, got {type(value).__name__}")
        return value

    @property
    def event_name(self) -> str:
        return self._event_name

    @property
    def event_date(self) -> date:
        return self._event_date

    @property
    def event_location(self) -> str:
        return self._event_location

    def get_insert_event_query(self) -> str:
        return f"INSERT INTO events (event_name, event_date, event_location) VALUES ('{self.event_name}', '{self.event_date}', '{self.event_location}');"
