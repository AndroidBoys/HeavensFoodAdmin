package androidboys.com.heavensfoodadmin.Models;

import java.io.Serializable;

public class User {

    public String name;
    public String email;
    public String phoneNumber;
    public Address userAddress;
    public String password;
    public Plan subscribedPlan;
    public Wallet wallet;
    public Absence absence;

    public User(String name, String email, String phoneNumber, Address userAddress, String password, Plan subscribedPlan, Wallet wallet, Absence absence) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.userAddress = userAddress;
        this.password = password;
        this.subscribedPlan = subscribedPlan;
        this.wallet = wallet;
        this.absence = absence;
    }

    public Absence getAbsence() {
        return absence;
    }

    public void setAbsence(Absence absence) {
        this.absence = absence;
    }

    public Address getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(Address userAddress) {
        this.userAddress = userAddress;
    }

    public User(String name, String email, String phone, Serializable address, String pass, Serializable plan) {
    }

    public String getName() {
        return name;
    }



    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Plan getSubscribedPlan() {
        return subscribedPlan;
    }

    public void setSubscribedPlan(Plan subscribedPlan) {
        this.subscribedPlan = subscribedPlan;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public User() {
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }
}
