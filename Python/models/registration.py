from typing import Any, Final
from datetime import date


class Registration:
    def __init__(self, event_id: int, attendee_id: int, registered_at: date) -> None:
        self._event_id: Final[int] = self._validate_type(
            "event_id", event_id, int)
        self._attendee_id: Final[int] = self._validate_type(
            "attendee_id", attendee_id, int)
        self._registered_at: Final[date] = self._validate_type(
            "registered_at", registered_at, date)

    @property
    def event_id(self) -> int:
        return self._event_id

    @property
    def attendee_id(self) -> int:
        return self._attendee_id

    @property
    def registered_at(self) -> date:
        return self._registered_at

    @staticmethod
    def _validate_type(param_name: str, value: Any, expected_type: type) -> None:
        if not isinstance(value, expected_type):
            raise TypeError(
                f"{param_name} must be a {expected_type}, got {type(value).__name__}")
        return value

    def get_insert_registration_query(self) -> str:
        return f"INSERT INTO registrations (event_id, attendee_id, registered_at) VALUES ('{self.event_id}', '{self.attendee_id}', '{self.registered_at}');"
