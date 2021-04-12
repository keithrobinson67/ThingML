/*FIFO*/

/* ADC operations */
int adc_read(uint8_t port, uint8_t channel)
{
	uint8_t i;
	int result;

		switch (channel)
		{
			case 0:
				Enable_ADC_AIN0;
				break;
			case 1:
				Enable_ADC_AIN1;
				break;
			case 2:
				Enable_ADC_AIN2;
				break;
			case 3:
				Enable_ADC_AIN3;
				break;
			case 4:
				Enable_ADC_AIN4;
				break;
			case 5:
				Enable_ADC_AIN5;
				break;
			case 6:
				Enable_ADC_AIN6;
				break;
			case 7:
				Enable_ADC_AIN7;
				break;
			default:
				return 0xffff;
		}

		CKDIV = 0x02;
		for (i = 0; i < 4; i++) {
			clr_ADCF;
			set_ADCS;
			while(ADCF == 0);
		}
		CKDIV = 0x00;
		result = (uint16_t)((ADCRH << 4) + (ADCRL & 0x0F));
		Disable_ADC;

		return result;
}

int adc_setref(uint8_t port, uint8_t pin, adc_refv_e ref_v)
{
	return 0;
}

/* GPIO operations */

static int  gpio_setmode_in(uint8_t port, uint8_t pin)
{
	switch(port)
	{
	case 0:
		switch (pin)
		{
		case 0:
			P00_Input_Mode;
			break;
		case 1:
			P01_Input_Mode;
			break;
		case 2:
			P02_Input_Mode;
			break;
		case 3:
			P03_Input_Mode;
			break;
		case 4:
			P04_Input_Mode;
			break;
		case 5:
			P05_Input_Mode;
			break;
		case 6:
			P06_Input_Mode;
			break;
		case 7:
			P07_Input_Mode;
			break;
		default:
			return 0xffff;
		}
		break;
	case 1:
		switch (pin)
		{
		case 0:
			P10_Input_Mode;
			break;
		case 1:
			P11_Input_Mode;
			break;
		case 2:
			P12_Input_Mode;
			break;
		case 3:
			P13_Input_Mode;
			break;
		case 4:
			P14_Input_Mode;
			break;
		case 5:
			P15_Input_Mode;
			break;
		case 6:
			P16_Input_Mode;
			break;
		case 7:
			P17_Input_Mode;
			break;
		default:
			return 0xffff;
		}
		break;
	case 2:
		return 0xffff;
		break;
	case 3:
		switch (pin)
		{
		case 0:
			P30_Input_Mode;
			break;
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		default:
			return 0xffff;
		}
		break;
	default:
		return 0xffff;
	}

	/* successful operation */
	return 0;

}

static int  gpio_setmode_out(uint8_t port, uint8_t pin)
{
	switch(port)
	{
	case 0:
		switch (pin)
		{
		case 0:
			P00_PushPull_Mode;
			break;
		case 1:
			P01_PushPull_Mode;
			break;
		case 2:
			P02_PushPull_Mode;
			break;
		case 3:
			P03_PushPull_Mode;
			break;
		case 4:
			P04_PushPull_Mode;
			break;
		case 5:
			P05_PushPull_Mode;
			break;
		case 6:
			P06_PushPull_Mode;
			break;
		case 7:
			P07_PushPull_Mode;
			break;
		default:
			return 0xffff;
		}
		break;
	case 1:
		switch (pin)
		{
		case 0:
			P10_PushPull_Mode;
			break;
		case 1:
			P11_PushPull_Mode;
			break;
		case 2:
			P12_PushPull_Mode;
			break;
		case 3:
			P13_PushPull_Mode;
			break;
		case 4:
			P14_PushPull_Mode;
			break;
		case 5:
			P15_PushPull_Mode;
			break;
		case 6:
			P16_PushPull_Mode;
			break;
		case 7:
			P17_PushPull_Mode;
			break;
		default:
			return 0xffff;
		}
		break;
	case 2:
		return 0xffff;
		break;
	case 3:
		switch (pin)
		{
		case 0:
			P30_PushPull_Mode;
			break;
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		default:
			return 0xffff;
		}
		break;
	default:
		return 0xffff;
	}

	/* successful operation */
	return 0;
}

int gpio_setmode(uint8_t port, uint8_t pin, gpio_dir_e dir)
{
	switch (dir)
	{
	case GPIO_DIR_IN:
		gpio_setmode_in(port, pin);
		break;
	case GPIO_DIR_OUT:
		gpio_setmode_out(port, pin);
		break;
	default:
		return 0xffff;
	}
}

int gpio_read(uint8_t port, uint8_t pin)
{
	switch(port)
	{
	case 0:
		switch (pin)
		{
		case 0:
			return P00;
			break;
		case 1:
			return P01;
			break;
		case 2:
			return P02;
			break;
		case 3:
			return P03;
			break;
		case 4:
			return P04;
			break;
		case 5:
			return P05;
			break;
		case 6:
			return P06;
			break;
		case 7:
			return P07;
			break;
		default:
			return 0xffff;
		}
		break;
	case 1:
		switch (pin)
		{
		case 0:
			return P10;
			break;
		case 1:
			return P11;
			break;
		case 2:
			return P12;
			break;
		case 3:
			return P13;
			break;
		case 4:
			return P14;
			break;
		case 5:
			return P15;
			break;
		case 6:
			return P16;
			break;
		case 7:
			return P17;
			break;
		default:
			return 0xffff;
		}
		break;
	case 2:
		return 0xffff;
		break;
	case 3:
		switch (pin)
		{
		case 0:
			return P30;
			break;
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		default:
			return 0xffff;
		}
		break;
	default:
		return 0xffff;
	}
}

int gpio_write(uint8_t port, uint8_t pin, uint8_t value)
{
	switch(port)
	{
	case 0:
		switch (pin)
		{
		case 0:
			P00 = value;
			break;
		case 1:
			P01 = value;
			break;
		case 2:
			P02 = value;
			break;
		case 3:
			P03 = value;
			break;
		case 4:
			P04= value;
			break;
		case 5:
			P05 = value;
			break;
		case 6:
			P06 = value;
			break;
		case 7:
			P07 = value;
			break;
		default:
			return 0xffff;
		}
		break;
	case 1:
		switch (pin)
		{
		case 0:
			P10 = value;
			break;
		case 1:
			P11 = value;
			break;
		case 2:
			P12 = value;
			break;
		case 3:
			P13 = value;
			break;
		case 4:
			P14= value;
			break;
		case 5:
			P15 = value;
			break;
		case 6:
			P16 = value;
			break;
		case 7:
			P17 = value;
			break;
		default:
			return 0xffff;
		}
		break;
	case 2:
		return 0xffff;
		break;
	case 3:
		switch (pin)
		{
		case 0:
			P30 = value;
			break;
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		default:
			return 0xffff;
		}
		break;
	default:
		return 0xffff;
	}

	/* successful operation */
	return 0;
}

static int pwm_on(uint8_t channel)
{
	switch(channel)
	{
		case 0:
			PWM0_P12_OUTPUT_ENABLE;
			break;
		case 1:
			PWM1_P11_OUTPUT_ENABLE;
			break;
		case 2:
			PWM2_P10_OUTPUT_ENABLE;
			break;
		case 3:
			PWM3_P00_OUTPUT_ENABLE;
			break;
		case 4:
			PWM4_P01_OUTPUT_ENABLE;
			break;
		case 5:
			PWM5_P15_OUTPUT_ENABLE;
			break;
		default:
			break;
	}

	/* operation successful */
	return 0;
}

static int pwm_off(uint8_t channel)
{
	switch(channel)
	{
		case 0:
			PWM0_P12_OUTPUT_DISABLE;
			break;
		case 1:
			PWM1_P11_OUTPUT_DISABLE;
			break;
		case 2:
			PWM2_P10_OUTPUT_DISABLE;
			break;
		case 3:
			PWM3_P00_OUTPUT_DISABLE;
			break;
		case 4:
			PWM4_P01_OUTPUT_DISABLE;
			break;
		case 5:
			PWM5_P15_OUTPUT_DISABLE;
			break;
		default:
			return 0xffff;
	}

	/* operation successful */
	return 0;
}

/* PWM operations */
int pwm_start(uint8_t port, uint8_t channel, uint16_t duty)
{
	switch(channel)
	{
		case 0:
			PWM0H = HIBYTE(duty);
			PWM0L = LOBYTE(duty);
			break;
		case 1:
			PWM1H = HIBYTE(duty);
			PWM1L = LOBYTE(duty);
			break;
		case 2:
			PWM2H = HIBYTE(duty);
			PWM2L = LOBYTE(duty);
			break;
		case 3:
			PWM3H = HIBYTE(duty);
			PWM3L = LOBYTE(duty);
			break;
		case 4:
			PWM4H = HIBYTE(duty);
			PWM4L = LOBYTE(duty);
			break;
		case 5:
			set_SFRPAGE;
			PWM5H = HIBYTE(duty);
			PWM5L = LOBYTE(duty);
			clr_SFRPAGE;
			break;
		default:
			return 0xffff;
	}

	/* use new period & duty cycle */
	set_LOAD;
	set_PWMRUN;
	return pwm_on(channel);
}

int pwm_stop(uint8_t port, uint8_t channel)
{
	return pwm_off(channel);
}

static uint32_t milliseconds = 0;

uint32_t millis(void)
{
	return milliseconds;
}

void Timer1_ISR (void) interrupt 3
{
	TH1 = HIBYTE(TIMER_DIV12_VALUE_1ms);
    TL1 = LOBYTE(TIMER_DIV12_VALUE_1ms);
	milliseconds++;
}

bool SysTick_Init(void)
{
	TIMER1_MODE1_ENABLE;
	clr_T1M;
	TH1 = HIBYTE(TIMER_DIV12_VALUE_1ms);
    TL1 = LOBYTE(TIMER_DIV12_VALUE_1ms);
	milliseconds = 0;

	set_ET1;
	set_TR1;
	return true;
}
