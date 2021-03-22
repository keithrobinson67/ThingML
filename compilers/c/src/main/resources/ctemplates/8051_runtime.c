/*FIFO*/

/* ADC operations */
int adc_read(uint8_t port, uint8_t pin)
{
	return -1;
}

int adc_setref(uint8_t port, uint8_t pin, adc_refv_e ref_v)
{
	return -1;
}

/* GPIO operations */
int gpio_setmode(uint8_t port, uint8_t pin, gpio_dir_e dir)
{
	return -1;
}

int gpio_read(uint8_t port, uint8_t pin)
{
	return -1;
}

int gpio_write(uint8_t port, uint8_t pin, uint8_t value)
{
	return -1;
}

/* PWM operations */
int pwm_start(uint8_t port, uint8_t pin, uint16_t duty)
{
	return -1;
}

int pwm_stop(uint8_t port, uint8_t pin)
{
	return -1;
}

