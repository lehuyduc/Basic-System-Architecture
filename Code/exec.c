#include <unistd.h>
#include <stdio.h>

void parseArgs(char *s, char *args[]) {
    int i, n, nargs;
    char *arg;

    n = strlen(s);
    nargs = 0;
    for (i=0; i<n;i++) {
        arg = "";
        while (i<n && s[i]!=' ') {
            arg = strncat(arg, &s[i], 1);
            i++;
        }

        nargs++;
        strcpy(args[nargs], arg);
    }
}

int main()
{
    char *args[100];
    char *s[100];

    while (true) {
        scanf("%s\n",s);
        if (strcmp(s, "quit")) {
            printf("User quit\n");
            return 0;
        }

        parseArgs(s, args);
        execvp(args[0], args);
    }

	return 0;
}

