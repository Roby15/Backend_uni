package com.example.demo.Program.Call_Func;
public class Call_func {
    public Call_func() {
    }
    public String generateCallFuncFile() {
        return """
        #include <fstream>
        #include "user_function.h"
        using namespace std;

        void call_func(ifstream& in, ofstream& out) {
            int x, y;
            in >> x >> y;
            int res = add_nums(x, y);
            out << res;
        }
        """;
    }
}


