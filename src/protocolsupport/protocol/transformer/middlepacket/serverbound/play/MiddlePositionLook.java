package protocolsupport.protocol.transformer.middlepacket.serverbound.play;

import net.minecraft.server.v1_9_R1.Packet;
import protocolsupport.protocol.ServerBoundPacket;
import protocolsupport.protocol.transformer.middlepacket.ServerBoundMiddlePacket;
import protocolsupport.protocol.transformer.middlepacketimpl.PacketCreator;
import protocolsupport.utils.recyclable.RecyclableCollection;
import protocolsupport.utils.recyclable.RecyclableEmptyList;
import protocolsupport.utils.recyclable.RecyclableSingletonList;

public abstract class MiddlePositionLook extends ServerBoundMiddlePacket {

	protected double x;
	protected double y;
	protected double z;
	protected float yaw;
	protected float pitch;
	protected boolean onGround;

	@Override
	public RecyclableCollection<? extends Packet<?>> toNative() throws Exception {
		if (!sharedstorage.isTeleportConfirmNeeded()) {
			PacketCreator creator = PacketCreator.create(ServerBoundPacket.PLAY_POSITION_LOOK.get());
			creator.writeDouble(x);
			creator.writeDouble(y);
			creator.writeDouble(z);
			creator.writeFloat(yaw);
			creator.writeFloat(pitch);
			creator.writeBoolean(onGround);
			return RecyclableSingletonList.create(creator.create());
		} else {
			int teleportId = sharedstorage.tryTeleportConfirm(x, y, z);
			if (teleportId == -1) {
				return RecyclableEmptyList.get();
			} else {
				PacketCreator creator = PacketCreator.create(ServerBoundPacket.PLAY_TELEPORT_ACCEPT.get());
				creator.writeVarInt(teleportId);
				return RecyclableSingletonList.create(creator.create());
			}
		}
	}

}
