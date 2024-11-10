package com.example.demo.Program.user;
public class user {



    public user() {

}
    public String generateUserFunctionFile(String userFunction) {
        return """
        #include <iostream>
        #include "user_function.h"
        using namespace std;

        {{USER_FUNCTION}}
        """.replace("{{USER_FUNCTION}}", userFunction);
    }

    public String generateUserFunctionHeader(String userFunction) {
        return """
        #ifndef USER_FUNCTION_H
        #define USER_FUNCTION_H

                {{USER_FUNCTION}}
                                               

        #endif // USER_FUNCTION_H
        """.replace("{{USER_FUNCTION}}", userFunction);
    }}


