/*****************************************************
 *      THIS IS A GENERATED FILE. DO NOT EDIT.
 *
 *  Generated from ThingML (http://www.thingml.org)
 *****************************************************/

#ifndef RUNTIME_H_
#define RUNTIME_H_

#ifdef __cplusplus
extern "C" {
#endif

#include <stdint.h>

typedef unsigned char byte;

typedef union {
   uint8_t bytebuffer[sizeof(void *)];
   void* ptr;
} ptr_union_t;


void _fifo_enqueue_ptr(void * ptr);
char * _malloc_string_copy(char * str);
void _free_string_copy(char * str);

//static_assert(sizeof(void*) <= PTR_MAX_SIZE*sizeof(uint8_t));



/* Adds and instance to the runtime and returns its id */
uint16_t add_instance(void * instance_struct);
/* Returns the instance with id */
void * instance_by_id(uint16_t id);

/* Returns the number of byte currently in the fifo */
int fifo_byte_length();
/* Returns the number of bytes currently available in the fifo */
int fifo_byte_available();
/* Returns true if the fifo is empty */
int fifo_empty();
/* Return true if the fifo is full */
int fifo_full();
/* Enqueue 1 byte in the fifo if there is space
   returns 1 for sucess and 0 if the fifo was full */
int fifo_enqueue(byte b);
/* Enqueue 1 byte in the fifo without checking for available space
   The caller should have checked that there is enough space */
int _fifo_enqueue(byte b);
/* Dequeue 1 byte in the fifo.
   The caller should check that the fifo is not empty */
byte fifo_dequeue();

// Synchronization for thread safe FIFO access
void fifo_lock();
void fifo_unlock();
void fifo_wait();
void fifo_unlock_and_notify();

void init_runtime();

typedef enum {
	ADC_REF_VDD,
	ADC_REF_EXT,
	ADC_REF_INT
} adc_refv_e;

typedef enum {
	GPIO_DIR_IN,
	GPIO_DIR_OUT
} gpio_dir_e;

/* ADC operations */
int adc_read(uint8_t port, uint8_t pin);
int adc_setref(uint8_t port, uint8_t pin, adc_refv_e ref_v);

/* GPIO operations */
int gpio_setmode(uint8_t port, uint8_t pin, gpio_dir_e dir);
int gpio_read(uint8_t port, uint8_t pin);
int gpio_write(uint8_t port, uint8_t pin, bool value);

/* PWM operations */
int pwm_start(uint8_t port, uint8_t pin, uint16_t duty);
int pwm_stop(uint8_t port, uint8_t pin);


#ifdef __cplusplus
}
#endif

#endif /*RUNTIME_H_*/
