package com.napier.sem;

import java.sql.*;

public class App
{
    /**
     * Connection to MySQL database.
     */
    private Connection con = null;

    /**
     * Connect to the MySQL database.
     */
    public void connect()
    {
        try
        {
            // Load Database driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 10;
        for (int i = 0; i < retries; ++i)
        {
            System.out.println("Connecting to database...");
            try
            {
                // Wait a bit for db to start
                Thread.sleep(30000);
                // Connect to database
                con = DriverManager.getConnection("jdbc:mysql://db:3306/employees?useSSL=false", "root", "example");
                System.out.println("Successfully connected");
                break;
            }
            catch (SQLException sqle)
            {
                System.out.println("Failed to connect to database attempt " + Integer.toString(i));
                System.out.println(sqle.getMessage());
            }
            catch (InterruptedException ie)
            {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }

    /**
     * Disconnect from the MySQL database.
     */
    public void disconnect()
    {
        if (con != null)
        {
            try
            {
                // Close connection
                con.close();
            }
            catch (Exception e)
            {
                System.out.println("Error closing connection to database");
            }
        }
    }

    public Employee getEmployee(int ID)
    {
        try
        {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement

//            String strSelect =
//                    "SELECT emp_no, first_name, last_name "
//                            + "FROM employees "
//                            + "WHERE emp_no = " + ID;

            String strSelect =
                    "SELECT employees.emp_no, first_name, last_name, titles.title as 'title', salaries.salary as 'salary', departments.dept_name as 'dept_name' "
                            + ", (SELECT CONCAT(first_name ,' ',last_name)  FROM employees WHERE employees.emp_no = dept_manager.emp_no) as 'manager' "
                            + "FROM employees inner join titles on employees.emp_no = titles.emp_no "
                            + "INNER JOIN salaries on employees.emp_no = salaries.emp_no "
                            + "INNER JOIN dept_emp on employees.emp_no = dept_emp.emp_no "
                            + "INNER JOIN departments on dept_emp.dept_no = departments.dept_no "
                            + "INNER JOIN dept_manager on dept_emp.dept_no = dept_manager.dept_no "
                            + "WHERE employees.emp_no = " + ID;

            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Return new employee if valid.
            // Check one is returned
            if (rset.next())
            {
                System.out.println(rset);
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("emp_no");
                emp.first_name = rset.getString("first_name");
                emp.last_name = rset.getString("last_name");
                emp.title = rset.getString("title");
                emp.salary = rset.getInt("salary");
                emp.dept_name = rset.getString("dept_name");
                emp.manager = rset.getString("manager");

                return emp;
            }
            else
                return null;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get employee details");
            return null;
        }
    }

    public void displayEmployee(Employee emp)
    {
        if (emp != null)
        {
            System.out.println(
                    emp.emp_no + " "
                            + emp.first_name + " "
                            + emp.last_name + "\n"
                            + emp.title + "\n"
                            + "Salary:" + emp.salary + "\n"
                            + emp.dept_name + "\n"
                            + "Manager: " + emp.manager + "\n");
        }
    }
    public void getSalaryByRole(String role)
    {
        try
        {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement

            String strSelect =
                    "SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary\n" +
                            "FROM employees, salaries, titles\n" +
                            "WHERE employees.emp_no = salaries.emp_no\n" +
                            "AND employees.emp_no = titles.emp_no\n" +
                            "AND salaries.to_date = '9999-01-01'\n" +
                            "AND titles.to_date = '9999-01-01'\n" +
                            "AND titles.title = '"+ role +"' \n" +
                            "ORDER BY employees.emp_no ASC";

//            String strSelect =
//                    "SELECT employees.emp_no, first_name, last_name, titles.title as 'title', salaries.salary as 'salary', departments.dept_name as 'dept_name' "
//                            + ", (SELECT CONCAT(first_name ,' ',last_name)  FROM employees WHERE employees.emp_no = dept_manager.emp_no) as 'manager' "
//                            + "FROM employees inner join titles on employees.emp_no = titles.emp_no "
//                            + "INNER JOIN salaries on employees.emp_no = salaries.emp_no "
//                            + "INNER JOIN dept_emp on employees.emp_no = dept_emp.emp_no "
//                            + "INNER JOIN departments on dept_emp.dept_no = departments.dept_no "
//                            + "INNER JOIN dept_manager on dept_emp.dept_no = dept_manager.dept_no "
//                            + "WHERE employees.emp_no = " + ID;
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Return new employee if valid.
            // Check one is returned
            while (rset.next())
            {

                Employee emp = new Employee();
                emp.emp_no = rset.getInt("emp_no");
                emp.first_name = rset.getString("first_name");
                emp.last_name = rset.getString("last_name");
//                emp.title = rset.getString("title");
                emp.salary = rset.getInt("salary");
                System.out.println(
                        emp.emp_no + "  " +
                                emp.first_name + "  " +
                                emp.last_name + "  " +
                                emp.salary);



            }

        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get employee details");
        }
    }



    public static void main(String[] args)
    {
        // Create new Application
        App a = new App();

        // Connect to database
        a.connect();

        // Get Employee
        Employee emp = a.getEmployee(255530);
        // Display results
        a.displayEmployee(emp);
        a.getSalaryByRole("Engineer");

        // Disconnect from database
        a.disconnect();
    }
}