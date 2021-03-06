package com.example.networkcalling.our_company;

import com.example.networkcalling.model.Employee;

import java.util.List;

public interface OurCompanyContract {

    interface View {
        void showEmployees(List<Employee> employees);

        void showMessage(String message);
    }

    interface Presenter {

        void getOurCompanyInfo();

        void editEmployee(Employee employee);

        void addEmployee(Employee employee);

        void deleteFromOurCompanyEmployee(long id);
    }

}
