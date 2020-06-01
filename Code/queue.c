#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>

struct Data {
    int id;
    float value;
};

struct Data Data(int x,int y) {
    struct Data d;
    d.id = x;
    d.value = y;
    return d;
};

struct Queue {
    int capacity;
    int size, first, last;
    struct Data *datas;
};

struct Queue makeQueue() {
    struct Queue *q = malloc(sizeof(struct Queue));

};

void push(struct Queue *q) {

}

int main()
{

}
