package org.esgi.crypto.homophonic;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.esgi.crypto.Application;
import org.esgi.crypto.FileHandler;
import org.esgi.crypto.ICipher;

public class HomophonicCipher implements ICipher{
	FileHandler fileHandler;
	String mess;
	HashMap<Character, Double> frequencyMap;
	HashMap<Character, Integer> weightMap;

	HashMap<Character, Byte[]> encodingMap;
	HashMap<Byte[], Character> decodingMap;
	int values;
	
	public HomophonicCipher(FileHandler fileHandler) {
		this.fileHandler = fileHandler;
		this.frequencyMap = new HashMap<Character, Double>();
		this.weightMap = new HashMap<Character, Integer>();
		this.encodingMap = new HashMap<Character, Byte[]>();
		this.decodingMap = new HashMap<Byte[], Character>();
		this.frequencyMap = Application.frequencies;
	}
	
	@Override
	public void encode(File message, File key, File encoded) {
		
		generateKey(key);
		
		String mess = fileHandler.readFile(message);
		List<Byte> byteList = new ArrayList<Byte>();
		for (int i = 0; i < mess.length(); i++) {
			Byte[] bA =  encodingMap.get(mess.charAt(i));
			for (int j = 0; j < bA.length; j++) {
				byteList.add(bA[j]);
			}
		}
		
		byte[] byteArray = new byte[byteList.size()];
		for (int i = 0; i < byteArray.length; i++) {
			byteArray[i] = byteList.get(i);
		}
		
		fileHandler.writeByteFile(encoded, byteArray);
	}
	
	private void assignWeight() {
		for (Character c : frequencyMap.keySet()) {
			int val = (int) ((frequencyMap.get(c)*12+6)/25)+1;
			values += val;
			weightMap.put(c, val);
		}
	}

	@Override
	public void decode(File encoded, File key, File message) {
		byte[] keyArray = fileHandler.readByteFile(key.getAbsolutePath());

		byte[] encodedArray = fileHandler.readByteFile(encoded.getAbsolutePath());
		
		List<Byte> encodedList = new ArrayList<Byte>();
		
		for (int i = 0; i < encodedArray.length; i++) {
			encodedList.add(encodedArray[i]);
		}
		
		int index = 0;
		int incr = 0;
		int hIndex = 0;
		while (index < keyArray.length-1) {
			incr = keyArray[index];
			int indTmp = index;
			int arrIndex = 0;
			Byte[] tmpArr = new Byte[incr];
			for (int i = index+1; i <= incr+indTmp; i++) {
				tmpArr[arrIndex] = keyArray[i];
				index++;
				arrIndex++;
			}
			char ch = Application.ALPHABET.charAt(hIndex);
			decodingMap.put(tmpArr, ch);
			
			hIndex++;
			index++;
		}
		
		index = 0;
		String messageHolder = "";
		while (!encodedList.isEmpty()) {
			for (Byte[] b : decodingMap.keySet()) {
				if (!encodedList.isEmpty() && b[0] == encodedList.get(0)) {
					
					messageHolder += decodingMap.get(b);
					for (int i = 0; i < b.length; i++) {
						encodedList.remove(0);
					}
				}
			}
			index++;
		}
		
		fileHandler.writeFile(message, messageHolder);
	}
	
	@Override
	public void generateKey(File key) {
		assignWeight();
		byte[] byte_array = new byte[values];
		
		for (int i = 0; i < byte_array.length; i++) {
			byte_array[i] = new Byte((byte) i);
		}
		
		RandomizeArray(byte_array);
		byteAssociation(byte_array);
		writeKey(key);
	}
	
	private void writeKey(File file) {
		BufferedOutputStream bs = null;
		try {
		    FileOutputStream fs = new FileOutputStream(file);
		    
		    bs = new BufferedOutputStream(fs);
		    
		    byte[] keyTmp = new byte[values + encodingMap.size()+1];
		    int keyIndex = 0;
		    for (Character c : encodingMap.keySet()) {
		    	keyTmp[keyIndex] = (byte) encodingMap.get(c).length;
		    	keyIndex++;
		    	for (int i = 0; i < encodingMap.get(c).length; i++) {
		    		keyTmp[keyIndex] = encodingMap.get(c)[i];
			    	keyIndex++;
				}
			}
		    keyTmp[keyTmp.length-1] = '\0';
		    bs.write(keyTmp);
		    bs.close();
		    bs = null;

		} catch (Exception e) {
			System.out.println(e);
		}

		if (bs != null) {
			try { 
				bs.close(); 
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}
	

	private void byteAssociation(byte[] byte_array) {
		int start = 0, end = 0;
		for (Character c : weightMap.keySet()) {
			end = weightMap.get(c) + start;
			Byte[] bA = new Byte[weightMap.get(c)];
			int bAIndex = 0;
			for (int i = start; i < end; i++) {
				bA[bAIndex] = byte_array[i];
				bAIndex++;
			}
			encodingMap.put(c, bA);
			start = end;
		}
	}
	
	public static byte[] RandomizeArray(byte[] array){
		Random rgen = new Random();	
 
		for (int i=0; i<array.length; i++) {
			byte randomPosition = (byte) rgen.nextInt(array.length);
			byte temp = array[i];
		    array[i] = array[randomPosition];
		    array[randomPosition] = temp;
		}
 
		return array;
	}

	public HashMap<Character, Double> calculateCharacterFrequency(File file) {
		String encodedMessage = fileHandler.readFile(file);
		
		for (int i = 0; i < encodedMessage.length(); i++) {
			char c = encodedMessage.charAt(i);
			Double val = frequencyMap.get(new Character(c));
			if (val != null) {
				frequencyMap.put(c, new Double(val + 1));
			} else {
				frequencyMap.put(c, (double) 1);
			}
		}
		
		for (char c : frequencyMap.keySet()) {
			frequencyMap.put(c, (frequencyMap.get(c) / encodedMessage.length()) * 100);
		}
		String str = "";
		for (int i = 0; i < Application.ALPHABET.length(); i++) {
			if (!frequencyMap.containsKey(Application.ALPHABET.charAt(i))) {
				str += Application.ALPHABET.charAt(i);
			}
		}
		for (int i = 0; i < str.length(); i++) {
			frequencyMap.put(str.charAt(i), (double) 0);
		}
		return frequencyMap;
	}
	
	public String deleteCharacters(String text, String charaters) {
		for (int i = 0; i < charaters.length(); i++) {
			text = text.replace(charaters.charAt(i), ' ');
		}
		text = text.replaceAll(" ", "");
		
		return text;
	}
}
