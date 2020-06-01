#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>

// std::priority_queue (stdpq) only allows us to update the min/max element
// this custom heap allows me to update any element of the heap at any time
// An example where this custom heap gives better performance than stdpq
// is Dijkstra algorithm:
//      + with stdpq, a vertex's value has to be pushed into the pq each time it is updated.
//        So the max size of the pq is ~~ O(M)
//      + however, with this heap, the size is exactly N. In dense graphs, it can give a noticeable performance boost

struct Data {
    int x, y;
};

struct Data Data(int x,int y) {
    struct Data d;
    d.x = x;
    d.y = y;
    return d;
};

struct Heap {
    int capacity;
    int size;
    float *tree;     // the heap binary tree
    struct Data *datas;
};

int parent(int node) {return node / 2;}

int leftc(int node) {return node * 2;}

int rightc(int node) {return node * 2 + 1;}

// our heap tree is from 1->capacity
struct Heap* makeHeap(int capacity) {
    struct Heap* heap = malloc(sizeof(struct Heap));
    heap->capacity = capacity;
    heap->size = 0;
    heap->tree = malloc(sizeof(int)*(capacity+1));
    heap->datas = malloc(sizeof(struct Data)*(capacity+1));

    for (int i=0; i<=capacity; i++) heap->tree[i] = 0;
    return heap;
}

void swapNode(struct Heap *heap, int a, int b) {
    float f;
    f = heap->tree[a];
    heap->tree[a] = heap->tree[b];
    heap->tree[b] = f;

    struct Data data;
    data = heap->datas[a];
    heap->datas[a] = heap->datas[b];
    heap->datas[b] = data;
}

void copy(struct Heap *heap, int a, int b) {
    heap->tree[a] = heap->tree[b];
    heap->datas[a] = heap->datas[b];
}

// update heap at node c
void upHeap(struct Heap *heap, int i) {
    float *tree = heap->tree;
    struct Data *datas = heap->datas;
    int c, r;

    c = i;
    float key = tree[c];
    struct Data value = datas[c];

    while (true) {
        r = parent(c);
        if (r==0 || tree[r] >= key) break; // if c is root, or priority(parent(c)) is > priority(c)
        copy(heap, c, r);
        c = r;
    }
    tree[c] = key;
    datas[c] = value;
}

// update heap after a value is decreased
void downHeap(struct Heap *heap, int i) {
    float *tree = heap->tree;
    struct Data *datas = heap->datas;
    int size = heap->size;
    int c, r;

    r = i;
    float key = tree[r];
    struct Data value = datas[r];

    while (true) {
        c = leftc(r);
        if (c < size && tree[c] < tree[c+1]) c = c + 1;
        if (c > size || tree[c] <= key) break;
        copy(heap, r, c);
        r = c;
    }
    tree[r] = key;
    datas[r] = value;
}

void insert(struct Heap *heap, float val, struct Data data) {
    float *tree = heap->tree;
    struct Data *datas = heap->datas;
    int c, r;

    int size;

    heap->size += 1;
    size = heap->size;
  //  printf("size = %d\n",size);
    tree[size] = val;
    datas[size] = data;
   // printf("Reached upheap\n");
    upHeap(heap, size);
    //printf("inserting %d %f %f\n",size, val, heap->p[size]);
}

void pop(struct Heap *heap) {
    float *tree = heap->tree;
    struct Data *datas = heap->datas;
    int size = heap->size;

    swapNode(heap, 1, heap->size);
    heap->size -= 1;
    downHeap(heap, 1);
}

float top(struct Heap *heap) {
    return heap->tree[1];
}

int topData(struct Heap* heap, struct Data *data) {
    *data = heap->datas[1];
    return heap->tree[1];
}

bool empty(struct Heap *heap) {
    return heap->size == 0;
}

void clear(struct Heap *heap) {
    while (!empty(heap)) pop(heap);
}

bool checkValid(struct Heap *heap) {
    int n = heap->size;
    float *tree = heap->tree;
    struct Data *datas = heap->datas;

    for (int i=1;i<=n;i++) {
        if (2*i > n) break;
        if (tree[i] < tree[2*i]) {
//                printf("invalid at %d %f %f\n",i,heap->p[i], heap->p[2*i]);
                return false;
        }
        if (tree[i] < tree[2*i]) {
  //              printf("invalidr at %d\n",i);
                return false;
        }
    }
    return true;
}

int n, Q;
int a[100002];
struct Heap *heap;

int rander() {
    return rand()%100000+1;
}

void makeInput() {
    for (int i=1;i<=n;i++) a[i] = rander();
}

int main()
{
    setbuf(stdout, NULL);
    int i, j, t, k;
    //freopen("heap.inp","r",stdin);
    //freopen("heap.out","w",stdout);

    printf("program start");
    int cap = 10000, n = 10000, test = 10000;
    int Q = 10000;
    int correct = 0;
    heap = makeHeap(cap);

    for (t=1;t<=test;t++) {
        makeInput();
        clear(heap);

      //  printf("hello0");
        for (i=1;i<=n;i++) insert(heap, a[i], Data(0,0));

      //  printf("hello");
        for (i=1;i<=Q;i++) {
            k = rand()%2;
            if (k==0) insert(heap, rander(), Data(0,0));
            else pop(heap);
        }

        if (!checkValid(heap)) {printf("false\n"); return 0;}
        else correct++;

        if (correct % 5 == 0) printf("%d/%d\n",correct,test);
    }

    return 0;
}
