package com.example.tales.tcc.db;

import android.content.Context;
import android.util.Log;

import com.example.tales.tcc.Constants;

import java.util.ArrayList;

/**
 * Created by tales on 19/06/2017.
 */

public class UserModel {
    private String name, login, password, type, parent;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public UserModel(String name, String login, String password, String type, String parent) {
        this.name = name;
        this.login = login;
        this.password = password;
        this.type = type;
        this.parent = parent;
    }

    public void insertUser(Context context) {
        UserDatasource userDatasource = new UserDatasource(context);
        userDatasource.open();
        userDatasource.insert(this);
        userDatasource.close();
    }

    public static ArrayList<UserModel> getUserByLogin(Context context, String login) {
        ArrayList<UserModel> users;
        UserDatasource userDatasource = new UserDatasource(context);
        userDatasource.open();
        users = userDatasource.getLocations(Constants.login + "=?", new String[]{login});
        userDatasource.close();
        return users;
    }

    public static ArrayList<UserModel> getUser(Context context) {
        ArrayList<UserModel> users;
        UserDatasource userDatasource = new UserDatasource(context);
        userDatasource.open();
        users = userDatasource.getLocations(null, null);
        userDatasource.close();
        return users;
    }

    public static void deleteUsers(Context context) {
        int users;
        UserDatasource userDatasource = new UserDatasource(context);
        userDatasource.open();
        users = userDatasource.delete(null, null);
        userDatasource.close();
        Log.d("UserModel", users + " users deleted");
    }
}
