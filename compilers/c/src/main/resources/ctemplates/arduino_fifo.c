#define MAX_INSTANCES 32
#define FIFO_SIZE 256

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

int fifo_dq = 0;
int fifo_enq = 1;
byte fifo[2][FIFO_SIZE];
int fifo_head[2] = {0};
int fifo_tail[2] = {0};

// Returns the number of byte currently in the fifo
int fifo_byte_length() {
	if (fifo_tail[fifo_enq] >= fifo_head[fifo_enq])
		return fifo_tail[fifo_enq] - fifo_head[fifo_enq];
	return fifo_tail[fifo_enq] + FIFO_SIZE - fifo_head[fifo_enq];
}

// Returns the number of bytes currently available in the fifo
int fifo_byte_available() {
	return FIFO_SIZE - 1 - fifo_byte_length();
}

// Returns true if the fifo is empty
int fifo_empty() {
	return fifo_head[fifo_dq] == fifo_tail[fifo_dq];
}

#if 0
// Return true if the fifo is full
int fifo_full() {
	return fifo_head == ((fifo_tail + 1) % FIFO_SIZE);
}
#endif

// Enqueue 1 byte in the fifo without checking for available space
// The caller should have checked that there is enough space
int _fifo_enqueue(byte b) {
	fifo[fifo_enq][fifo_tail[fifo_enq]] = b;
	fifo_tail[fifo_enq] = (fifo_tail[fifo_enq] + 1) % FIFO_SIZE;
	return 0; // Dummy added by steffend
}

// Dequeue 1 byte in the fifo.
// The caller should check that the fifo is not empty
byte fifo_dequeue() {
	if (!fifo_empty()) {
		byte result = fifo[fifo_dq][fifo_head[fifo_dq]];
		fifo_head[fifo_dq] = (fifo_head[fifo_dq] + 1) % FIFO_SIZE;
		return result;
	}
	return 0;
}
