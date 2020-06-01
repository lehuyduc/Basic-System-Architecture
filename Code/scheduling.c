#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>

#define maxn 1000000
#define inf 1000000007

struct Data {
    int id;
    float x, y;
};

struct Data Data(int id,float x) {
    struct Data d;
    d.id = id;
    d.x = x;
    d.y = 0;
    return d;
};

struct Data Data2(int id,float x, float y) {
    struct Data d;
    d.id = id;
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
        if (r==0 || tree[r] <= key) break; // if c is root, or priority(parent(c)) is > priority(c)
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
        if (c < size && tree[c] > tree[c+1]) c = c + 1;
        if (c > size || tree[c] >= key) break;
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
    tree[size] = val;
    datas[size] = data;
    upHeap(heap, size);
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

float topData(struct Heap* heap, struct Data *data) {
    *data = heap->datas[1];
    return heap->tree[1];
}

bool empty(struct Heap *heap) {
    return heap->size == 0;
}

void clear(struct Heap *heap) {
    while (!empty(heap)) pop(heap);
}


// END OF HEAP

int n;
float a[maxn], b[maxn]; // arrival, waiting

void fcfs(int n, float a[], float b[]) {
    int i,j;
    float *turning = malloc(sizeof(float)*(n+1));
    float current = 0;

    for (i=0; i<n; i++) {
        if (current < a[i]) current = a[i];
        current = current + b[i];
        turning[i] = current - a[i];
    }

    float waitTime = 0, turnTime = 0;
    for (int i=0; i<n; i++) {
        turnTime += turning[i];
        waitTime += turning[i] - b[i];
    }
    printf("FCFS turning: "); for (i=0;i<n;i++) printf("%f ",turning[i]);
    printf("\n");
    printf("FCFS: waiting = %f, turning = %f\n\n", waitTime/n, turnTime/n);
}

//---------------------         SHORTEST TIME FIRST
void stf(int n, float a[], float b[]) {
    int i, j;
    struct Data pid;
    float *turning; // amount of time each process had to wait
    float current, shortest;
    struct Heap *heap;

    // for each object in heap, key is remaining time, value is the process id
    heap = makeHeap(2*n);
    turning = malloc(sizeof(float) * (n+1));
    for (i=0; i<n; i++) turning[i] = 0;
    current = 0;

    // for stf(), data.x and data.y are not used
    i = 0;
    while (i < n || !empty(heap)) {
        while (i < n && a[i] <= current) {
            insert(heap, b[i], Data(i, 0));
            i = i + 1;
        }

        if (empty(heap)) {
            if (current < a[i]) current = a[i];
            current = current + b[i];
            turning[i] = b[i];
            i++;
        }
        else {
            shortest = topData(heap, &pid);
            pop(heap);
            current += shortest;
            turning[pid.id] = current - a[pid.id];
        }
    }

    float waitTime = 0, turnTime = 0;
    for (int i=0; i<n; i++) {
        turnTime += turning[i];
        waitTime += turning[i] - b[i];
    }
    printf("SJF turning: "); for (i=0;i<n;i++) printf("%f ",turning[i]);
    printf("\n");
    printf("SJF: waiting = %f, turning = %f\n\n", waitTime/n, turnTime/n);
}

//---------------------         SHORTEST REMAINING TIME FIRST
void srtf(int n, float a[], float b[]) {
    int i, j;
    struct Data data, proc;
    float *turning; // amount of time each process had to wait
    float last, shortest;
    struct Heap *heap;

    // for each object, key is remaining time, value is the process id
    heap = makeHeap(n);
    turning = malloc(sizeof(float) * (n+1));
    for (i=0; i<n; i++) turning[i] = 0;

    // add a process at infinity time to make it easier to process
    // in srtf(), data.x is the moment the process is pushed into the heap
    // heap contains the shortest remaining time
    a[n] = inf;
    b[n] = 0;

    last = 0;
    for (i=0; i<=n; i++) {
        if (empty(heap)) {
            insert(heap, b[i], Data(i, 0));
        }
        else {
            // if (i==n) -> a[i] == inf -> all remaining processes will be processed
            while (!empty(heap) && last < a[i]) {    // while there's waiting process and time to spent before this process appear
                shortest = topData(heap, &proc);        // get process with least remaining time
                pop(heap);

                if (a[i] - last >= shortest) {       //if enough time since the last event to finish this process
                    turning[proc.id] = last + shortest - a[proc.id];
                    last = last + shortest;
                }
                else {
                    insert(heap, shortest - (a[i] - last), Data(proc.id, 0)); // spend the available time, and push that process back into heap
                    last = a[i];
                }
            }


            insert(heap, b[i], Data(i, 0));
        }
        last = a[i];
    }

    float waitTime = 0, turnTime = 0;
    for (int i=0; i<n; i++) {
        turnTime += turning[i];
        waitTime += turning[i] - b[i];
    }
    printf("SRTF turning: "); for (i=0;i<n;i++) printf("%f ",turning[i]);
    printf("\n");
    printf("SRTF: waiting = %f, turning = %f\n\n", waitTime/n, turnTime/n);
}


//---------------------         ROUND ROBIN
int main()
{
    setbuf(stdout, NULL);
    freopen("scheduling.inp","r",stdin);
 //   freopen("scheduling.out","w",stdout);
    int i,j,k;

    // number of processes
    // each line are 2 number: arrival time + waiting time
    scanf("%d",&n);
    for (i=0;i<n;i++) scanf("%f%f",&a[i],&b[i]);

    fcfs(n,a,b);
    stf(n,a,b);
    srtf(n,a,b);
    return 0;
}
