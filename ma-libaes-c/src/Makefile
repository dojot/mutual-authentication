all: libjcrypto

libjcrypto: aes.o intel-gcm.o intel-aes.o jcrypto_jni.o secure-util.o
	gcc -Wall -fPIC -shared aes.o secure-util.o intel-gcm.o intel-aes.o jcrypto_jni.o -o libjcrypto.so
	execstack -c libjcrypto.so	
   
aes.o: aes.c
	gcc -Wall -fPIC -mssse3 -c aes.c -o aes.o

secure-util.o: secure-util.c
	gcc -Wall -fPIC -c secure-util.c -o secure-util.o

ntel-gcm.o: intel-gcm.s
	as intel-gcm.s -o intel-gcm.o

intel-aes.o: intel-aes.s
	as intel-aes.s -o intel-aes.o

jcrypto_jni.o: jcrypto_jni.c
	gcc -Wall -fPIC -I/usr/lib/jvm/java-8-openjdk-amd64/include/linux -I/usr/lib/jvm/java-8-openjdk-amd64/include -c jcrypto_jni.c -o jcrypto_jni.o
	
clean:	
	rm *.o *.so
