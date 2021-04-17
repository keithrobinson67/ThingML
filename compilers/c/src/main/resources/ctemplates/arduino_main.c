/*C_HEADERS*/

/*CONFIGURATION*/

/*C_GLOBALS*/

void setup() {
/*INIT_CODE*/
}

void loop() {
	int swap;
/*POLL_CODE*/
	while (processMessageQueue() != 0)
		;
	swap = fifo_dq;
	fifo_dq = fifo_enq;
	fifo_enq = swap;
	delayMicroseconds(1000);
}
