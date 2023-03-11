package com.napier.sem;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class AppTest
{
    static App app;

    @BeforeAll
    static void init()
    {
        app = new App();
    }
    @Test
    void printSalariesTestEmpty()
    {
        ArrayList<Employee> employess = new ArrayList<Employee>();
        app.printSalaries(employess);
    }

   // @Test
    //void printSalariesTestNull()
   //{
    //    app.printSalaries(ArrayList<Employee> salariesTable);
   //}
   public void printSalaries(ArrayList<Employee> employees)
   {
       // Check employees is not null
       if (employees == null)
       {
           System.out.println("No employees");
           return;
       }
       // Print header
       System.out.println(String.format("%-10s %-15s %-20s %-8s", "Emp No", "First Name", "Last Name", "Salary"));
       // Loop over all employees in the list
       for (Employee emp : employees)
       {
           if (emp == null)
               continue;
           String emp_string =
                   String.format("%-10s %-15s %-20s %-8s",
                           emp.emp_no, emp.first_name, emp.last_name, emp.salary);
           System.out.println(emp_string);
       }
   }

    @Test
    void printSalaries()
    {
        ArrayList<Employee> employees = new ArrayList<Employee>();
        Employee emp = new Employee();
        emp.emp_no = 1;
        emp.first_name = "Emon";
        emp.last_name = "Ahmed";
        emp.title = "Engineer";
        emp.salary = 55000;
        employees.add(emp);
        app.printSalaries(employees);
    }
    @Test
    void displayEmployee()
    {
        ArrayList<Employee> employees = new ArrayList<Employee>();
        Employee emp= new Employee();
        emp.emp_no=1;
        emp.first_name="Emon";
        emp.last_name="Ahmed";
        emp.title="Engineer";
        emp.salary=90000;
        employees.add(emp);
        app.displayEmployee(emp);
    }
}