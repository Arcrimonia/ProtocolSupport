package protocolsupport.protocol.packet.middle.clientbound.play;

import java.io.IOException;

import protocolsupport.protocol.packet.middle.ClientBoundMiddlePacket;
import protocolsupport.protocol.serializer.PacketDataSerializer;

public abstract class MiddlePlayerListHeaderFooter<T> extends ClientBoundMiddlePacket<T> {

	protected String headerJson;
	protected String footerJson;

	@Override
	public void readFromServerData(PacketDataSerializer serializer) throws IOException {
		headerJson = serializer.readString(Short.MAX_VALUE);
		footerJson = serializer.readString(Short.MAX_VALUE);
	}

}
