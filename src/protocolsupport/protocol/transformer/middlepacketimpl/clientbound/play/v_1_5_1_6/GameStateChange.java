package protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v_1_5_1_6;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import protocolsupport.api.ProtocolVersion;
import protocolsupport.protocol.ClientBoundPacket;
import protocolsupport.protocol.PacketDataSerializer;
import protocolsupport.protocol.transformer.middlepacket.clientbound.play.MiddleGameStateChange;
import protocolsupport.protocol.transformer.middlepacketimpl.PacketData;
import protocolsupport.protocol.transformer.middlepacketimpl.SupportedVersions;
import protocolsupportbuildprocessor.annotations.NeedsNoArgConstructor;

@NeedsNoArgConstructor
@SupportedVersions({ProtocolVersion.MINECRAFT_1_6_4, ProtocolVersion.MINECRAFT_1_6_2, ProtocolVersion.MINECRAFT_1_5_2})
public class GameStateChange extends MiddleGameStateChange<Collection<PacketData>> {

	@Override
	public Collection<PacketData> toData(ProtocolVersion version) throws IOException {
		switch (type) {
			case 1: {
				type = 2;
				break;
			}
			case 2: {
				type = 1;
				break;
			}
			default: {
				break;
			}
		}
		PacketDataSerializer serializer = PacketDataSerializer.createNew(version);
		serializer.writeByte(type);
		serializer.writeByte((int) value);
		return Collections.singletonList(new PacketData(ClientBoundPacket.PLAY_GAME_STATE_CHANGE_ID, serializer));
	}

}
