/*C_HEADERS*/
typedef uint8_t byte;
typedef uint8_t bool;
#define true 1
#define false 0

/*CONFIGURATION*/

/*C_GLOBALS*/

void setup() {
/*INIT_CODE*/
}

void loop() {
/*POLL_CODE*/
    processMessageQueue();
}

void main(void)
{
	setup();
	loop();
}
