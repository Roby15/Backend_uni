package com.example.demo.Program.main;

public class main {
    public main() {
    }
    public String generateMainFile() {
    return """
        #include <fstream>
        #include <string>
        #define NR_TEST_CASES 10
        using namespace std;

        void call_func(ifstream& in, ofstream& out);

        int main() {
            for (int t = 0; t < NR_TEST_CASES; t++) {
                ifstream in("input" + to_string(t) + ".txt");
                ofstream out("output" + to_string(t) + ".txt");

                call_func(in, out);

                //in.close();
                //out.close();
            }
            return 0;
        }
        """;
}
}
