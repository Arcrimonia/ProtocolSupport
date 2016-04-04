package protocolsupport.protocol.transformer.middlepacketimpl.serverbound.play.v_1_8;

import java.io.IOException;

import protocolsupport.protocol.PacketDataSerializer;
import protocolsupport.protocol.transformer.middlepacket.serverbound.play.MiddleResourcePackStatus;

public class ResourcePackStatus extends MiddleResourcePackStatus {

	@Override
	public void readFromClientData(PacketDataSerializer serializer) throws IOException {
		hash = serializer.readString(40);
		result = serializer.readVarInt();
	}

}
