package protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v1_5_1_6_1_7;

import java.util.Collection;
import java.util.Collections;

import protocolsupport.api.ProtocolVersion;
import protocolsupport.protocol.ClientBoundPacket;
import protocolsupport.protocol.PacketDataSerializer;
import protocolsupport.protocol.transformer.middlepacket.clientbound.play.MiddleBlockChangeMulti;
import protocolsupport.protocol.transformer.middlepacketimpl.PacketData;
import protocolsupport.protocol.transformer.middlepacketimpl.SupportedVersions;
import protocolsupport.protocol.typeremapper.id.IdRemapper;
import protocolsupport.protocol.typeremapper.id.RemappingTable;
import protocolsupportbuildprocessor.annotations.NeedsNoArgConstructor;

@NeedsNoArgConstructor
@SupportedVersions({ProtocolVersion.MINECRAFT_1_7_10, ProtocolVersion.MINECRAFT_1_7_5, ProtocolVersion.MINECRAFT_1_6_4, ProtocolVersion.MINECRAFT_1_6_2, ProtocolVersion.MINECRAFT_1_5_2})
public class BlockChangeMulti extends MiddleBlockChangeMulti<Collection<PacketData>> {

	@Override
	public Collection<PacketData> toData(ProtocolVersion version) {
		RemappingTable remapper = IdRemapper.BLOCK.getTable(version);
		PacketDataSerializer serializer = PacketDataSerializer.createNew(version);
		serializer.writeInt(chunkX);
		serializer.writeInt(chunkZ);
		serializer.writeShort(records.length);
		serializer.writeInt(records.length * 4);
		for (Record record : records) {
			serializer.writeShort(record.coord);
			int id = record.id;
			serializer.writeShort((remapper.getRemap(id >> 4) << 4) | (id & 0xF));
		}
		return Collections.singletonList(new PacketData(ClientBoundPacket.PLAY_BLOCK_CHANGE_MULTI_ID, serializer));
	}

}
