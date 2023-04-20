package com.napier.sem;

import java.sql.*;
import java.util.ArrayList;

public class App {
    /**
     * Connection to MySQL database.
     */
    private Connection con = null;

    /**
     * Connect to the MySQL database.
     */
    public void connect() {
        try {
            // Load Database driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 10;
        for (int i = 0; i < retries; ++i) {
            System.out.println("Connecting to database...");
            try {
                // Wait a bit for db to start
                Thread.sleep(30000);
                // Connect to database
                con = DriverManager.getConnection("jdbc:mysql://db:3306/employees?useSSL=false", "root", "example");
                System.out.println("Successfully connected");
                break;
            } catch (SQLException sqle) {
                System.out.println("Failed to connect to database attempt " + Integer.toString(i));
                System.out.println(sqle.getMessage());
            } catch (InterruptedException ie) {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }

    /**
     * Disconnect from the MySQL database.
     */
    public void disconnect() {
        if (con != null) {
            try {
                // Close connection
                con.close();
            } catch (Exception e) {
                System.out.println("Error closing connection to database");
            }
        }
    }

    /**
     * Gets all the current employees and salaries.
     *
     * @return A list of all employees and salaries, or null if there is an error.
     */
    public ArrayList<Employee> getAllSalaries() {
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            //!!!GETTING DUPLICATE COLUMNS when I add the titles table
            String strSelect =
                    "SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary, titles.title "
                            + "FROM employees, salaries, titles "
                            + "WHERE employees.emp_no = salaries.emp_no AND salaries.to_date = '9999-01-01' "
                            + "AND employees.emp_no = titles.emp_no "
                            + "ORDER BY employees.emp_no ASC";
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Extract employee information
            ArrayList<Employee> employees = new ArrayList<Employee>();
            while (rset.next()) {
                Employee emp = new Employee();
                //variable to store emp_no for the manager
//                int managerID = rset.getInt("dept_manager.emp_no");
                emp.emp_no = rset.getInt("employees.emp_no");
                emp.first_name = rset.getString("employees.first_name");
                emp.last_name = rset.getString("employees.last_name");
                emp.salary = rset.getInt("salaries.salary");
                emp.title = rset.getString("titles.title");
//                emp.manager = getEmployee(managerID);
//                emp.dept = getDepartment(rset.getString("departments.dept_name"));
                employees.add(emp);
            }

            return employees;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get salary details");
            return null;
        }
    }

    public void printSalaries(ArrayList<Employee> salariesTable) {
        //this will format and print every Employee object in the salariesTable as a string
        System.out.println(String.format("%-10s %-15s %-20s %-8s", "Emp No", "First Name", "Last Name", "Salary"));
        // Loop over all employees in the list
        for (Employee emp : salariesTable) {
            String emp_string =
                    String.format("%-10s %-15s %-20s %-8s",
                            emp.emp_no, emp.first_name, emp.last_name, emp.salary);
            System.out.println(emp_string);
        }
    }


    //function to get employee's department name base on their ID
    public String getEmployeeDepartmentName(int emp_no){
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            //the query should find the department of the employee
            //and find the name of that department

            String strSelect =
                    "SELECT departments.dept_name "
                            + "FROM employees, dept_emp, departments "
                            + "WHERE employees.emp_no = " + emp_no + " " +
                            "AND employees.emp_no = dept_emp.emp_no AND dept_emp.dept_no = departments.dept_no ";
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Return new employee if valid.
            // Check one is returned
            if (rset.next()) {
                String deptName;
                //get the manager id from the response
                deptName = rset.getString("departments.dept_name");


                return deptName;
            } else
                return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get Department details");
            return null;
        }
    }

    //function to get employee manager based on their id
    public int getEmployeeManagerID(int emp_no){

        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            //the query should find the department of the employee
            //find the manager of that department
            //return the manager's emp_no

            String strSelect =
                    "SELECT dept_manager.emp_no as 'managerID' "
                            + "FROM employees, dept_emp, departments, dept_manager "
                            + "WHERE employees.emp_no = " + emp_no + " " +
                            "AND employees.emp_no = dept_emp.emp_no AND dept_emp.dept_no = departments.dept_no " +
                            "AND departments.dept_no = dept_manager.dept_no" ;
//                            + "AND employees.emp_no = dept_manager.emp_no AND dept_manager.dept_no = departments.dept_no";
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Return new employee if valid.
            // Check one is returned
            if (rset.next()) {
                int managerID;
                //get the manager id from the response
                managerID = rset.getInt("managerID");


                return managerID;
            } else
                return -1;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get manager details");
            return -1;
        }

    };

    public Employee getEmployee(int ID) {
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement

//            String strSelect =
//                    "SELECT emp_no, first_name, last_name "
//                            + "FROM employees "
//                            + "WHERE emp_no = " + ID;

            String strSelect =
                    "SELECT employees.emp_no, first_name, last_name, titles.title as 'title', salaries.salary as 'salary' "
                            + "FROM employees, salaries, titles "
                            + "WHERE employees.emp_no = " + ID + " " +
                            "AND employees.emp_no = salaries.emp_no AND employees.emp_no = titles.emp_no";
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Return new employee if valid.
            // Check one is returned
            if (rset.next()) {
                Employee emp = new Employee();
                int empID = rset.getInt("employees.emp_no");
                //variable to store the department name
                String deptName = getEmployeeDepartmentName(empID);
                //variable to store managerID
                int managerID = getEmployeeManagerID(empID);

                emp.emp_no = rset.getInt("emp_no");
                emp.first_name = rset.getString("first_name");
                emp.last_name = rset.getString("last_name");
                emp.title = rset.getString("title");
                emp.salary = rset.getInt("salary");

                emp.dept = getDepartment(deptName);
//                emp.manager = getEmployeeManagerID(managerID);

                return emp;
            } else
                return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get employee details");
            return null;
        }
    }

    public void displayEmployee(Employee emp) {
        if (emp != null) {
            System.out.println(
                    emp.emp_no + " "
                            + emp.first_name + " "
                            + emp.last_name + "\n"
                            + emp.title + "\n"
                            + "Salary:" + emp.salary + "\n"
//                            + emp.dept.dept_name + "\n"
//                            + "Manager: " + emp.manager.first_name +" "+emp.manager.last_name + "\n"
            );
        }
    }
    public void addEmployee(Employee emp)
    {
        try
        {
            Statement stmt = con.createStatement();
            String strUpdate =
                    "INSERT INTO employees (emp_no, first_name, last_name, birth_date, gender, hire_date) " +
                            "VALUES (" + emp.emp_no + ", '" + emp.first_name + "', '" + emp.last_name + "', " +
                            "'9999-01-01', 'M', '9999-01-01')";
            stmt.execute(strUpdate);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to add employee");
        }
    }

    public ArrayList<Employee> getSalariesByTitle(String title) {
        try {
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
                            "AND titles.title = '" + title + "' \n" +
                            "ORDER BY employees.emp_no ASC";

            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Return new employee if valid.
            // Check one is returned
            //array to store the employee information
            ArrayList<Employee> employees = new ArrayList<Employee>();
            while (rset.next()) {

                Employee emp = new Employee();
                emp.emp_no = rset.getInt("emp_no");
                emp.first_name = rset.getString("first_name");
                emp.last_name = rset.getString("last_name");
//                emp.title = rset.getString("title");
                emp.salary = rset.getInt("salary");

                //add the employee object to the array
                employees.add(emp);
            }
            return employees;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get employee details");
            return null;
        }
    }

    //add functions to get the department by name
    //and get the salaries by department
    public Department getDepartment(String dept_name){
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            //need to select all the properties that a department object requires
            //will need to use the getEmployee function
            //join the dept_manager table
            //join the employees table
            //select dept_no, dept_name
            //select the emp_no to be used in the getEmployee function

            String strSelect =
                    "SELECT departments.dept_no, departments.dept_name, employees.emp_no "
                            + "FROM departments, dept_manager, employees "
                            + "WHERE departments.dept_no = dept_manager.dept_no "
                            + "AND dept_manager.emp_no = employees.emp_no "
                            + "AND departments.dept_name = '"+dept_name+"' ";
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Return new employee if valid.
            // Check one is returned
            if (rset.next()) {
                Department dept = new Department();
                //store the emp_no int in a variable
                int employeeID = rset.getInt("emp_no");
                //have problem setting the manager without recursively calling
                //getEmployee function like : getEmp -> getMan -> getEmp .....
                Employee manager;
                dept.dept_no = rset.getString("dept_no");
                dept.dept_name =  rset.getString("dept_name");
//                dept.manager = manager;

                return dept;
            } else
                return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get department details");
            return null;
        }
    }

    public void displayDepartment(Department dept){
        if (dept != null) {
            System.out.println(
                    "Name: " + dept.dept_name + "\n"
                            +"ID: " + dept.dept_no + "\n"
                            + "Manager: \n"
            );
            //call the displayEmployee function to see the manager information
            displayEmployee(dept.manager);
        }
    }

    public ArrayList<Employee> getSalariesByDepartment(Department dept){
        //need to extract the department id from the dept object
        //use the id to select the matching employees
        //need to merge the departments, dept_emp and employee tables
        //it should update an arraylist of type Employee
        try {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement

            String strSelect =
                    "SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary " +
                            "FROM employees, salaries, dept_emp, departments " +
                            "WHERE employees.emp_no = salaries.emp_no " +
                            "AND employees.emp_no = dept_emp.emp_no " +
                            "AND dept_emp.dept_no = departments.dept_no " +
                            "AND salaries.to_date = '9999-01-01' " +
                            "AND departments.dept_no = '" + dept.dept_no + "' "+
                            "ORDER BY employees.emp_no ASC";

            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Return new employee if valid.
            // Check one is returned

            //array to store the employee information
            ArrayList<Employee> employees = new ArrayList<Employee>();
            while (rset.next()) {

                Employee emp = new Employee();
                emp.emp_no = rset.getInt("emp_no");
                emp.first_name = rset.getString("first_name");
                emp.last_name = rset.getString("last_name");
//                emp.title = rset.getString("title");
                emp.salary = rset.getInt("salary");

                //add the employee object to the array
                employees.add(emp);
            }
            return employees;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get employee details");
            return null;
        }

    }
    public void connect(String location, int delay) {
        try {
            // Load Database driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 10;
        for (int i = 0; i < retries; ++i) {
            System.out.println("Connecting to database...");
            try {
                // Wait a bit for db to start
                Thread.sleep(delay);
                // Connect to database
                con = DriverManager.getConnection("jdbc:mysql://" + location
                                + "/employees?allowPublicKeyRetrieval=true&useSSL=false",
                        "root", "example");
                System.out.println("Successfully connected");
                break;
            } catch (SQLException sqle) {
                System.out.println("Failed to connect to database attempt " +                                  Integer.toString(i));
                System.out.println(sqle.getMessage());
            } catch (InterruptedException ie) {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }

    public static void main(String[] args) {
        // Create new Application and connect to database
        App a = new App();

        if(args.length < 1){
            a.connect("localhost:33060", 30000);
        }else{
            a.connect(args[0], Integer.parseInt(args[1]));
        }

        Department dept = a.getDepartment("Development");
        ArrayList<Employee> employees = a.getSalariesByDepartment(dept);


        // Print salary report
        a.printSalaries(employees);

        // Disconnect from database
        a.disconnect();
    }

}