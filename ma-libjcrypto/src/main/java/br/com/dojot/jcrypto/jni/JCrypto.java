package br.com.dojot.jcrypto.jni;
import java.nio.ByteBuffer;


public class JCrypto {
	/* Return the size of the context */
	public static native int aes_size();
	/* Return indicates error occurrence */
    public static native int aes_init(ByteBuffer ctx, byte[] iv, int mode);
    public static native int aes_key_exp(ByteBuffer ctx, byte[] key, int direction);
    public static native int aes_enc(ByteBuffer ctx, byte[] output, int out_offset, int out_len, byte[] input, int in_offset, int in_len, int blockIndex);

    /* Return the size of the context */
    public static native int aes_gcm_size();
    /* Return indicates error occurrence */
    public static native int aes_gcm_init(ByteBuffer ctx, byte[] iv, int taglen, int mode);
    public static native int aes_gcm_key_exp(ByteBuffer ctx, byte[] key, int direction);
    public static native int aes_gcm_aad(ByteBuffer ctx, byte[] aad);
    public static native int aes_gcm_enc(ByteBuffer ctx, byte[] output, int out_offset, int out_len, byte[] input, int in_offset, int in_len, int blockIndex);
}
