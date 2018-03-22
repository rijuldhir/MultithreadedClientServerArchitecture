# MultithreadedClientServerArchitecture
Operating Systems Lab Project

# Server Architecture
1. generate_request() to generate request and receive file
2. acknowledgement_Handling() to generate SUCCESS ACK and handle OK ACK from Client




############### Client Architecture ###############
### Threads
1) Thread to catch the server request and create thread to process the request
2) Thread that generates the fibbonacci sequence of a given particular length
3) Thread that writes the result to the recieved blank file
4) Thread that sends the file with success message
5) Thread for handling the acknowledgement
