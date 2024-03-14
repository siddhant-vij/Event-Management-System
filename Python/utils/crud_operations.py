import psycopg2
from typing import Optional, Union


class CrudOperations:
    @staticmethod
    def connect_to_db(db_name: str, user: str, password: str) -> psycopg2.extensions.connection:
        conn: Optional[psycopg2.extensions.connection] = None
        try:
            conn = psycopg2.connect(
                host="localhost",
                port=5432,
                database=db_name,
                user=user,
                password=password
            )
        except (Exception, psycopg2.Error) as error:
            print(f"Error while connecting to the PostgreSQL server: {error}")
        finally:
            return conn

    @staticmethod
    def close_connection(con: psycopg2.extensions.connection) -> None:
        if con:
            try:
                con.close()
            except (Exception, psycopg2.Error) as error:
                print(f"Error while closing the database connection: {error}")

    @staticmethod
    def execute_query(con: psycopg2.extensions.connection, query: str) -> Union[list, int]:
        cur = None
        try:
            cur = con.cursor()
            cur.execute(query)
            if cur.description:  # SELECT query
                rows = cur.fetchall()
            elif cur.rowcount == -1:  # CREATE TABLE
                rows = 0
            else:  # INSERT, UPDATE, DELETE queries
                rows = cur.rowcount
        except (Exception, psycopg2.DatabaseError) as error:
            print(f"Error: {error}")
            rows = -1
        finally:
            if cur:
                cur.close()
        return rows

    @staticmethod
    def print_query_result(queryStr: str, file_path: str, rows: Union[list, int]) -> None:
        with open(file_path, "a") as f:
            print(f"{queryStr}", file=f)
            if isinstance(rows, list):
                for row in rows:
                    print("|".join(map(str, row)), file=f)
                print("\n\n\n\n", file=f)
            else:
                if rows != -1:
                    print(f"{rows} rows affected.", file=f)
                    print("\n\n\n\n", file=f)
