/*C_HEADERS*/

/*CONFIGURATION*/

/*C_GLOBALS*/

void setup() {
/*INIT_CODE*/

	Set_All_GPIO_Quasi_Mode;
	P0 = 0xffff;
	P1 = 0xffff;
	P2 = 0xffff;

	/**********************************************************************
	PWM frequency = Fpwm/((PWMPH,PWMPL) + 1) <Fpwm = Fsys/PWM_CLOCK_DIV>
								= (16MHz/8)/(0x7CF + 1)
								= 1KHz (1ms)
	***********************************************************************/
	PWM_IMDEPENDENT_MODE;
	PWM_CLOCK_DIV_8;
	PWMPH = HIBYTE(0x7CF);
	PWMPL = LOBYTE(0x7CF);

	SysTick_Init();
}

void loop() {
	while (1) {
		int swap;
/*POLL_CODE*/
		while (processMessageQueue() != 0)
			;

		swap = fifo_dq;
		fifo_dq = fifo_enq;
		fifo_enq = swap;
		set_EA;
		set_IDL;
		clr_EA;
	}
}

void main(void)
{
	setup();
	loop();
}
