#include <iostream>
#include <bits/stdc++.h>
using namespace std;

int main()
{
    int a, b, res1, test;
    float c, d, res2;

    a = 5;
    b = 10;
    res1 = a + b;

    c = 5;
    d = 10;
    res2 = c + d;

    memcpy(&test, &res2, sizeof(float));
    cout << test;
    return 0;
}
