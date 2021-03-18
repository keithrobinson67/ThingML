
/*FIFO*/

/* ADC operations */
int adc_read(uint8_t port, uint8_t pin)
{
	return analogRead(pin);
}

int adc_setref(uint8_t port, uint8_t pin, adc_refv_e ref_v)
{
	switch(ref_v) {
	case ADC_REF_VDD:
		analogReference(DEFAULT);
		break;
	case ADC_REF_EXT:
		analogReference(EXTERNAL);
		break;
	case ADC_REF_INT:
		analogReference(INTERNAL);
		break;
	default:
		/* invalid reference requested */
		return -1;
		break;
	}
	return 0;
}

/* GPIO operations */
int gpio_setmode(uint8_t port, uint8_t pin, gpio_dir_e dir)
{
	pinMode(pin, (dir == GPIO_DIR_IN ? INPUT : OUTPUT));
	return 0;
}

int gpio_read(uint8_t port, uint8_t pin)
{
	return digitalRead(pin);
}

int gpio_write(uint8_t port, uint8_t pin, uint8_t value)
{
	digitalWrite(pin, (value == 0 ? LOW : HIGH));
	return -1;
}

/* PWM operations */
int pwm_start(uint8_t port, uint8_t pin, uint16_t duty)
{
	analogWrite(pin, duty);
	return -1;
}

int pwm_stop(uint8_t port, uint8_t pin)
{
	analogWrite(pin, 0);
	return -1;
}

