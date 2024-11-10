package com.example.demo.Program.Call_Func;
public class Call_func {
    public Call_func() {
    }
    public String generateCallFuncFile(String userFunction) {
        return """
                #include <fstream>
                #include "user_function.h"
                using namespace std;

        {{USER_FUNCTION}}
        """.replace("{{USER_FUNCTION}}", userFunction);
    }
}


