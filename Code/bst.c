#include <stdlib.h>
#include <stdbool.h>
#include <stdio.h>

const void *nullptr = NULL;

struct Node {
    double key, value;
    struct Node *parent, *left, *right;
};

struct Bst {
    struct Node *root;
};

struct Bst* makeTree() {
    struct Bst* tree = malloc(sizeof(struct Bst));
    tree->root = nullptr;
    return tree;
};

struct Node* Node(double key, double value) {
    struct Node* res = malloc(sizeof(struct Node));
    res->left = nullptr;
    res->right = nullptr;
    res->key = key;
    res->value = value;
    return res;
};

void copyVal(struct Node* target, struct Node* source) {
    target->key = source->key;
    target->value = source->value;
}
/*
void copy(struct Node* target, struct Node* source) {

    target->parent = source->parent;
    target->left = source->left;
    target->right = source->right;
}
*/

struct Node* minimum(struct Node* x) {
    if (x==nullptr) printf("Min: nullptr access");
    while (x->left!=nullptr) x = x->left;
    return x;
};

struct Node* maximum(struct Node* x) {
    if (x==nullptr) printf("Max: nullptr access");
    while (x->right!=nullptr) x = x->right;
    return x;
};

struct Node* pred(struct Node* x) {
    if (x==nullptr) printf("Pred: nullptr access");
    if (x->left != nullptr)
        return maximum(x->left);

    struct Node* parent;
    while (true) {
        parent = x->parent;
        if (parent==nullptr || parent->right==x) return parent;
        x = parent;
    }

    printf("Pred: can't reach here");
    return nullptr;
};

struct Node* next(struct Node* x) {
    if (x==nullptr) printf("Next: nullptr access");
    if (x->right != nullptr)
        return minimum(x->right);

    struct Node* parent;
    while (true) {
        parent = x->parent;
        if (parent==nullptr || parent->left==x) return parent;
        x = parent;
    }

    printf("Next: can't reach here");
    return nullptr;
};

struct Node* search(struct Node* x, double key) {
    while (x!=nullptr && x->key!=key) {
        if (key < x->key) x = x->left;
        else x = x->right;
    }
    return x;
};

void setLink(struct Node* parent, struct Node* child, bool leftChild) {
    if (child!=nullptr) child->parent = parent;
    if (leftChild) parent->left = child;
    else parent->right = child;
}


void insert(struct Bst *tree, double key, double value) {
    struct Node* newNode = Node(key, value); //malloc(sizeof(struct Node));

    if (tree->root==nullptr) {
        tree->root = newNode;
        return;
    }

    struct Node *x = tree->root, *y = nullptr;

    while (x!=nullptr) {
        y = x;
        if (key < x->key) x = x->left;
        else if (key > x->key) x = x->right;
        else {
            x->value = value;
            return;
        }
    }

    setLink(y, newNode, key < y->key);
}

void deleter(struct Bst *tree, struct Node* x) {
    if (x==nullptr) printf("Deleter: nullptr access");

    struct Node *y, *z;
    if (x->left!=nullptr && x->right!=nullptr) {
        printf("deleter: %f %f\n",x->key,x->value);
        y = maximum(x->left);
        copyVal(x, y);
        x = y;
    }


    if (x->left!=nullptr) y = x->left;
    else y = x->right;

    if (x==tree->root) {
        tree->root = y;
    }
    else {
        z = x->parent;
    printf("deleter z: %f %f\n",z->key,z->value);
        setLink(z, y, z->left==x);
    }
    printf("deleter x: %f %f\n",x->key,x->value);
    free(x);
}

void pop(struct Bst *tree) {
    if (tree==nullptr) printf("Pop: nullptr");
    deleter(tree, minimum(tree->root));
}

float top(struct Bst *tree, float *value) {
    if (tree==nullptr) printf("top: nullptr");
    struct Node *mini = minimum(tree->root);
    *value = mini->value;
    return mini->key;
}

bool empty(struct Bst* tree) {
    return tree->root == nullptr;
}

void preorder(struct Node* x) {
    if (x==nullptr) return;
    printf("%f \n",x->key);
    preorder(x->left);
    preorder(x->right);
}

void inorder(struct Node* x) {
    if (x==nullptr) return;
    inorder(x->left);
    printf("%f \n",x->key);
    inorder(x->right);
}

void postorder(struct Node* x) {
    if (x==nullptr) return;
    postorder(x->left);
    postorder(x->right);
    printf("%f \n",x->key);
}

int n;
int a[100002];
struct Bst *tree;

int main()
{
    int i;
    freopen("bst.inp","r",stdin);
    //freopen("bst.out","w",stdout);

    scanf("%d", &n);
    for (i=1;i<=n;i++) scanf("%d",&a[i]);

    tree = makeTree();
    for (i=1;i<=n;i++) insert(tree, (double)a[i], i);
    preorder(tree->root);
    printf("\n\n");

    insert(tree, 12, 8);
    deleter(tree, tree->root);
    inorder(tree->root);
    return 0;
}
