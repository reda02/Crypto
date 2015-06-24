package com.esgi.crypto;

import java.io.File;

import com.esgi.crypto.homophonique.CypherHomophonique;

public class Application {

	public static final String KEY_FILE = "resources/key.txt";
	public static final String MESSAGE_FILE = "resources/message.txt";
	public static final String ENCODED_FILE = "resources/encoded.txt";
	public static final String FOUND_KEY_FILE = "resources/foundKey.txt";
	
	public static final String ROMAN_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String PONCTUATION = " _.,;:\"'";
	
	public static final String KEY_BASE = ROMAN_ALPHABET/* + PONCTUATION*/;
	
	private FileHandler fileHandler;
	
	public Application() {
		fileHandler = new FileHandler();
//		MonoCipher monoCipher = new MonoCipher(fileHandler);
//		
		File keyFile = new File(KEY_FILE);
		File messageFile = new File(MESSAGE_FILE);
		File encodedFile = new File(ENCODED_FILE);
//		
//		monoCipher.generateKey(key);
//		monoCipher.encode(message, key, encoded);
//		
//		MonoEncodedAttack m = new MonoEncodedAttack(fileHandler);
//		m.findKey(encoded, foundKey);
		
		//CesarCipher cesarCipher = new CesarCipher(fileHandler);
		//cesarCipher.encode(message, key, encoded);
		//cesarCipher.decode(encoded, key, message);
		CypherHomophonique cypherHomophonique = new CypherHomophonique(fileHandler);
		//cypherHomophonique.generateKey(key);
		cypherHomophonique.encode(messageFile, keyFile, encodedFile);
	}
	
	public static void main(String[] args) {
		Application app = new Application();
	}

}
