package br.com.dojot.jcrypto.hash;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigestSpi;
import java.util.Arrays;

import br.com.dojot.jcrypto.util.CryptoUtil;
import br.com.dojot.jcrypto.util.SecureUtil;

public class SHA512 extends MessageDigestSpi {
	public static final int DIGEST_LEN = 64;
	public static final int BYTES_BLOCK_LEN = 128;
	
	/** Message schedule. */
	private static final long K[] = 
	{
		0x428a2f98d728ae22L, 0x7137449123ef65cdL, 0xb5c0fbcfec4d3b2fL, 0xe9b5dba58189dbbcL,
		0x3956c25bf348b538L, 0x59f111f1b605d019L, 0x923f82a4af194f9bL, 0xab1c5ed5da6d8118L,
		0xd807aa98a3030242L, 0x12835b0145706fbeL, 0x243185be4ee4b28cL, 0x550c7dc3d5ffb4e2L,
		0x72be5d74f27b896fL, 0x80deb1fe3b1696b1L, 0x9bdc06a725c71235L, 0xc19bf174cf692694L,
		0xe49b69c19ef14ad2L, 0xefbe4786384f25e3L, 0x0fc19dc68b8cd5b5L, 0x240ca1cc77ac9c65L,
		0x2de92c6f592b0275L, 0x4a7484aa6ea6e483L, 0x5cb0a9dcbd41fbd4L, 0x76f988da831153b5L,
		0x983e5152ee66dfabL, 0xa831c66d2db43210L, 0xb00327c898fb213fL, 0xbf597fc7beef0ee4L,
		0xc6e00bf33da88fc2L, 0xd5a79147930aa725L, 0x06ca6351e003826fL, 0x142929670a0e6e70L,
		0x27b70a8546d22ffcL, 0x2e1b21385c26c926L, 0x4d2c6dfc5ac42aedL, 0x53380d139d95b3dfL,
		0x650a73548baf63deL, 0x766a0abb3c77b2a8L, 0x81c2c92e47edaee6L, 0x92722c851482353bL,
		0xa2bfe8a14cf10364L, 0xa81a664bbc423001L, 0xc24b8b70d0f89791L, 0xc76c51a30654be30L,
		0xd192e819d6ef5218L, 0xd69906245565a910L, 0xf40e35855771202aL, 0x106aa07032bbd1b8L,
		0x19a4c116b8d2d0c8L, 0x1e376c085141ab53L, 0x2748774cdf8eeb99L, 0x34b0bcb5e19b48a8L,
		0x391c0cb3c5c95a63L, 0x4ed8aa4ae3418acbL, 0x5b9cca4f7763e373L, 0x682e6ff3d6b2b8a3L,
		0x748f82ee5defb2fcL, 0x78a5636f43172f60L, 0x84c87814a1f0ab72L, 0x8cc702081a6439ecL,
		0x90befffa23631e28L, 0xa4506cebde82bde9L, 0xbef9a3f7b2c67915L, 0xc67178f2e372532bL,
		0xca273eceea26619cL, 0xd186b8c721c0c207L, 0xeada7dd6cde0eb1eL, 0xf57d4f7fee6ed178L,
		0x06f067aa72176fbaL, 0x0a637dc5a2c898a6L, 0x113f9804bef90daeL, 0x1b710b35131c471bL,
		0x28db77f523047d84L, 0x32caab7b40c72493L, 0x3c9ebe0a15c9bebcL, 0x431d67c49c100d4cL,
		0x4cc5d4becb3e42b6L, 0x597f299cfc657e2aL, 0x5fcb6fab3ad6faecL, 0x6c44198c4a475817L
	};
	
	/** Maximum message size in bytes. */
	private static final BigInteger limit = BigInteger.valueOf(2).pow(125);
	
	
	
	/** Hash value. */
	private long[] H;
	
	/** Working variables according to FIPS 180-4. */
	private long a, b, c, d, e, f, g, h;
	
	/** Buffer to store the last block of message. */
	private byte[] block;
	
	/** Offset inside the buffer. */
	private int blockOffset;
	
	/** Total number of bytes already processed. */
	private BigInteger numberOfBytesProcessed;
	
	public SHA512() {
		this.H = new long[8];
		this.block = new byte[BYTES_BLOCK_LEN];		
		
		initState();
	}
	
	@Override
	protected byte[] engineDigest() {
		byte[] digest, extra;
		ByteBuffer bb;
		
		digest = new byte[64];
		bb = ByteBuffer.wrap(digest);
		extra = addPaddingToMessage();
		doSHA256Update(extra, 0, extra.length);
		
		for(int i = 0; i < 8; i++) {
			bb.putLong(H[i]);
		}
		initState();
		return digest;
	}

	@Override
	protected void engineReset() {
		initState();
	}
	
	@Override
	protected int engineGetDigestLength() {
		return DIGEST_LEN;
	}

	@Override
	protected void engineUpdate(byte input) {
		doSHA256Update(new byte[]{input}, 0, 1);
	}

	@Override
	protected void engineUpdate(byte[] input, int offset, int len) {
		doSHA256Update(input, offset, len);
	}
	
	private void doSHA256Update(byte[] input, int offset, int len) 
	{
		int remainingBytes = CryptoUtil.calculateRemainingBytes(len, offset, blockOffset, BYTES_BLOCK_LEN);
		int fullBytes = CryptoUtil.calculateFullBytes(len, offset, blockOffset, remainingBytes);
		
		if(fullBytes == 0 && len > 0) {
			/* There is no enough bytes to be processed, so put it in the buffer */
			System.arraycopy(input, offset, block, blockOffset, SecureUtil.sub_s(len, offset));
			blockOffset = SecureUtil.add_s(blockOffset, len);
			blockOffset = SecureUtil.sub_s(blockOffset, offset);
		} else {
			/* Process */
			byte[] data = new byte[fullBytes];
			System.arraycopy(block, 0, data, 0, blockOffset);
			if(len > 0) {
				System.arraycopy(input, offset, data, blockOffset, SecureUtil.sub_s(fullBytes, blockOffset));
			}
			updateSHA256State(data);
			
			System.arraycopy(input, SecureUtil.sub_s(len, remainingBytes), block, 0, remainingBytes);
			blockOffset = remainingBytes;
			numberOfBytesProcessed = numberOfBytesProcessed.add(BigInteger.valueOf(fullBytes));
		}
	}
	
	private long ch(long x, long y, long z) {
		return (x & y) ^ (~x & z);
	}
	
	private long maj(long x, long y, long z) {
		return (x & y) ^ (x & z) ^ (y & z);
	}
	
	private long rotr(long x, int n) {
		return (x >>> n) | (x << 64 - n);
	}
	
	private long shr(long x, int n) {
		return (x >>> n);
	}
	
	private byte[] addPaddingToMessage() 
	{
		byte[] sizeByteEncoded, additionalBlock = new byte[0];
		BigInteger numberOfZeros;
		ByteBuffer bb;
		
		this.block[blockOffset] = (byte) 0x80;
		numberOfBytesProcessed = numberOfBytesProcessed.add(BigInteger.valueOf(blockOffset));
		// Number of bits processed
		numberOfBytesProcessed = numberOfBytesProcessed.multiply(BigInteger.valueOf(8L));	
		sizeByteEncoded = numberOfBytesProcessed.toByteArray();
		
		// Expression to determine the number of zeros that will be inserted.
		// length(in bits) + 1 + k = 896 mod 1024
		numberOfZeros = BigInteger.valueOf(896L).subtract(numberOfBytesProcessed).subtract(BigInteger.ONE);
		numberOfZeros = numberOfZeros.mod(BigInteger.valueOf(1024));
		
		if((blockOffset << 3) + 128 + numberOfZeros.longValue() + 1 > 1024) {
			additionalBlock = new byte[BYTES_BLOCK_LEN];
			bb = ByteBuffer.wrap(additionalBlock, BYTES_BLOCK_LEN - sizeByteEncoded.length,  sizeByteEncoded.length);
			bb.put(sizeByteEncoded);
		}
		else {
			bb = ByteBuffer.wrap(this.block, BYTES_BLOCK_LEN - sizeByteEncoded.length,  sizeByteEncoded.length);
			bb.put(sizeByteEncoded);	
		}
		this.blockOffset = BYTES_BLOCK_LEN;
		if(numberOfBytesProcessed.compareTo(limit) >= 0) {
			throw new IllegalArgumentException("Processed more bytes than allowed!");
		}
		
		return additionalBlock;
	}
	
	private void updateSHA256State(byte[] message)
	{
		for(int i = 0; i < message.length / BYTES_BLOCK_LEN; i++) {
			
			long[] Wt = scheduleMessage(message, i * BYTES_BLOCK_LEN, BYTES_BLOCK_LEN);
			
			a = H[0];
			b = H[1];
			c = H[2];
			d = H[3];
			e = H[4];
			f = H[5];
			g = H[6];
			h = H[7];
			
			for(int t = 0; t < 80; t++) {
				long T1, T2;
				T1 = h + sum1(e) + ch(e, f, g) + K[t] + Wt[t];
				T2 = sum0(a) + maj(a, b, c);
				h = g;
				g = f;
				f = e;
				e = d + T1;
				d = c;
				c = b;
				b = a;
				a = T1 + T2;
			}
			
			H[0] += a;
			H[1] += b;
			H[2] += c;
			H[3] += d;
			H[4] += e;
			H[5] += f;
			H[6] += g;
			H[7] += h;
		}
	}
	
	private long[] scheduleMessage(byte[] message, int offset, int len)
	{
		long[] Wt;
		
		Wt = new long[80];
		ByteBuffer bb = ByteBuffer.wrap(message, offset, len);
		for(int t = 0; t < 16; t++) {
			Wt[t] = bb.getLong();
		}
		
		for(int t = 16; t < 80; t++) {
			Wt[t] = sigma1(Wt[t-2]) + Wt[t-7] + sigma0(Wt[t-15]) + Wt[t-16];
		}
		
		return Wt;
	}
	
	private void initState() 
	{
		Arrays.fill(block, (byte)0x00);
		
		/** Initial hash value. */
		this.H[0] = 0x6a09e667f3bcc908L;
		this.H[1] = 0xbb67ae8584caa73bL;
		this.H[2] = 0x3c6ef372fe94f82bL;
		this.H[3] = 0xa54ff53a5f1d36f1L;
		this.H[4] = 0x510e527fade682d1L;
		this.H[5] = 0x9b05688c2b3e6c1fL;
		this.H[6] = 0x1f83d9abfb41bd6bL;
		this.H[7] = 0x5be0cd19137e2179L;	
		
		this.numberOfBytesProcessed = BigInteger.valueOf(0);
		this.blockOffset = 0;
	}
	
	private long sigma1(long x) {
		return rotr(x, 19) ^ rotr(x, 61) ^ shr(x, 6);
	}
	
	private long sigma0(long x) {
		return rotr(x, 1) ^ rotr(x, 8) ^ shr(x, 7);
	}
	
	private long sum0(long x) {
		return rotr(x, 28) ^ rotr(x, 34) ^ rotr(x, 39);
	}
	
	private long sum1(long x) {
		return rotr(x, 14) ^ rotr(x, 18) ^ rotr(x, 41);
	}
}
