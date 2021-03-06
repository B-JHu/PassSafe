package b_jHu.passwordManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class AESWrapper {

	//source: https://www.codejava.net/coding/file-encryption-and-decryption-simple-example
	private static final String ALGORITHM = "AES";
	private static final String TRANSFORMATION = "AES";
	
	public AESWrapper() {

	}

	public void encryptFile(String key, String inputFileDir, String outputFileDir) {
		doCrypto(Cipher.ENCRYPT_MODE, key, inputFileDir, outputFileDir);
	}

	public void decryptFile(String key, String inputFileDir, String outputFileDir) {
		doCrypto(Cipher.DECRYPT_MODE, key, inputFileDir, outputFileDir);
	}

	private void doCrypto(int cipherMode, String key, String inputFileDir, String outputFileDir) {
		try {
			File inputFile = new File(inputFileDir);
			File outputFile = new File(outputFileDir);

			Key secretKey = new SecretKeySpec(Arrays.copyOf(key.getBytes(), 16), ALGORITHM);
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(cipherMode, secretKey);

			FileInputStream inputStream = new FileInputStream(inputFile);
			byte[] inputBytes = new byte[(int) inputFile.length()];
			inputStream.read(inputBytes);

			byte[] outputBytes = cipher.doFinal(inputBytes);

			FileOutputStream outputStream = new FileOutputStream(outputFile);
			outputStream.write(outputBytes);

			inputStream.close();
			outputStream.close();
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException | IOException e) {
			new ErrorMessage("Error encrypting/decrypting the file", e);
		}       
	}
}

