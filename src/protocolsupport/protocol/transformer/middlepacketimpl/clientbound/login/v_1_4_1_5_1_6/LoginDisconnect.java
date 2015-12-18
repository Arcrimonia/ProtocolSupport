package protocolsupport.protocol.transformer.middlepacketimpl.clientbound.login.v_1_4_1_5_1_6;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import protocolsupport.api.ProtocolVersion;
import protocolsupport.protocol.ClientBoundPacket;
import protocolsupport.protocol.PacketDataSerializer;
import protocolsupport.protocol.transformer.middlepacket.clientbound.login.MiddleLoginDisconnect;
import protocolsupport.protocol.transformer.middlepacketimpl.PacketData;
import protocolsupport.protocol.transformer.utils.LegacyUtils;
import protocolsupportbuildprocessor.annotations.NeedsNoArgConstructor;

@NeedsNoArgConstructor
public class LoginDisconnect extends MiddleLoginDisconnect<Collection<PacketData>> {

	@Override
	public Collection<PacketData> toData(ProtocolVersion version) throws IOException {
		PacketDataSerializer serializer = PacketDataSerializer.createNew(version);
		serializer.writeString(LegacyUtils.toText(ChatSerializer.a(messageJson)));
		return Collections.singletonList(new PacketData(ClientBoundPacket.LOGIN_DISCONNECT_ID, serializer));
	}

}
