package protocolsupport.protocol.packet.v_1_8;

import java.util.List;

import org.spigotmc.SneakyThrow;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import net.minecraft.server.v1_9_R2.EnumProtocol;
import net.minecraft.server.v1_9_R2.Packet;
import protocolsupport.api.ProtocolVersion;
import protocolsupport.protocol.legacyremapper.LegacyAnimatePacketReorderer;
import protocolsupport.protocol.packet.middle.ServerBoundMiddlePacket;
import protocolsupport.protocol.packet.middleimpl.serverbound.handshake.v_1_7_1_8.SetProtocol;
import protocolsupport.protocol.packet.middleimpl.serverbound.login.v_1_4_1_5_1_6_1_7_1_8.EncryptionResponse;
import protocolsupport.protocol.packet.middleimpl.serverbound.login.v_1_7_1_8.LoginStart;
import protocolsupport.protocol.packet.middleimpl.serverbound.play.v_1_4_1_5_1_6_1_7_1_8.Chat;
import protocolsupport.protocol.packet.middleimpl.serverbound.play.v_1_4_1_5_1_6_1_7_1_8.CreativeSetSlot;
import protocolsupport.protocol.packet.middleimpl.serverbound.play.v_1_4_1_5_1_6_1_7_1_8.Flying;
import protocolsupport.protocol.packet.middleimpl.serverbound.play.v_1_4_1_5_1_6_1_7_1_8.HeldSlot;
import protocolsupport.protocol.packet.middleimpl.serverbound.play.v_1_4_1_5_1_6_1_7_1_8.InventoryClick;
import protocolsupport.protocol.packet.middleimpl.serverbound.play.v_1_4_1_5_1_6_1_7_1_8.InventoryClose;
import protocolsupport.protocol.packet.middleimpl.serverbound.play.v_1_4_1_5_1_6_1_7_1_8.InventoryEnchant;
import protocolsupport.protocol.packet.middleimpl.serverbound.play.v_1_4_1_5_1_6_1_7_1_8.InventoryTransaction;
import protocolsupport.protocol.packet.middleimpl.serverbound.play.v_1_4_1_5_1_6_1_7_1_8.Look;
import protocolsupport.protocol.packet.middleimpl.serverbound.play.v_1_6_1_7_1_8.PlayerAbilities;
import protocolsupport.protocol.packet.middleimpl.serverbound.play.v_1_7_1_8.ClientCommand;
import protocolsupport.protocol.packet.middleimpl.serverbound.play.v_1_8.Animation;
import protocolsupport.protocol.packet.middleimpl.serverbound.play.v_1_8.BlockDig;
import protocolsupport.protocol.packet.middleimpl.serverbound.play.v_1_8.BlockPlace;
import protocolsupport.protocol.packet.middleimpl.serverbound.play.v_1_8.ClientSettings;
import protocolsupport.protocol.packet.middleimpl.serverbound.play.v_1_8.CustomPayload;
import protocolsupport.protocol.packet.middleimpl.serverbound.play.v_1_8.EntityAction;
import protocolsupport.protocol.packet.middleimpl.serverbound.play.v_1_8.KeepAlive;
import protocolsupport.protocol.packet.middleimpl.serverbound.play.v_1_8.Position;
import protocolsupport.protocol.packet.middleimpl.serverbound.play.v_1_8.PositionLook;
import protocolsupport.protocol.packet.middleimpl.serverbound.play.v_1_8.ResourcePackStatus;
import protocolsupport.protocol.packet.middleimpl.serverbound.play.v_1_8.Spectate;
import protocolsupport.protocol.packet.middleimpl.serverbound.play.v_1_8.SteerVehicle;
import protocolsupport.protocol.packet.middleimpl.serverbound.play.v_1_8.TabComplete;
import protocolsupport.protocol.packet.middleimpl.serverbound.play.v_1_8.UpdateSign;
import protocolsupport.protocol.packet.middleimpl.serverbound.play.v_1_8.UseEntity;
import protocolsupport.protocol.packet.middleimpl.serverbound.status.v_1_7_1_8.Ping;
import protocolsupport.protocol.packet.middleimpl.serverbound.status.v_1_7_1_8.ServerInfoRequest;
import protocolsupport.protocol.pipeline.IPacketDecoder;
import protocolsupport.protocol.serializer.WrappingBufferPacketDataSerializer;
import protocolsupport.protocol.storage.SharedStorage;
import protocolsupport.protocol.utils.registry.MiddleTransformerRegistry;
import protocolsupport.protocol.utils.registry.MiddleTransformerRegistry.InitCallBack;
import protocolsupport.utils.netty.ChannelUtils;
import protocolsupport.utils.recyclable.RecyclableCollection;

public class PacketDecoder implements IPacketDecoder {

	private final MiddleTransformerRegistry<ServerBoundMiddlePacket> registry = new MiddleTransformerRegistry<>();
	{
		try {
			registry.register(EnumProtocol.HANDSHAKING, 0x00, SetProtocol.class);
			registry.register(EnumProtocol.LOGIN, 0x00, LoginStart.class);
			registry.register(EnumProtocol.LOGIN, 0x01, EncryptionResponse.class);
			registry.register(EnumProtocol.STATUS, 0x00, ServerInfoRequest.class);
			registry.register(EnumProtocol.STATUS, 0x01, Ping.class);
			registry.register(EnumProtocol.PLAY, 0x00, KeepAlive.class);
			registry.register(EnumProtocol.PLAY, 0x01, Chat.class);
			registry.register(EnumProtocol.PLAY, 0x02, UseEntity.class);
			registry.register(EnumProtocol.PLAY, 0x03, Flying.class);
			registry.register(EnumProtocol.PLAY, 0x04, Position.class);
			registry.register(EnumProtocol.PLAY, 0x05, Look.class);
			registry.register(EnumProtocol.PLAY, 0x06, PositionLook.class);
			registry.register(EnumProtocol.PLAY, 0x07, BlockDig.class);
			registry.register(EnumProtocol.PLAY, 0x08, BlockPlace.class);
			registry.register(EnumProtocol.PLAY, 0x09, HeldSlot.class);
			registry.register(EnumProtocol.PLAY, 0x0A, Animation.class);
			registry.register(EnumProtocol.PLAY, 0x0B, EntityAction.class);
			registry.register(EnumProtocol.PLAY, 0x0C, SteerVehicle.class);
			registry.register(EnumProtocol.PLAY, 0x0D, InventoryClose.class);
			registry.register(EnumProtocol.PLAY, 0x0E, InventoryClick.class);
			registry.register(EnumProtocol.PLAY, 0x0F, InventoryTransaction.class);
			registry.register(EnumProtocol.PLAY, 0x10, CreativeSetSlot.class);
			registry.register(EnumProtocol.PLAY, 0x11, InventoryEnchant.class);
			registry.register(EnumProtocol.PLAY, 0x12, UpdateSign.class);
			registry.register(EnumProtocol.PLAY, 0x13, PlayerAbilities.class);
			registry.register(EnumProtocol.PLAY, 0x14, TabComplete.class);
			registry.register(EnumProtocol.PLAY, 0x15, ClientSettings.class);
			registry.register(EnumProtocol.PLAY, 0x16, ClientCommand.class);
			registry.register(EnumProtocol.PLAY, 0x17, CustomPayload.class);
			registry.register(EnumProtocol.PLAY, 0x18, Spectate.class);
			registry.register(EnumProtocol.PLAY, 0x19, ResourcePackStatus.class);
			registry.setCallBack(new InitCallBack<ServerBoundMiddlePacket>() {
				@Override
				public void onInit(ServerBoundMiddlePacket object) {
					object.setSharedStorage(sharedstorage);
				}
			});
		} catch (Throwable t) {
			SneakyThrow.sneaky(t);
		}
	}

	protected final SharedStorage sharedstorage;
	private final WrappingBufferPacketDataSerializer serializer = WrappingBufferPacketDataSerializer.create(ProtocolVersion.MINECRAFT_1_8);
	public PacketDecoder(SharedStorage sharedstorage) {
		this.sharedstorage = sharedstorage;
	}

	private final LegacyAnimatePacketReorderer reorderer = new LegacyAnimatePacketReorderer();

	@Override
	public void decode(ChannelHandlerContext ctx, ByteBuf input, List<Object> list) throws Exception {
		if (!input.isReadable()) {
			return;
		}
		Channel channel = ctx.channel();
		EnumProtocol currentProtocol = channel.attr(ChannelUtils.CURRENT_PROTOCOL_KEY).get();
		serializer.setBuf(input);
		int packetId = serializer.readVarInt();
		ServerBoundMiddlePacket packetTransformer = registry.getTransformer(currentProtocol, packetId);
		if (packetTransformer != null) {
			if (packetTransformer.needsPlayer()) {
				packetTransformer.setPlayer(ChannelUtils.getBukkitPlayer(channel));
			}
			packetTransformer.readFromClientData(serializer);
			RecyclableCollection<? extends Packet<?>> collection = packetTransformer.toNative();
			try {
				if (currentProtocol == EnumProtocol.PLAY) {
					list.addAll(reorderer.orderPackets(collection));
				} else {
					list.addAll(collection);
				}
			} finally {
				collection.recycle();
			}
		}
		if (serializer.isReadable()) {
			throw new DecoderException("Did not read all data from packet " + packetId+ ", bytes left: " + serializer.readableBytes());
		}
	}

}
