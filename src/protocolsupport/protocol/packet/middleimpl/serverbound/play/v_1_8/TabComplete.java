package protocolsupport.protocol.packet.middleimpl.serverbound.play.v_1_8;

import java.io.IOException;

import protocolsupport.protocol.packet.middle.serverbound.play.MiddleTabComplete;
import protocolsupport.protocol.serializer.PacketDataSerializer;

public class TabComplete extends MiddleTabComplete {

	@Override
	public void readFromClientData(PacketDataSerializer serializer) throws IOException {
		string = serializer.readString(Short.MAX_VALUE);
		if (serializer.readBoolean()) {
			position = serializer.readPosition();
		}
	}

}
