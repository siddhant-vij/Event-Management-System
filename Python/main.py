import sys

from services.create_tables import CreateTables
from services.insert_data import InsertData
from services.sql_queries import SqlQueries


def main(args):
    if len(args) == 0:
        print("No arguments provided. Please use:\n--c (for create tables)\n--i (for insert data)\n--q (for sql queries)")
    elif len(args) == 1:
        if args[0] == "--c":
            CreateTables()
        elif args[0] == "--i":
            InsertData()
        elif args[0] == "--q":
            SqlQueries()
        else:
            print("Invalid argument.")
    else:
        print("Invalid number of arguments.")


if __name__ == "__main__":
    # bash: psql --username=postgres
    # bash: Enter your password
    # ------------------------------------
    # psql: CREATE DATABASE eventmgmt;
    # psql: \c eventmgmt;

    main(sys.argv[1:])

    # psql: \c postgres;
    # psql: DROP DATABASE eventmgmt;
