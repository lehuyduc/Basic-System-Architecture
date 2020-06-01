#include<bits/stdc++.h>
#include <time.h>
#include <windows.h>

using namespace std;

#define RED 		12
#define D_ORANGE	228
#define L_ORANGE 	206
#define YELLOW 		14
#define GREEN 		10
#define BLUE 		9
#define MAGENTA		13
#define CYAN		11
#define WHITE		7

/*----------------------------------------------------------------------------------------------------------------------*/

struct Sched
{
	string name;		// Process
	float AT;			// Arrival Time (ms)
	float BT;			// Burst Time (ms)
};

Sched newProc(string s, float x, float y)
{
	Sched p;
	p.name = s;
	p.AT = x;
	p.BT = y;
	return p;
}

/*----------------------------------------------------------------------------------------------------------------------*/

int n;
int COORDx, COORDy;
float maxAT, minAT;
int maxBT, minBT;
float wFCFS, wSJF, wSRTF, wRR;
float tFCFS, tSJF, tSRTF, tRR;
string s;
float a, b;
Sched P[16];			// 16 Processes are queueing for executed

/*----------------------------------------------------------------------------------------------------------------------*/

void gotoxy(int x, int y)
{
  	static HANDLE h = NULL;
  	if(!h)
    	h = GetStdHandle(STD_OUTPUT_HANDLE);
  	COORD c = {x,y};
  	SetConsoleCursorPosition(h,c);
}

int wherex()
{
	CONSOLE_SCREEN_BUFFER_INFO csbi;
	if (!GetConsoleScreenBufferInfo(GetStdHandle(STD_OUTPUT_HANDLE),&csbi))
	return -1;
	return csbi.dwCursorPosition.X;
}

int wherey()
{
	CONSOLE_SCREEN_BUFFER_INFO csbi;
	if (!GetConsoleScreenBufferInfo(GetStdHandle(STD_OUTPUT_HANDLE),&csbi))
	return -1;
	return csbi.dwCursorPosition.Y;
}

/*----------------------------------------------------------------------------------------------------------------------*/

void getMaxAT(float &maxAT, int n)
{
	maxAT = P[0].AT;
	for (int i=1; i<n; ++i)
	{
		if (maxAT < P[i].AT) maxAT = P[i].AT;
	}
}

void getMinAT(float &minAT, int n)
{
	minAT = P[0].AT;
	for (int i=1; i<n; ++i)
	{
		if (minAT > P[i].AT) minAT = P[i].AT;
	}
}

void getMaxBT(int &maxBT, int n)
{
	maxBT = P[0].BT;
	for (int i=1; i<n; ++i)
	{
		if (maxBT < P[i].BT) maxBT = P[i].BT;
	}
}

void getMinBT(int &minBT, int n)
{
	minBT = P[0].BT;
	for (int i=1; i<n; ++i)
	{
		if (minBT > P[i].BT) minBT = P[i].BT;
	}
}

/*----------------------------------------------------------------------------------------------------------------------*/

void InfoTable(int n)
{
	SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), 7);
	gotoxy(0,0);

	cout << char(218);
	for (int i=0; i<9; ++i) cout << char(196);
	cout << char(194);
	for (int i=0; i<19; ++i) cout << char(196);
	cout << char(194);
	for (int i=0; i<17; ++i) cout << char(196);
	cout << char(191) << endl;

	cout << char(179) << " Process " << char(179) << " Arrival Time (ms) " << char(179) << " Burst Time (ms) " << char(179) << endl;
	cout << char(195);
	for (int i=0; i<9; ++i) cout << char(196);
	cout << char(197);
	for (int i=0; i<19; ++i) cout << char(196);
	cout << char(197);
	for (int i=0; i<17; ++i) cout << char(196);
	cout << char(180) << endl;

	for (int i=0; i<n; ++i)
	{
		gotoxy(0,3+i); cout << char(179);
		gotoxy(10,3+i); cout << char(179);
		gotoxy(30,3+i); cout << char(179);
		gotoxy(48,3+i); cout << char(179);
	}

	cout << endl << char(192);
	for (int i=0; i<9; ++i) cout << char(196);
	cout << char(193);
	for (int i=0; i<19; ++i) cout << char(196);
	cout << char(193);
	for (int i=0; i<17; ++i) cout << char(196);
	cout << char(217) << endl;
}

void ProcInfo(int n)
{
	for (int i=0; i<n; ++i)
	{
		gotoxy(4,3+i); cout << P[i].name;
		gotoxy(19,3+i); cout << fixed << setprecision(1) << P[i].AT;		// We can adjust the setprecision(n) for desire display
		gotoxy(39,3+i); cout << fixed << setprecision(0) << P[i].BT;		// We can adjust the setprecision(n) for desire display
	}
}

void ProcBTColor(int x)
{
	if (P[x].name=="P1") SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), RED);
		else if (P[x].name=="P2") SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), YELLOW);
			else if (P[x].name=="P3") SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), GREEN);
				else if (P[x].name=="P4") SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), BLUE);
}

void ProcVisualize(int n)
{
	int lb,rb;			// left bound, right bound
	int k;

	for (int i=0; i<n; ++i)
	{
		gotoxy(0,5+n+i);
		cout << P[i].name << endl;
	}

	for (int i=0; i<n; ++i)
	{
		gotoxy(4+maxBT/0.2+5,5+n+i);
		cout << "(" << P[i].BT << " ms)" << endl;
	}

	for (int i=0; i<n; ++i)
	{
		ProcBTColor(i);
		lb = 4 + P[i].AT / 0.2;
		gotoxy(lb,5+n+i);
		k = P[i].BT / 0.2;
		for (int j=0; j<k; ++j)
		{

			cout << char(178);
		}
	}

	SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), 7);
}

void DisplayAnswers(int x)
{
	SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), MAGENTA);
	gotoxy(0,x); 	cout << "- Question 2:" << endl;
	gotoxy(0,x+11); cout << "- Question 3:" << endl;

	SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), CYAN);
	gotoxy(0,x+2); 	cout << "  + FCFS: ";
	gotoxy(0,x+13);	cout << "  + FCFS: ";
	gotoxy(0,x+4); 	cout << "  + SJF: ";
	gotoxy(0,x+15);	cout << "  + SJF: ";
	gotoxy(0,x+6); 	cout << "  + SRTF: ";
	gotoxy(0,x+17);	cout << "  + SRTF: ";
	gotoxy(0,x+8); 	cout << "  + RR: ";
	gotoxy(0,x+19);	cout << "  + RR: ";

	SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), WHITE);
	gotoxy(0,x+1); 	cout << "Average waiting time:";
	gotoxy(0,x+12); cout << "Average turnaround time:";

	gotoxy(0,x+3); 	cout << "    W = " << fixed << setprecision(3) << wFCFS << " ms";
	gotoxy(0,x+14); cout << "    T = " << fixed << setprecision(3) << tFCFS << " ms";
	gotoxy(0,x+5); 	cout << "    W = " << fixed << setprecision(3) << wSJF << " ms";
	gotoxy(0,x+16); cout << "    T = " << fixed << setprecision(3) << tSJF << " ms";
	gotoxy(0,x+7); 	cout << "    W = " << fixed << setprecision(3) << wSRTF << " ms";
	gotoxy(0,x+18); cout << "    T = " << fixed << setprecision(3) << tSRTF << " ms";
	gotoxy(0,x+9); 	cout << "    W = " << fixed << setprecision(3) << wRR << " ms";
	gotoxy(0,x+20); cout << "    T = " << fixed << setprecision(3) << tRR << " ms";

}

/*----------------------------------------------------------------------------------------------------------------------*/

float FCFS()
{
	int k;
	float W[16];
	float w, aw; // Waiting time, Average Waiting time

	SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), 11);
	cout << "  + FCFS:" << endl;
	SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), 7);
	cout << "    Scheduled:   ";
	for (int i=0; i<n; ++i)
	{
		ProcBTColor(i);
		k = P[i].BT / 0.2;
		for (int j=0; j<k; ++j)
		{
			cout << char(178);
		}
	}

	W[0]=0.0;
	for (int k=0; k<n; ++k)
		for (int i=0; i<=k; ++i)
			W[k]+=P[i].BT;


	for (int i=0; i<n-1; ++i) w+=W[i]-P[i+1].AT;

	aw = w/(float)n;

	SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), 7);

	return aw;
}

float SJF()
{
	int k;
	int BT[15];
	string Pname[15];

	SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), 11);
	cout << "  + SJF:" << endl;
	SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), 7);
	cout << "    Scheduled:   ";

	ProcBTColor(0);
	k = P[0].BT / 0.2;
	for (int i=0; i<k; ++i) cout << char(178);

	for (int i=1; i<n; ++i) BT[i-1]=P[i].BT, Pname[i-1]=P[i].name;

	for (int i=0; i<n; ++i)
	{
		int x = BT[i];
		int y = i;
		string z = Pname[i];

		while ((y>0) && BT[y-1] > x)
		{
			BT[y] = BT[y-1];
			Pname[y] = Pname[y-1];						// We are shifting the element to the right!
			y--;
		}
		BT[y] = x;
		Pname[y] = z;
	}

	for (int i=1; i<n; ++i)
	{
		if (Pname[i]=="P1") SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), 12);
			else if (Pname[i]=="P2") SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), 14);
				else if (Pname[i]=="P3") SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE),10);
					else if (Pname[i]=="P4") SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), 9);
		k = BT[i] / 0.2;
		for (int i=0; i<k; ++i) cout << char(178);
	}

	SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), 7);
}

float SRTF()
{
	int k;

	SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), 11);
	cout << "  + SRTF:" << endl;
	SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), 7);
	cout << "    Scheduled:   ";
	for (int i=0; i<n; ++i)
	{
		ProcBTColor(i);
		k = P[i].BT / 0.2;
		for (int j=0; j<k; ++j)
		{
			cout << char(178);
		}
	}
	SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), 7);
}

float RR(float ts)
{
	int k;
	float BTr[16];	// Burst Time remaining

	SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), 11);
	cout << "  + RR:" << endl;
	SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), 7);
	cout << "    Scheduled:   ";

	for (int i=0; i<n; ++i) BTr[i]=P[i].BT;

	int i = 0;
	while (BTr[i]>=-maxBT)
	{
		ProcBTColor(i);

		k = (int)ceil(min((double)ts/0.2,((double)BTr[i])/0.2));

		for (int j=0; j<k; ++j)		// Still have some problems with real time slot (e.g: 1.2, 1.4, 1.6,...)
		{
			cout << char(178);
		}
		BTr[i]-=ts;
		++i;
		if (i==n) i=0;
	}

	SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), 7);
}

/*----------------------------------------------------------------------------------------------------------------------*/

int main()
{
	n = 4;

	P[0]=newProc("P1", 0.0, 8);
	P[1]=newProc("P2", 0.4, 4);
	P[2]=newProc("P3", 1.0, 1);
	P[3]=newProc("P4", 1.3, 5);

	getMaxAT(maxAT,4);
	getMinAT(minAT,4);
	getMaxBT(maxBT,4);
	getMinBT(minBT,4);

	InfoTable(n);
	ProcInfo(n);
	ProcVisualize(n);

	gotoxy(0,6+2*n);
	SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), MAGENTA);
	cout << "- Question 1: " << endl;

	wFCFS = FCFS(); 	cout << endl;
	wSJF = SJF(); 		cout << endl;
	wSRTF = SRTF(); 	cout << endl;
	wRR = RR(1.0);		cout << endl;

	/*
	cout << "* SRTF" << endl;
	cout << "Scheduled" << endl;
	cout << "[--][---][-----][-----------------][-------------------------][--------------------------------------]" << endl;
	cout << " P1   P2    P3 		P2 			P4				P1" << endl;
	cout << endl;
	*/

	COORDy = wherey()+1;

	DisplayAnswers(COORDy);

	/*
	cout << "* FCFS	: " << endl;
	cout << "	T(P1) = 8 ms" << endl;
	cout << "	T(P2) = 11.6 ms" << endl;
	cout << "	T(P3) = 12 ms" << endl;
	cout << "	T(P4) = 16.7 ms" << endl;
	cout << "   -->  Average T = 12.075 ms" << endl;
	cout << "* SJF	: " << endl;
	cout << "	T(P1) = 8 ms" << endl;
	cout << "        T(P2) = 12.6 ms" << endl;
	cout << "        T(P3) = 8 ms" << endl;
	cout << "        T(P4) = 16.7 ms" << endl;
	cout << "   -->  Average T = 11.325 ms" << endl;
	cout << "* SRTF	:" << endl;
	cout << "	T(P1) = 18 ms" << endl;
	cout << "       T(P2) = 5 ms" << endl;
	cout << "       T(P3) = 1 ms" << endl;
	cout << "       T(P4) = 9.1 ms" << endl;
	cout << "  -->  Average T = 8.275 ms" << endl;
	cout << "* RR	: " << endl;
	cout << "	T(P1) = 18 ms" << endl;
	cout << "        T(P2) = 11.6 ms" << endl;
	cout << "        T(P3) = 2 ms" << endl;
	cout << "        T(P4) = 13.7 ms" << endl;
	cout << "   -->  Average T = 11.325 ms" << endl;
	*/

	cin.get();
	cin.get();
}
