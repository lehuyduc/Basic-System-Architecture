#include <stdio.h>
#include <stdlib.h>
#include <string.h>

//#include "aes.h"

typedef unsigned char BYTE;

extern void aes_encrypt(BYTE* cipher, BYTE* message, BYTE* key);
extern void aes_decrypt(BYTE* message, BYTE* cipher, BYTE* key);

void display(BYTE* state, char *label);
void padding(BYTE* padded, BYTE* input);

int main(int argc, char* argv[])
{
    BYTE ciphertext_block[16];
    BYTE key[16] = {0x2B, 0x7E, 0x15, 0x16, 0x28, 0xAE, 0xD2, 0xA6, 0xAB, 0xF7, 0x15, 0x88, 0x09, 0xCF, 0x4F, 0x3C};
    BYTE plaintext_block[16];

    char name_ciphertext[100];
    char name_plaintext[100];

    FILE* pfd_ciphertext = NULL;
    FILE* pfd_plaintext = NULL;

    unsigned int nb_byte;


    if (argc != 3)
    {
    printf("Usage: %s plaintext_file ciphertext_file\n", argv[0]);
    exit(-1);
    }

    sprintf(name_plaintext, "%s", argv[1]);
    sprintf(name_ciphertext, "%s", argv[2]);

    printf("name_plaintext : %s\n", name_plaintext);
    printf("name_ciphertext : %s\n", name_ciphertext);

    if ((pfd_plaintext = fopen(name_plaintext, "r")) == NULL)
    {
    printf("ERROR: can not open file \"%s\" for reading\n", name_plaintext);
    exit(-1);
    }

    if ((pfd_ciphertext = fopen(name_ciphertext, "w")) == NULL)
    {
    printf("ERROR: can not open file \"%s\" for writing\n", name_ciphertext);
    exit(-1);
    }

  // complete here

    char buffer[16];
    int bytes_read = 0;
    char message[16];
    char cipher;
    while (bytes_read = fread(buffer, 1, sizeof(buffer), pdf_plaintext) == 16) {
        strcpy(message, buffer);
        aes_encrypt(cipher, message, key);
        fwrite(cipher, sizeof(cipher), 1, pfd_ciphertext);
    }

    if (nb_byte > 0) {
        memset(message + bytes_read, (16-bytes_read), 16 - bytes_read);
        aes_encrypt(cipher, message, key);
        fwrite(cipher, 16, 1, pfd_ciphertext);
    }

  fclose(pfd_plaintext);
  fclose(pfd_ciphertext);

  return 0;
}

void padding(BYTE* padded, BYTE* input) {

}
