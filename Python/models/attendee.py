from typing import Any, Final


class Attendee:
    def __init__(self, attendee_name: str, attendee_email: str) -> None:
        self._attendee_name: Final[str] = self._validate_type(
            "attendee_name", attendee_name, str)
        self._attendee_email: Final[str] = self._validate_type(
            "attendee_email", attendee_email, str)

    @property
    def attendee_name(self) -> str:
        return self._attendee_name

    @property
    def attendee_email(self) -> str:
        return self._attendee_email

    @staticmethod
    def _validate_type(param_name: str, value: Any, expected_type: type) -> None:
        if not isinstance(value, expected_type):
            raise TypeError(
                f"{param_name} must be a {expected_type}, got {type(value).__name__}")
        return value

    def get_insert_attendee_query(self) -> str:
        return f"INSERT INTO attendees (attendee_name, attendee_email) VALUES ('{self.attendee_name}', '{self.attendee_email}');"
