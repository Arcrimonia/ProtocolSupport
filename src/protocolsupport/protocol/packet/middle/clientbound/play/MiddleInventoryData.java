package protocolsupport.protocol.packet.middle.clientbound.play;

import java.io.IOException;

import protocolsupport.protocol.packet.middle.ClientBoundMiddlePacket;
import protocolsupport.protocol.serializer.PacketDataSerializer;

public abstract class MiddleInventoryData<T> extends ClientBoundMiddlePacket<T> {

	protected int windowId;
	protected int type;
	protected int value;

	@Override
	public void readFromServerData(PacketDataSerializer serializer) throws IOException {
		windowId = serializer.readUnsignedByte();
		type = serializer.readShort();
		value = serializer.readShort();
	}

}
