package com.example.networkcalling.profile;

import android.content.Context;

import com.example.networkcalling.model.Employee;
import com.example.networkcalling.model.EmployeeResponse;
import com.example.networkcalling.network.AppApiClient;
import com.example.networkcalling.repository.all_employees.AllEmployeesRepository;
import com.example.networkcalling.repository.all_employees.IAllEmployeesRepository;
import com.example.networkcalling.repository.our_company_employees.IOurCompanyRepository;
import com.example.networkcalling.repository.our_company_employees.OurCompanyRepository;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfilePresenter implements ProfileContract.Presenter {
    private AppApiClient appApiClient = AppApiClient.get();
    private IAllEmployeesRepository allWorkersRepository;
    private IOurCompanyRepository ourCompanyRepository;
    private ProfileContract.View view;

    private Employee obtainedEmployee;

    public ProfilePresenter(ProfileContract.View view, Context context) {
        this.view = view;
        allWorkersRepository = new AllEmployeesRepository(context);
        ourCompanyRepository = new OurCompanyRepository(context);
    }

    @Override
    public void getEmployeeDataFromDatabase(long id) {
        Employee employee = allWorkersRepository.getEmployee(id);
        if (employee == null) {
            view.showMessage("Пользователь не найден. ID = " + id);
        } else {
            view.showEmployeeProfile(employee);
        }
    }

    @Override
    public void getEmployeeData(long id) {

        appApiClient.getEmployee(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<EmployeeResponse>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull EmployeeResponse employeeResponse) {
                        if (employeeResponse != null) {
                            obtainedEmployee = employeeResponse.getEmployee();
                            view.showEmployeeProfile(obtainedEmployee);
                        } else {
                            view.showMessage("Ошибка! Пользователь не найден. ID = " + id);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                });

        /*// Асинхронный способ
        appApiClient.getEmployee(id).enqueue(new Callback<EmployeeResponse>() {
            @Override
            public void onResponse(Call<EmployeeResponse> call, Response<EmployeeResponse> response) {
                EmployeeResponse employeeResponse = response.body();
                if (employeeResponse != null) {
                    obtainedEmployee = employeeResponse.getEmployee();
                    view.showEmployeeProfile(obtainedEmployee);
                } else {
                    view.showMessage("Ошибка! Пользователь не найден. ID = " + id);
                }
            }

            @Override
            public void onFailure(Call<EmployeeResponse> call, Throwable t) {
                view.showMessage(t.getMessage());
            }
        });*/

        /*// Синхронный
        try {
            Response<EmployeeResponse> response = appApiClient.getEmployee(id).execute();
            EmployeeResponse employeeResponse = response.body();
            showEmployeeProfile(employeeResponse.getEmployee());
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }


    @Override
    public void addToCompanyEmployee() {
        if (obtainedEmployee != null) {
            ourCompanyRepository.insertEmployee(obtainedEmployee);
            view.showSuccessfulAddedToCompany();
            view.showButtonsState(true);
        }
    }

    @Override
    public void deleteFromCompanyEmployee() {
        if (obtainedEmployee != null) {
            ourCompanyRepository.deleteConcreteEmployee(obtainedEmployee.getId());
            view.showSuccessfulDeletedFromCompany();
            view.showButtonsState(false);
        }
    }

    @Override
    public void checkUserExistInOurCompany(Employee employee) {
        Employee employeeFromDatabase = ourCompanyRepository.getEmployee(employee.getId());
        boolean isExistUserInOurCompany = employeeFromDatabase != null;
        view.showButtonsState(isExistUserInOurCompany);
    }


}
