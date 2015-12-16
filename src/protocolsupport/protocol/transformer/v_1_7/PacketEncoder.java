package protocolsupport.protocol.transformer.v_1_7;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

import java.io.IOException;
import java.util.Collection;

import org.spigotmc.SneakyThrow;

import net.minecraft.server.v1_8_R3.EnumProtocol;
import net.minecraft.server.v1_8_R3.EnumProtocolDirection;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.NetworkManager;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketListener;

import protocolsupport.api.ProtocolVersion;
import protocolsupport.protocol.ClientBoundPacket;
import protocolsupport.protocol.PacketDataSerializer;
import protocolsupport.protocol.core.IPacketEncoder;
import protocolsupport.protocol.storage.LocalStorage;
import protocolsupport.protocol.transformer.middlepacket.ClientBoundMiddlePacket;
import protocolsupport.protocol.transformer.middlepacketimpl.PacketData;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.login.v_1_7.LoginSuccess;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v1_5_1_6_1_7.BlockChangeMulti;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v1_5_1_6_1_7.BlockSignUpdate;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v1_5_1_6_1_7.ChunkMulti;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v1_5_1_6_1_7.ChunkSingle;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v1_5_1_6_1_7.CollectEffect;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v1_5_1_6_1_7.Entity;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v1_5_1_6_1_7.EntityDestroy;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v1_5_1_6_1_7.EntityEffectAdd;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v1_5_1_6_1_7.EntityEffectRemove;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v1_5_1_6_1_7.EntityEquipment;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v1_5_1_6_1_7.EntityHeadRotation;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v1_5_1_6_1_7.EntityLook;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v1_5_1_6_1_7.EntityMetadata;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v1_5_1_6_1_7.EntityRelMove;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v1_5_1_6_1_7.EntityVelocity;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v1_5_1_6_1_7.InventoryData;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v1_5_1_6_1_7.InventoryOpen;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v1_5_1_6_1_7.InventorySetItems;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v1_5_1_6_1_7.InventorySetSlot;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v1_5_1_6_1_7.KeepAlive;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v1_5_1_6_1_7.PlayerInfo;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v1_5_1_6_1_7.ScoreboardObjective;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v1_5_1_6_1_7.ScoreboardScore;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v1_5_1_6_1_7.ScoreboardTeam;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v1_5_1_6_1_7.SetExperience;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v1_5_1_6_1_7.WorldEvent;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v1_5_1_6_1_7.WorldParticle;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v_1_6_1_7.EntitySetAttributes;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v_1_6_1_7.SetHealth;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v_1_7.BlockAction;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v_1_7.BlockBreakAnimation;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v_1_7.BlockChangeSingle;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v_1_7.BlockOpenSignEditor;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v_1_7.BlockTileUpdate;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v_1_7.Chat;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v_1_7.CustomPayload;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v_1_7.EntityTeleport;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v_1_7.Login;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v_1_7.Map;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v_1_7.Position;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v_1_7.ResourcePack;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v_1_7.Respawn;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v_1_7.SpawnLiving;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v_1_7.SpawnNamed;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v_1_7.SpawnObject;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v_1_7.SpawnPainting;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v_1_7.SpawnPosition;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v_1_7.UseBed;
import protocolsupport.protocol.transformer.middlepacketimpl.clientbound.status.v_1_7.ServerInfo;
import protocolsupport.protocol.transformer.utils.registry.MiddleTransformerRegistry;
import protocolsupport.utils.Utils;

public class PacketEncoder implements IPacketEncoder {

	private static final EnumProtocolDirection direction = EnumProtocolDirection.CLIENTBOUND;
	private static final AttributeKey<EnumProtocol> currentStateAttrKey = NetworkManager.c;

	private static final boolean[] blockedPlayPackets = new boolean[256];
	static {
		for (int i = 0x41; i < 0x49; i++) {
			if (i == 0x48) {
				continue;
			}
			blockedPlayPackets[i] = true;
		}
	}

	private static final MiddleTransformerRegistry<ClientBoundMiddlePacket<Collection<PacketData>>> registry = new MiddleTransformerRegistry<>(ProtocolVersion.MINECRAFT_1_7_10, ProtocolVersion.MINECRAFT_1_7_5);
	static {
		try {
			registry.register(EnumProtocol.LOGIN, ClientBoundPacket.LOGIN_SUCCESS_ID, LoginSuccess.class);
			registry.register(EnumProtocol.STATUS, ClientBoundPacket.STATUS_SERVER_INFO_ID, ServerInfo.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_KEEP_ALIVE_ID, KeepAlive.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_LOGIN_ID, Login.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_CHAT_ID, Chat.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_ENTITY_EQUIPMENT_ID, EntityEquipment.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_SPAWN_POSITION_ID, SpawnPosition.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_UPDATE_HEALTH_ID, SetHealth.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_RESPAWN_ID, Respawn.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_POSITION_ID, Position.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_BED_ID, UseBed.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_SPAWN_NAMED_ID, SpawnNamed.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_COLLECT_EFFECT_ID, CollectEffect.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_SPAWN_OBJECT_ID, SpawnObject.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_SPAWN_LIVING_ID, SpawnLiving.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_SPAWN_PAINTING_ID, SpawnPainting.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_ENTITY_VELOCITY_ID, EntityVelocity.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_ENTITY_DESTROY_ID, EntityDestroy.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_ENTITY_ID, Entity.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_ENTITY_REL_MOVE_ID, EntityRelMove.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_ENTITY_LOOK_ID, EntityLook.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_ENTITY_REL_MOVE_LOOK_ID, EntityRelMove.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_ENTITY_TELEPORT_ID, EntityTeleport.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_ENTITY_HEAD_ROTATION_ID, EntityHeadRotation.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_ENTITY_METADATA_ID, EntityMetadata.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_ENTITY_EFFECT_ADD_ID, EntityEffectAdd.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_ENTITY_EFFECT_REMOVE_ID, EntityEffectRemove.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_EXPERIENCE_ID, SetExperience.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_ENTITY_ATTRIBUTES_ID, EntitySetAttributes.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_CHUNK_SINGLE_ID, ChunkSingle.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_BLOCK_CHANGE_MULTI_ID, BlockChangeMulti.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_BLOCK_CHANGE_SINGLE_ID, BlockChangeSingle.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_BLOCK_ACTION_ID, BlockAction.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_BLOCK_BREAK_ANIMATION_ID, BlockBreakAnimation.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_CHUNK_MULTI_ID, ChunkMulti.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_WORLD_EVENT_ID, WorldEvent.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_WORLD_PARTICLES_ID, WorldParticle.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_WINDOW_OPEN_ID, InventoryOpen.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_WINDOW_SET_SLOT_ID, InventorySetSlot.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_WINDOW_SET_ITEMS_ID, InventorySetItems.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_WINDOW_DATA_ID, InventoryData.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_UPDATE_SIGN_ID, BlockSignUpdate.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_MAP_ID, Map.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_UPDATE_TILE_ID, BlockTileUpdate.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_SIGN_EDITOR_ID, BlockOpenSignEditor.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_PLAYER_INFO_ID, PlayerInfo.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_SCOREBOARD_OBJECTIVE_ID, ScoreboardObjective.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_SCOREBOARD_SCORE_ID, ScoreboardScore.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_SCOREBOARD_TEAM_ID, ScoreboardTeam.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_CUSTOM_PAYLOAD_ID, CustomPayload.class);
			registry.register(EnumProtocol.PLAY, ClientBoundPacket.PLAY_RESOURCE_PACK_ID, ResourcePack.class);
		} catch (Throwable t) {
			SneakyThrow.sneaky(t);
		}
	}

	private final ProtocolVersion version;
	public PacketEncoder(ProtocolVersion version) {
		this.version = version;
	}

	private final LocalStorage storage = new LocalStorage();

	@Override
	public void encode(ChannelHandlerContext ctx, Packet<PacketListener> packet, ByteBuf output) throws Exception {
		Channel channel = ctx.channel();
		EnumProtocol currentProtocol = channel.attr(currentStateAttrKey).get();
		final Integer packetId = currentProtocol.a(direction, packet);
		if (packetId == null) {
			throw new IOException("Can't serialize unregistered packet");
		}
		if ((currentProtocol == EnumProtocol.PLAY) && blockedPlayPackets[packetId]) {
			return;
		}
		ClientBoundMiddlePacket<Collection<PacketData>> packetTransformer = registry.getTransformer(currentProtocol, packetId);
		try {
			if (packetTransformer != null) {
				PacketDataSerializer serverdata = PacketDataSerializer.createNew(ProtocolVersion.getLatest());
				packet.b(serverdata);
				try {
					if (packetTransformer.needsPlayer()) {
						packetTransformer.setPlayer(Utils.getBukkitPlayer(channel));
					}
					packetTransformer.readFromServerData(serverdata);
					packetTransformer.handle(storage);
					Collection<PacketData> data = packetTransformer.toData(version);
					try {
						for (PacketData packetdata : data) {
							PacketDataSerializer singlepacketdata = PacketDataSerializer.createNew(version);
							singlepacketdata.writeVarInt(packetdata.getPacketId());
							singlepacketdata.writeBytes(packetdata.getData());
							ctx.write(singlepacketdata);
						}
						ctx.flush();
					} finally {
						for (PacketData packetdata : data) {
							packetdata.getData().release();
						}
					}
				} finally {
					serverdata.release();
				}
			} else {
				PacketDataSerializer outserializer = new PacketDataSerializer(output, version);
				outserializer.writeVarInt(packetId);
				packet.b(outserializer);
			}
		} catch (Throwable t) {
			if (MinecraftServer.getServer().isDebugging()) {
				t.printStackTrace();
			}
			throw t;
		}
	}

}
