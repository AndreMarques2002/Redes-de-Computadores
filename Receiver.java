import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class Receiver{
	
	public static void main(String args[]) throws Exception {
		
		int c = 0;
		int ack = 1;
		
		try {
			
			// Cria um datagrama
			DatagramSocket receiverSocket = new DatagramSocket(9876);
			
			while(true) {

				
				// Avalia o ID da mensagem recebida
				byte[] recIDBuffer = new byte[1024];
				DatagramPacket recIDPkt = new DatagramPacket(recIDBuffer, recIDBuffer.length);
				receiverSocket.receive(recIDPkt);
				String mensagemID = new String(recIDPkt.getData(), recIDPkt.getOffset(), recIDPkt.getLength());
				
				
				// Avalia o tipo de mensagem recebida (duplicada, lenta etc.)
				byte[] recEnvioBuffer = new byte[1024];
				InetAddress IPAddress = recIDPkt.getAddress();
				int port = recIDPkt.getPort();
				DatagramPacket recEnvioPkt = new DatagramPacket(recEnvioBuffer, recEnvioBuffer.length);
				receiverSocket.receive(recEnvioPkt);
				String envioTipo = new String(recEnvioPkt.getData(), recEnvioPkt.getOffset(), recEnvioPkt.getLength());
				
				
				if(envioTipo.equalsIgnoreCase("Duplicada")) {
					
					if(c==0) {
						System.out.println("Mensagem id " + mensagemID + " recebida de forma duplicada");
						
						byte[] recMensagemBuffer = new byte[1024];
						DatagramPacket recMensagemPkt = new DatagramPacket(recMensagemBuffer, recMensagemBuffer.length);
						receiverSocket.receive(recMensagemPkt);
	
						
						// Confirmando o recebimento
						byte[] sendBuf = new byte[1024];
						DatagramPacket recPacket =	new DatagramPacket(sendBuf, sendBuf.length, IPAddress, port);
						receiverSocket.send(recPacket);
						
						
						// Enviando o ACK
				        byte[] sendACK = new byte[1024];
				        sendACK = Integer.toString(ack).getBytes();
						DatagramPacket sendACKPacket = new DatagramPacket(sendACK, sendACK.length, IPAddress, port);
						receiverSocket.send(sendACKPacket);
						
						ack++;
						c++;
					}
					else {
						// Recebendo a mensagem no formato UDP
						//byte[] recMensagemUDPBuffer = new byte[1024];;
						//DatagramPacket recMensagemUDPPacket =	new DatagramPacket(recMensagemUDPBuffer, recMensagemUDPBuffer.length, IPAddress, port);
						//receiverSocket.receive(recMensagemUDPPacket);
						
						byte[] recMensagemBuffer = new byte[1024];
						DatagramPacket recMensagemPkt = new DatagramPacket(recMensagemBuffer, recMensagemBuffer.length);
						receiverSocket.receive(recMensagemPkt);
						c=0;
					}
					
				}
				else {
					if(envioTipo.equalsIgnoreCase("Fora de ordem")) {
						System.out.println("Mensagem id " + mensagemID + " recebida fora de ordem, ainda não recebidos os identificadores [" + mensagemID + "]");
						c=0;
					}
					else if(envioTipo.equalsIgnoreCase("Lento")) {
						System.out.println("Mensagem id " + mensagemID + " recebida na ordem, entregando para a camada de aplicação");
						c=0;
					}
					else if(envioTipo.equalsIgnoreCase("Normal")) {
						System.out.println("Mensagem id " + mensagemID + " recebida na ordem, entregando para a camada de aplicação");
						c=0;
					}
					else {
						// Perda (após o reenvio)
						System.out.println("Mensagem id " + mensagemID + " recebida na ordem, entregando para a camada de aplicação");
						c=0;
					}
					
					
					// Avalia a mensagem recebida
					byte[] recMensagemBuffer = new byte[1024];
					DatagramPacket recMensagemPkt = new DatagramPacket(recMensagemBuffer, recMensagemBuffer.length);
					receiverSocket.receive(recMensagemPkt);

					
					// Confirmando o recebimento
					byte[] sendBuf = new byte[1024];
					DatagramPacket recPacket =	new DatagramPacket(sendBuf, sendBuf.length, IPAddress, port);
					receiverSocket.send(recPacket);
					
					
					// Enviando o ACK
			        	byte[] sendACK = new byte[1024];
			        	sendACK = Integer.toString(ack).getBytes();
					DatagramPacket sendACKPacket = new DatagramPacket(sendACK, sendACK.length, IPAddress, port);
					receiverSocket.send(sendACKPacket);
					
					ack++;
					
					
					// Recebendo a mensagem no formato UDP
					//byte[] recMensagemUDPBuffer = new byte[1024];;
					//DatagramPacket recMensagemUDPPacket =	new DatagramPacket(recMensagemUDPBuffer, recMensagemUDPBuffer.length, IPAddress, port);
					//receiverSocket.receive(recMensagemUDPPacket);
				}
				
				
				
				
			}
			
			
		} catch (Exception e) {
			
		}
		
	
	
	}
}