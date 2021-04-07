/*****************************************************
 *      THIS IS A GENERATED FILE. DO NOT EDIT.
 *
 *  Generated from ThingML (http://www.thingml.org)
 *****************************************************/

#include <unistd.h>
#include <stdlib.h>
#include <stdio.h>
#include <ctype.h>
#include <string.h>
#include <math.h>
#include <signal.h>
#include <pthread.h>

#include "runtime.h"

static long start_time;

#define MAX_INSTANCES 2
#define FIFO_SIZE 32768

/*********************************
 * Instance IDs and lookup
 *********************************/

void * instances[MAX_INSTANCES];
uint16_t instances_count = 0;

void * instance_by_id(uint16_t id) {
  return instances[id];
}

uint16_t add_instance(void * instance_struct) {
  instances[instances_count] = instance_struct;
  return instances_count++;
}

/******************************************
 * Simple byte FIFO implementation
 ******************************************/

byte fifo[FIFO_SIZE];
int fifo_head = 0;
int fifo_tail = 0;

// Returns the number of byte currently in the fifo
int fifo_byte_length() {
  if (fifo_tail >= fifo_head)
    return fifo_tail - fifo_head;
  return fifo_tail + FIFO_SIZE - fifo_head;
}

// Returns the number of bytes currently available in the fifo
int fifo_byte_available() {
  return FIFO_SIZE - 1 - fifo_byte_length();
}

// Returns true if the fifo is empty
int fifo_empty() {
  return fifo_head == fifo_tail;
}

// Return true if the fifo is full
int fifo_full() {
  return fifo_head == ((fifo_tail + 1) % FIFO_SIZE);
}

// Enqueue 1 byte in the fifo if there is space
// returns 1 for sucess and 0 if the fifo was full
int fifo_enqueue(byte b) {
  int new_tail = (fifo_tail + 1) % FIFO_SIZE;
  if (new_tail == fifo_head) return 0; // the fifo is full
  fifo[fifo_tail] = b;
  fifo_tail = new_tail;
  return 1;
}

// Enqueue 1 byte in the fifo without checking for available space
// The caller should have checked that there is enough space
int _fifo_enqueue(byte b) {
  fifo[fifo_tail] = b;
  fifo_tail = (fifo_tail + 1) % FIFO_SIZE;
  return 0; // Dummy added by steffend
}

// Dequeue 1 byte in the fifo.
// The caller should check that the fifo is not empty
byte fifo_dequeue() {
  if (!fifo_empty()) {
    byte result = fifo[fifo_head];
    fifo_head = (fifo_head + 1) % FIFO_SIZE;
    return result;
  }
  return 0;
}

// Enqueue a pointer in the fifo without checking for available space
// The caller should have checked that there is enough space
void _fifo_enqueue_ptr(void * ptr) {
  ptr_union_t ptr_union;
  ptr_union.ptr = ptr;
  for (uint8_t i=0; i<sizeof(void *); i++)
    _fifo_enqueue(ptr_union.bytebuffer[i] & 0xFF );
}

char * _malloc_string_copy(char * str) {
  if (str == NULL) return NULL;
  // Allocate memory for the string copy
  char * result = malloc(sizeof(*str) * strlen(str));
  if(result==NULL)                     
  {
      printf("FATAL: ThingML runtime failed to allocate memory for a String (char *) parameter. Exiting.");
      exit(-1);
  }
  // Copy the string
  strcpy(result, str);
  return result;
}

void _free_string_copy(char * str) {
  if (str != NULL) {
    // Overwrite the whole string with NULLs so that it cannot be used further by accident
    for (size_t i=0; i<=strlen(str); i++) str[i] = 0;
    // Free the memory
    free(str);
  }
}

/******************************************
 * Synchronization for thread safe access
 ******************************************/

pthread_mutex_t fifo_mut;
pthread_cond_t fifo_cond;

void fifo_lock() {
  pthread_mutex_lock (&fifo_mut);
}
void fifo_unlock() {
  pthread_mutex_unlock (&fifo_mut);	  
}
void fifo_wait() {
  pthread_cond_wait (&fifo_cond, &fifo_mut);
}
void fifo_unlock_and_notify() {
  pthread_mutex_unlock (&fifo_mut);
  pthread_cond_signal (&fifo_cond);
}

/******************************************
 * MCU DSL runtime
 ******************************************/

/* ADC operations */
int adc_read(uint8_t port, uint8_t pin)
{
	printf("adc_read %d : %d\n", port, pin);
}

int adc_setref(uint8_t port, uint8_t pin, adc_refv_e ref_v)
{
	switch(ref_v) {
	case ADC_REF_VDD:
		printf("adc_setref VDD %d : %d\n", port, pin);
		break;
	case ADC_REF_EXT:
		printf("adc_setref EXT %d : %d\n", port, pin);
		break;
	case ADC_REF_INT:
		printf("adc_setref INT %d : %d\n", port, pin);
		break;
	default:
		printf("adc_setref INVALID %d : %d\n", port, pin);
		/* invalid reference requested */
		return -1;
		break;
	}
	return 0;
}

/* GPIO operations */
int gpio_setmode(uint8_t port, uint8_t pin, gpio_dir_e dir)
{
	printf("gpio_setmode %d : %d : %d\n", port, pin, dir);
}

int gpio_read(uint8_t port, uint8_t pin)
{
	printf("gpio_read %d : %d\n", port, pin);
}

int gpio_write(uint8_t port, uint8_t pin, bool value)
{
	printf("gpio_write %d : %d : %d\n", port, pin, value);
}

/* PWM operations */
int pwm_start(uint8_t port, uint8_t pin, uint16_t duty)
{
	printf("pwm_start %d : %d : %d\n", port, pin, duty);
}

int pwm_stop(uint8_t port, uint8_t pin)
{
	printf("pwm_stop %d : %d\n", port, pin);
}

uint32_t millis()
{
	struct timespec t;
    (void)clock_gettime(CLOCK_MONOTONIC, &t);

    return (t.tv_nsec - start_time) / 1000000;
}


/******************************************
 * Initialization
 ******************************************/

void init_runtime() {
  struct timespec t;
  pthread_mutex_init (&fifo_mut, NULL);
  pthread_cond_init (&fifo_cond, NULL);

  (void)clock_gettime(CLOCK_MONOTONIC, &t);
  start_time = t.tv_nsec;
}
