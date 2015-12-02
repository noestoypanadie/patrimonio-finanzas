package es.vdiaz.patrimionio.finanzas.exec;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;

/**
 * Tests que prueban la funcionalidad del servicio de acceso a Pubmed
 * 
 * @author Juanma
 * 
 */
public class CorrigeHST {

	public static void main(String[] args) throws Exception {
		CorrigeHST obj = new CorrigeHST();
		if (args.length == 2) {
			String pathInput = args[0];
			String pathOutput = args[1];
			obj.procesa(pathInput, pathOutput);
		} else {
			System.err.println("NECESITO DOS PARAMETROS: fichero de entrada y fichero de salida");
		}
	}

	public void procesa(String pathInput, String pathOutput) throws Exception {

		DataInputStream dis = new DataInputStream(new FileInputStream(new File(pathInput)));
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File(pathOutput)));
		try {
			MtVersion version = new CorrigeHST.MtVersion(dis);
			version.toOutput(dos);
			System.out.println(version);

			Data lastData = null;
			int contador = 0;
			while (true) {
				Data data = new Data(dis);
				if (lastData != null && data.incoherente(lastData)) {
					System.out.println("DATA INCOHERENTE");
					data = lastData;
				}
				System.out.println(data);
				data.toOutput(dos);
				lastData = data;
				contador++;

				// if (contador == 10000) {
				// dos.flush();
				// dos.close();
				// break;
				// }
			}
		} catch (Exception e) {
			dos.flush();
			dos.close();
		}
	}

	// private static Date readDate(DataInputStream dis) throws IOException {
	// int i = readInt(dis);
	// long il = (long) i * 1000;
	// return new Date(il);
	// }

	// private static int readInt(DataInputStream dis) throws IOException {
	// int byte1 = dis.readUnsignedByte();
	// int byte2 = dis.readUnsignedByte();
	// int byte3 = dis.readUnsignedByte();
	// int byte4 = dis.readUnsignedByte();
	//
	// byte[] arr = { (byte) byte4, (byte) byte3, (byte) byte2, (byte) byte1 };
	// ByteBuffer wrapped = ByteBuffer.wrap(arr); // big-endian by default
	// return wrapped.getInt(); // 1
	// }

	public class MtVersion {
		MtInt version;
		byte[] copyrightB = new byte[64];
		byte[] symbolB = new byte[12];
		String copyright;
		String symbol;
		MtInt period;
		MtInt digits;
		MtDate timesign;
		MtInt lastSync;
		byte[] bytes52 = new byte[52];

		public MtVersion(DataInputStream dis) throws IOException {
			version = new MtInt(dis);
			dis.read(copyrightB);
			copyright = new String(copyrightB);
			dis.read(symbolB);
			symbol = new String(symbolB);
			period = new MtInt(dis);
			digits = new MtInt(dis);
			timesign = new MtDate(dis);
			lastSync = new MtInt(dis);
			dis.read(bytes52);
		}

		@Override
		public String toString() {
			return (version.value + " | " + copyright + " | " + symbol + " | " + period.value + " | " + digits.value + " | " + timesign.value + " | " + lastSync.value);
		}

		public void toOutput(DataOutputStream dos) throws IOException {
			version.toOutput(dos);
			dos.write(copyrightB);
			dos.write(symbolB);
			period.toOutput(dos);
			digits.toOutput(dos);
			timesign.toOutput(dos);
			lastSync.toOutput(dos);
			dos.write(bytes52);
		}
	}

	public class MtInt {
		int byte1;
		int byte2;
		int byte3;
		int byte4;

		int value;

		public MtInt(DataInputStream dis) throws IOException {
			byte1 = dis.readUnsignedByte();
			byte2 = dis.readUnsignedByte();
			byte3 = dis.readUnsignedByte();
			byte4 = dis.readUnsignedByte();

			byte[] arr = { (byte) byte4, (byte) byte3, (byte) byte2, (byte) byte1 };
			ByteBuffer wrapped = ByteBuffer.wrap(arr); // big-endian by default
			value = wrapped.getInt();
		}

		public void toOutput(DataOutputStream dos) throws IOException {
			dos.write(byte1);
			dos.write(byte2);
			dos.write(byte3);
			dos.write(byte4);
		}
	}

	public class MtDate {
		int byte1;
		int byte2;
		int byte3;
		int byte4;

		Date value;

		public MtDate(DataInputStream dis) throws IOException {
			byte1 = dis.readUnsignedByte();
			byte2 = dis.readUnsignedByte();
			byte3 = dis.readUnsignedByte();
			byte4 = dis.readUnsignedByte();

			byte[] arr = { (byte) byte4, (byte) byte3, (byte) byte2, (byte) byte1 };
			ByteBuffer wrapped = ByteBuffer.wrap(arr); // big-endian by default
			long il = (long) wrapped.getInt() * 1000;
			value = new Date(il);
		}

		public void toOutput(DataOutputStream dos) throws IOException {
			dos.write(byte1);
			dos.write(byte2);
			dos.write(byte3);
			dos.write(byte4);
		}
	}

	public class MtDouble {
		int byte1;
		int byte2;
		int byte3;
		int byte4;
		int byte5;
		int byte6;
		int byte7;
		int byte8;

		double value;

		public MtDouble(DataInputStream dis) throws IOException {
			byte1 = dis.readUnsignedByte();
			byte2 = dis.readUnsignedByte();
			byte3 = dis.readUnsignedByte();
			byte4 = dis.readUnsignedByte();
			byte5 = dis.readUnsignedByte();
			byte6 = dis.readUnsignedByte();
			byte7 = dis.readUnsignedByte();
			byte8 = dis.readUnsignedByte();

			byte[] arr = { (byte) byte8, (byte) byte7, (byte) byte6, (byte) byte5, (byte) byte4, (byte) byte3, (byte) byte2, (byte) byte1 };
			ByteBuffer wrapped = ByteBuffer.wrap(arr); // big-endian by default

			value = wrapped.getDouble(); // 1
		}

		public void toOutput(DataOutputStream dos) throws IOException {
			dos.write(byte1);
			dos.write(byte2);
			dos.write(byte3);
			dos.write(byte4);
			dos.write(byte5);
			dos.write(byte6);
			dos.write(byte7);
			dos.write(byte8);
		}

	}

	public class Data {
		MtDate ctm;
		MtDouble open;
		MtDouble low;
		MtDouble high;
		MtDouble close;
		MtDouble volume;

		int INCREMENTO_MAXIMO = 3;

		public Data(DataInputStream dis) throws IOException {
			ctm = new MtDate(dis);
			open = new MtDouble(dis);
			low = new MtDouble(dis);
			high = new MtDouble(dis);
			close = new MtDouble(dis);
			volume = new MtDouble(dis);
		}

		@Override
		public String toString() {
			return (ctm.value + " | " + open.value + " | " + low.value + " | " + high.value + " | " + close.value + " | " + volume.value);
		}

		public void toOutput(DataOutputStream dos) throws IOException {
			ctm.toOutput(dos);
			open.toOutput(dos);
			low.toOutput(dos);
			high.toOutput(dos);
			close.toOutput(dos);
			volume.toOutput(dos);
		}

		public boolean incoherente(Data lastData) {
			if (open.value > INCREMENTO_MAXIMO * lastData.open.value || open.value < lastData.open.value / INCREMENTO_MAXIMO) {
				return true;
			}
			if (low.value > INCREMENTO_MAXIMO * lastData.low.value || low.value < lastData.low.value / INCREMENTO_MAXIMO) {
				return true;
			}
			if (high.value > INCREMENTO_MAXIMO * lastData.high.value || high.value < lastData.high.value / INCREMENTO_MAXIMO) {
				return true;
			}
			if (close.value > INCREMENTO_MAXIMO * lastData.close.value || close.value < lastData.close.value / INCREMENTO_MAXIMO) {
				return true;
			}

			return false;
		}
	}
}
