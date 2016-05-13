package protocolsupport.protocol.packet.middle.clientbound.play;

import java.io.IOException;

import net.minecraft.server.v1_9_R2.BlockPosition;
import protocolsupport.protocol.packet.middle.ClientBoundMiddlePacket;
import protocolsupport.protocol.serializer.PacketDataSerializer;

public abstract class MiddleUseBed<T> extends ClientBoundMiddlePacket<T> {

	protected int entityId;
	protected BlockPosition bed;

	@Override
	public void readFromServerData(PacketDataSerializer serializer) throws IOException {
		entityId = serializer.readVarInt();
		bed = serializer.readPosition();
	}

	@Override
	public void handle() {
	}

}
