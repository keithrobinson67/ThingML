/*C_HEADERS*/

/*CONFIGURATION*/

/*C_GLOBALS*/

typedef uint8_t byte;
typedef uint8_t bool;

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
