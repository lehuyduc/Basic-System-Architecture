#include <stdio.h>

struct A {
    int x;
};

struct B {
    int y;
};

void f(struct A* a) {
    printf("%d",a->x);
}

int main() {
    struct A a;
    struct B b;
    a.x = 5;
    b.y = 10;
    f(&b);
}
