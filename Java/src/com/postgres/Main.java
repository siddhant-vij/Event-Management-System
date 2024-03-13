package com.postgres;

import com.postgres.services.CreateTables;
import com.postgres.services.InsertData;
import com.postgres.services.SqlQueries;

public class Main {
  public static void main(String[] args) {
    // bash: psql --username=postgres
    // bash: Enter your password
    // ------------------------------------
    // psql: CREATE DATABASE eventmgmt;
    if (args.length == 0) {
      System.out.println(
          "No arguments provided. Please use:\n--c (for create tables)\n--i (for insert data)\n--q (for sql queries)");
    } else if (args.length == 1) {
      if (args[0].equals("--c")) {
        new CreateTables();
      } else if (args[0].equals("--i")) {
        new InsertData();
      } else if (args[0].equals("--q")) {
        new SqlQueries();
      } else {
        System.out.println("Invalid argument.");
      }
    } else {
      System.out.println("Invalid number of arguments.");
    }
    // psql: \c postgres;
    // psql: DROP DATABASE eventmgmt;
  }
}
