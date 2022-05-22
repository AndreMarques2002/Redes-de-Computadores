import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

// Biblioteca Gson
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class Sender{
	
	public static void main(String args[]) throws Exception {
		
		int nextSeqNum = 1;
		while(true) {
			
			DatagramSocket senderSocket = new DatagramSocket();
			
			// IP do Sender
			InetAddress IPAddress = InetAddress.getByName("127.0.0.1");
			
			// Mensagem do teclado
			System.out.println("Mensagem:");
			BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
	    	String mensagem;  
	        mensagem = inFromUser.readLine();			
			
	        // Buffer da mensagem enviada
	        byte[] sendMensagem = new byte[1024];
	        sendMensagem = mensagem.getBytes();
			DatagramPacket sendMensagemPacket = new DatagramPacket(sendMensagem, sendMensagem.length, IPAddress, 9876);
	        
			// Tipo de envio da mensagem
	        System.out.println("Digite a opção de envio que deseja: \n"
	        		+ " Lento \n Perda \n Fora de ordem \n"
	        		+ " Duplicada \n Normal");
	        String opcao;  
	        opcao = inFromUser.readLine();
	        
	        // Buffer do tipo de mensagem enviada
	        byte[] sendTipoMensagem = new byte[1024];
	        sendTipoMensagem = opcao.getBytes();
			DatagramPacket sendTipoPacket = new DatagramPacket(sendTipoMensagem, sendTipoMensagem.length, IPAddress, 9876);
	        
	        // Buffer do ID
	        DatagramSocket IDSocket = new DatagramSocket();
	        byte[] sendID = new byte[1024];
	        sendID = Integer.toString(nextSeqNum).getBytes();
			DatagramPacket sendIDPacket = new DatagramPacket(sendID, sendID.length, IPAddress, 9876);
			
			
			// Analisando a opção de envio selecionanda
			if(opcao.equalsIgnoreCase("Perda")) {
				// Enviando a mensagem (opção Perda)
				opcao = "Perda";
				
				// Sem envio da mensagem
				
				int a = nextSeqNum;
				// tamanho da janela igual a 4
				int j = 4;
				int p = 0;
				while (a>0){	
					// Quando o temporizador der timeout e não receber o reconhecimento:
					System.out.println("Timeout. Reenviando pacote " + a + "  ");
					
					p++;
					if(p==j) {
						break;
					}
					a--;
				}
				
				senderSocket.send(sendIDPacket);
				senderSocket.send(sendTipoPacket);
				senderSocket.send(sendMensagemPacket);
				
					
			}
			
			else {
				
				if(opcao.equalsIgnoreCase("Lento")){
					// Enviando a mensagem (opção Lento)
					opcao = "Lento";
					
					// Lentidão de 10 segundos para envio da mensagem
					Thread.sleep(10000);
					senderSocket.send(sendIDPacket);
					senderSocket.send(sendTipoPacket);
					senderSocket.send(sendMensagemPacket);
					
					// Exibindo a mensagem capturada e o tipo de envio selecionado
					System.out.println("Mensagem \""+ mensagem +"\" enviada como " + opcao +" com id " + nextSeqNum);
				}
				
				else if(opcao.equalsIgnoreCase("Fora de ordem")){
					opcao = "Fora de ordem";
					// Enviando a mensagem (opção Fora de ordem)
					senderSocket.send(sendIDPacket);
					senderSocket.send(sendTipoPacket);
					senderSocket.send(sendMensagemPacket);
					
					// Exibindo a mensagem capturada e o tipo de envio selecionado
					System.out.println("Mensagem \""+ mensagem +"\" enviada como " + opcao +" com id " + nextSeqNum);
				}
				
				else if(opcao.equalsIgnoreCase("Duplicada")){
					// Enviando a mensagem (opção Duplicada)
					opcao = "Duplicada";
					senderSocket.send(sendIDPacket);
					senderSocket.send(sendTipoPacket);
					senderSocket.send(sendMensagemPacket);
					
					senderSocket.send(sendIDPacket);
					senderSocket.send(sendTipoPacket);
					senderSocket.send(sendMensagemPacket);
					
					// Exibindo a mensagem capturada e o tipo de envio selecionado
					System.out.println("Mensagem \""+ mensagem +"\" enviada como " + opcao +" com id " + nextSeqNum);
				}
				
				else{
					// Enviando a mensagem (opção Normal)
					opcao = "Normal";
					senderSocket.send(sendIDPacket);
					senderSocket.send(sendTipoPacket);
					senderSocket.send(sendMensagemPacket);
					
					// Exibindo a mensagem capturada e o tipo de envio selecionado
					System.out.println("Mensagem \""+ mensagem +"\" enviada como " + opcao +" com id " + nextSeqNum);
				}
				
			}
			

			
			// Recebendo o reconhecimento do receiver
			byte[] recReconhecimentoBuffer = new byte[1024];
			DatagramPacket recPkt = new DatagramPacket(recReconhecimentoBuffer, recReconhecimentoBuffer.length);
			senderSocket.receive(recPkt);
			
			// Recebendo o ACK
			byte[] recACKBuffer = new byte[1024];
			DatagramPacket recACKPkt = new DatagramPacket(recACKBuffer, recACKBuffer.length);
			senderSocket.receive(recACKPkt);
			String recACK = new String(recACKPkt.getData(), recACKPkt.getOffset(), recACKPkt.getLength());
			
			//Após receber o reconhecimento do receiver:
			System.out.println("Mensagem id "+ nextSeqNum + " recebida pelo receiver");
			
			
			// Criando o objeto mensagem
			Mensagem mensagemInfo = new Mensagem();
			
			int DESTport = 9876;
			int SRCport = 9876;
			
			//mensagemInfo.srcPort =  SRCport;
			mensagemInfo.destPort = SRCport;
			mensagemInfo.ID = nextSeqNum;
			mensagemInfo.ACK = recACK;
			mensagemInfo.DATA = mensagem;
			
			// Convertendo a mensagem para JSON
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String jsonString = gson.toJson(mensagemInfo);
			System.out.println(jsonString);
			
			
			// Enviando a mensagem no formato UDP
			byte[] sendMensagemUDPBuffer = new byte[1024];
			sendMensagemUDPBuffer = jsonString.getBytes();
			DatagramPacket sendMensagemUDPPkt = new DatagramPacket(sendMensagemUDPBuffer, sendMensagemUDPBuffer.length, IPAddress, 9876);
			senderSocket.send(sendMensagemUDPPkt);
			
			
			nextSeqNum++;
			}
		}
}