package protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v_1_4_1_5_1_6_1_7_1_8;

import java.io.IOException;

import net.minecraft.server.v1_9_R1.ItemStack;
import protocolsupport.api.ProtocolVersion;
import protocolsupport.protocol.ClientBoundPacket;
import protocolsupport.protocol.storage.SharedStorage.WindowType;
import protocolsupport.protocol.transformer.middlepacket.clientbound.play.MiddleInventorySetItems;
import protocolsupport.protocol.transformer.middlepacketimpl.PacketData;
import protocolsupport.utils.recyclable.RecyclableCollection;
import protocolsupport.utils.recyclable.RecyclableSingletonList;

public class InventorySetItems extends MiddleInventorySetItems<RecyclableCollection<PacketData>> {

	@Override
	public RecyclableCollection<PacketData> toData(ProtocolVersion version) throws IOException {
		if (windowId == 0) {
			itemstacks.remove(itemstacks.size() - 1);
		}
		if (version.isBefore(ProtocolVersion.MINECRAFT_1_9) && sharedstorage.getOpenedWindow() == WindowType.BREING) {
			itemstacks.remove(4);
		} else if (version.isBefore(ProtocolVersion.MINECRAFT_1_8) && sharedstorage.getOpenedWindow() == WindowType.ENCHANT) {
			itemstacks.remove(1);
		}
		PacketData serializer = PacketData.create(ClientBoundPacket.PLAY_WINDOW_SET_ITEMS_ID, version);
		serializer.writeByte(windowId);
		serializer.writeShort(itemstacks.size());
		for (ItemStack itemstack : itemstacks) {
			serializer.writeItemStack(itemstack);
		}
		return RecyclableSingletonList.create(serializer);
	}

}
