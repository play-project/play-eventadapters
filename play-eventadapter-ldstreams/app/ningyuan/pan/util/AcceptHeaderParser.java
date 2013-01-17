package ningyuan.pan.util;

/**
 * @author Ningyuan Pan
 *
 */

public class AcceptHeaderParser {
	
	private int index = 0;
	private int temp = -1;
	private char[] line;
	private boolean error = false;
	
	private String header;
	
	private final AcceptHeaderRegister register;
	
	public AcceptHeaderParser(AcceptHeaderRegister r){
		if(r == null)
			throw new IllegalArgumentException();
		else
			register = r;
	}
	
	public void parse(String accept){
		if(accept == null){
			return;
		}
		else{
				System.out.println("ACCEPT: "+accept);
			line = accept.toCharArray();
			header =accept;
				
			while(!error && index < line.length){
				if(line[index] == ' '){
					index++;
				}
				else if(isLetter(line[index]) || line[index] == '*'){
					getMediaRange();
				}
				else if(line[index] == ';'){
					System.out.println("error "+index);
					error = true;
				}
				else if(line[index] == ','){
					System.out.println("error "+index);
					error = true;
				}
				else{
					System.out.println("error "+index);
					error = true;
				}
			}
		}
	}	
	
	public AcceptHeaderRegister getRegister(){
		return register;
	}
	
	/*
	 * parse the part of media-range in accept header.
	 * 
	 * media-range = ( "* / *" | (type "/" "*")
	 *                 | (type "/" subtype)   )
	 *                *(";" parameter)
	 */
	private void getMediaRange(){
		boolean end = false;
		int state = 0;
		temp = index;
		
		while(!error && !end && index < line.length){
			switch(state){
				// type
				case 0:{
					if(line[index] == '/'){
						state = 1;
							System.out.print(header.substring(temp, index)+" / ");
						register.registerType(header.substring(temp, index));
						index++;
						break;
					}
					else{
						index++;
						break;
					}
				}
				// after /
				case 1:{
					if(line[index] == ' '){
						index++;
						break;
					}
					if(line[index] == ','){
						System.out.println("error "+index);
						error = true;
						break;
					}
					else if(line[index] == ';'){
						System.out.println("error "+index);
						error = true;
						break;
					}
					else{
						state = 2;
						// do not ++, index stay at the first letter of subtype
						temp = index;
						break;
					}
				}
				// subtype
				case 2:{
					if(line[index] == ','){
						//default quality q = 1
						end = true;
							System.out.print(header.substring(temp, index)+" q=default\n");
						register.registerSubtype(header.substring(temp, index));
						register.registerQuality(10);
						index++;
						break;
					}
					else if(line[index] == ';'){
						state = 3;
							System.out.print(header.substring(temp, index)+" ");
						register.registerSubtype(header.substring(temp, index));
						index++;
						break;
					}
					else{
						index++;
						break;
					}
				}
				// after ;
				case 3:{
					if(line[index] == 'q'){
						temp = index;
						state = 4;
						index++;
						break;
					}
					else if(line[index] == ' '){
						index++;
						break;
					}
					else if(line[index] == ','){
						System.out.println("error "+index);
						error = true;
						break;
					}
					else if(line[index] == ';'){
						System.out.println("error "+index);
						error = true;
						break;
					}
					else{
						state = 5;
						//do not ++, index stay at the first letter of param name
							temp = index;
						break;
					}
				}
				// after ;q
				case 4:{
					if(line[index] == ' '){
						index++;
						break;
					}
					else if(line[index] == '='){
						end = true;
						index++;
						getAcceptParams();
						break;
					}
					else if(line[index] == ','){
						System.out.println("error "+index);
						error = true;
						break;
					}
					else if(line[index] == ';'){
						System.out.println("error "+index);
						error = true;
						break;
					}
					else{
						state = 5;
						// set index back to 'q', the first letter of param value
						index = temp;
						break;
					}
				}
				// param name
				case 5:{
					if(line[index] == '='){
						state = 6;
							System.out.print(header.substring(temp, index)+" = ");
						register.registerParamName(header.substring(temp, index));
						index++;
							
						break;
					}
					else if(line[index] == ','){
						System.out.println("error "+index);
						error = true;
						break;
					}
					else if(line[index] == ';'){
						System.out.println("error "+index);
						error = true;
						break;
					}
					else{
						index++;
						break;
					}
				}
				// after =
				case 6:{
					if(line[index] == ' '){
						index++;
						break;
					}
					else if(line[index] == ','){
						System.out.println("error "+index);
						error = true;
						break;
					}
					else if(line[index] == ';'){
						System.out.println("error "+index);
						error = true;
						break;
					}
					else{
						state = 7;
						//do not ++, index stay at the first letter of param value
						temp = index;
						break;
					}
				}
				// param value
				case 7:{
					if(line[index] == ','){
						// default quality q = 1
						end = true;
							System.out.print(header.substring(temp, index)+" q=default\n");
						register.registerParamValue(header.substring(temp, index));
						register.registerQuality(10);
						index++;
							
						break;
					}
					else if(line[index] == ';'){
						state = 3;
							System.out.print(header.substring(temp, index)+" ");
						register.registerParamValue(header.substring(temp, index));
						index++;
						break;
					}
					else{
						index++;
						break;
					}
				}
			}
		}
		
		// handle the rest part of token
		if(index == line.length){
			switch(state){
				case 0:{
						System.out.print(header.substring(temp, index)+" <incomplete type>\n");
					break;
				}
				case 1:{
						System.out.print("<incomplete subtype>\n");
					break;
				}
				case 2:{
					if(end == true){
							System.out.print("<useless ,>\n");
						break;
					}
					else{
							System.out.print(header.substring(temp, index)+" q=default\n");
						register.registerSubtype(header.substring(temp, index));
						register.registerQuality(10);
						break;
					}
				}
				case 3:{
						System.out.print("<lack of param name>\n");
					break;
				}
				case 4:{
					if(!end){
							System.out.print(header.substring(temp, index)+" <incomplete param name>\n");
					}
					break;
				}
				case 5:{
						System.out.print(header.substring(temp, index)+" <incomplete param name or lack of param value>\n");
					break;
				}
				case 6:{
						System.out.print("<lack of param value>\n");
						break;
				}
				case 7:{
					if(end == true){
							System.out.print("<useless ,>\n");
						break;
					}
					else{
							System.out.print(header.substring(temp, index)+" q=default\n");
						register.registerQuality(10);
						break;
					}
				}
			}
		}
	}
	
	/*
	 * parse the part of accept-params in accpet header
	 * 
	 * accept-params = ";" "q" "=" qvalue 
	 */
	private void getAcceptParams(){
		boolean end = false;
		int state = 0;
		temp = index;
		
		while(!error && !end && index < line.length){
			switch(state){
				// after q =
				case 0:{
					if(line[index] == ' '){
						index++;
						break;
					}
					else if(line[index] == '0'){
						temp = index;
						state = 1;
						index++;
						break;
					}
					else if(line[index] == '1'){
						temp = index;
						state = 2;
						index++;
						break;
					}
					else{
						System.out.println("error "+index);
						error = true;
						break;
					}
				}
				// after q = 0
				case 1:{
					if(line[index] == '.'){
						state = 3;
						index++;
						break;
					}
					else if(line[index] == ' '){
						state = 5;
						index++;
						break;
					}
					else if(line[index] == ','){
						end = true;
							System.out.print("q="+header.substring(temp, index)+"\n");
						int quantity = Integer.valueOf(header.substring(temp, index).trim());
						register.registerQuality(quantity);
						index++;
						break;
					}
					else{
						System.out.println("error "+index);
						error = true;
						break;
					}
				}
				// after q = 1
				case 2:{
					if(line[index] == ' '){
						index++;
						break;
					}
					else if(line[index] == ','){
						end = true;
							System.out.print("q="+header.substring(temp, index)+"\n");
						int quantity = Integer.valueOf(header.substring(temp, index).trim());
						register.registerQuality(quantity * 10);
						index++;
						break;
					}
					else{
						System.out.println("error "+index);
						error = true;
						break;
					}
				}
				// after q = 0.
				case 3:{
					if(isDigit(line[index])){
						state = 4;
						index++;
						break;
					}
					else{
						System.out.println("error "+index);
						error = true;
						break;
					}
				}
				// after q = 0.x
				case 4:{
					if(line[index] == ' '){
						index++;
						break;
					}
					else if(line[index] == ','){
						end = true;
							System.out.print("q="+header.substring(temp, index)+"\n");
						int quantity = Integer.valueOf(header.substring(temp+2, temp+3));
						register.registerQuality(quantity);
						index++;
						break;
					}
					else{
						System.out.println("error "+index);
						error = true;
						break;
					}
				}
				// after q = 0 blank
				case 5:{
					if(line[index] == ' '){
						index++;
						break;
					}
					else if(line[index] == ','){
						end = true;
							System.out.print("q="+header.substring(temp, index)+"\n");
						int quantity = Integer.valueOf(header.substring(temp, index).trim());
						register.registerQuality(quantity);
						index++;
						break;
					}
					else{
						System.out.println("error "+index);
						error = true;
						break;
					}
				}
			}
		}
		
		// handle the rest part of token
		if(index == line.length){
			switch(state){
				case 0:{
					System.out.print("q= <incomplete quantity>\n");
					break;
				}
				// after q=0
				case 1:{
					if(end){
							System.out.print("<useless ,>\n");
						break;
					}
					else{
							System.out.print("q="+header.substring(temp, index)+"\n");
						int quantity = Integer.valueOf(header.substring(temp, index).trim());
						register.registerQuality(quantity);
						break;
					}
				}
				// after q=1
				case 2:{
					if(end){
							System.out.print("<useless ,>\n");
						break;
					}
					else{
							System.out.print("q="+header.substring(temp, index)+"\n");
						int quantity = Integer.valueOf(header.substring(temp, index).trim());
						register.registerQuality(quantity * 10);
						break;
					}
				}
				case 3:{
						System.out.println("q="+header.substring(temp,index)+" <incomplete quantity>\n");
					break;
				}
				// after q=0.x
				case 4:{
					if(end){
							System.out.print("<useless ,>\n");
						break;
					}
					else{
							System.out.print("q="+header.substring(temp, index)+"\n");
						int quantity = Integer.valueOf(header.substring(temp+2, temp+3));
						register.registerQuality(quantity);
						break;
					}
				}
				// after q=0 blank
				case 5:{
					if(end){
						System.out.print("<useless ,>\n");
						break;
					}
					else{
							System.out.print("q="+header.substring(temp, index)+"\n");
						int quantity = Integer.valueOf(header.substring(temp, index).trim());
						register.registerQuality(quantity);
						break;
					}
				}
			}
		}
	}
	
	private boolean isLetter(char c){
		if((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))
			return true;
		else
			return false;
	}
	
	private boolean isDigit(char c){
		if(c >= '0' && c <= '9')
			return true;
		else
			return false;
	}
}
