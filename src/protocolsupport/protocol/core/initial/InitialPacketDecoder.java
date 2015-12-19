package protocolsupport.protocol.core.initial;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.util.concurrent.Future;

import net.minecraft.server.v1_8_R3.MinecraftServer;

import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.concurrent.TimeUnit;

import protocolsupport.api.ProtocolVersion;
import protocolsupport.protocol.core.ChannelHandlers;
import protocolsupport.protocol.core.IPipeLineBuilder;
import protocolsupport.protocol.storage.ProtocolStorage;
import protocolsupport.utils.Utils;

public class InitialPacketDecoder extends SimpleChannelInboundHandler<ByteBuf> {

	private static final int ping152delay = getPing152Delay();

	private static int getPing152Delay() {
		try {
			return Integer.parseInt(System.getProperty("protocolsupport.ping152delay", "500"));
		} catch (Throwable t) {
		}
		return 500;
	}

	private static final int pingLegacyDelay = getPingLegacyDelay();

	private static int getPingLegacyDelay() {
		try {
			return Integer.parseInt(System.getProperty("protocolsupport.pinglegacydelay", "1000"));
		} catch (Throwable t) {
		}
		return 500;
	}

	@SuppressWarnings("serial")
	private static final EnumMap<ProtocolVersion, IPipeLineBuilder> pipelineBuilders = new EnumMap<ProtocolVersion, IPipeLineBuilder>(ProtocolVersion.class) {{
		IPipeLineBuilder builder = new protocolsupport.protocol.transformer.v_1_8.PipeLineBuilder();
		put(ProtocolVersion.MINECRAFT_1_8, builder);
		IPipeLineBuilder builder17 = new protocolsupport.protocol.transformer.v_1_7.PipeLineBuilder();
		put(ProtocolVersion.MINECRAFT_1_7_10, builder17);
		put(ProtocolVersion.MINECRAFT_1_7_5, builder17);
		IPipeLineBuilder builder16 = new protocolsupport.protocol.transformer.v_1_6.PipeLineBuilder();
		put(ProtocolVersion.MINECRAFT_1_6_4, builder16);
		put(ProtocolVersion.MINECRAFT_1_6_2, builder16);
		put(ProtocolVersion.MINECRAFT_1_6_1, builder16);
		IPipeLineBuilder builder15 = new protocolsupport.protocol.transformer.v_1_5.PipeLineBuilder();
		put(ProtocolVersion.MINECRAFT_1_5_2, builder15);
		put(ProtocolVersion.MINECRAFT_1_5_1, builder15);
		put(ProtocolVersion.MINECRAFT_1_4_7, new protocolsupport.protocol.transformer.v_1_4.PipeLineBuilder());
		put(ProtocolVersion.UNKNOWN, builder);
	}};


	protected final ByteBuf receivedData = Unpooled.buffer();

	protected volatile Future<?> responseTask;

	protected void scheduleTask(ChannelHandlerContext ctx, Runnable task, long delay, TimeUnit tu) {
		cancelTask();
		responseTask = ctx.executor().schedule(task, delay, tu);
	}

	protected void cancelTask() {
		if (responseTask != null) {
			responseTask.cancel(true);
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		cancelTask();
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception  {
		super.handlerRemoved(ctx);
		cancelTask();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
		if (!buf.isReadable()) {
			return;
		}
		receivedData.writeBytes(buf);
		final Channel channel = ctx.channel();
		ProtocolVersion handshakeversion = ProtocolVersion.NOT_SET;
		receivedData.readerIndex(0);
		int firstbyte = receivedData.readUnsignedByte();
		switch (firstbyte) {
			case 0xFE: { //old ping
				try {
					if (receivedData.readableBytes() == 0) { //really old protocol probably
						scheduleTask(ctx, new Ping11ResponseTask(channel), pingLegacyDelay, TimeUnit.MILLISECONDS);
					} else if (receivedData.readUnsignedByte() == 1) {
						if (receivedData.readableBytes() == 0) {
							//1.5.2 probably
							scheduleTask(ctx, new Ping152ResponseTask(this, channel), ping152delay, TimeUnit.MILLISECONDS);
						} else if (
							(receivedData.readUnsignedByte() == 0xFA) &&
							"MC|PingHost".equals(new String(Utils.toArray(receivedData.readBytes(receivedData.readUnsignedShort() * 2)), StandardCharsets.UTF_16BE))
						) { //1.6.*
							receivedData.readUnsignedShort();
							handshakeversion = ProtocolVersion.fromId(receivedData.readUnsignedByte());
						}
					}
				} catch (IndexOutOfBoundsException ex) {
				}
				break;
			}
			case 0x02: { //1.6 or 1.5.2 handshake
				try {
					handshakeversion = ProtocolVersion.fromId(receivedData.readUnsignedByte());
				} catch (IndexOutOfBoundsException ex) {
				}
				break;
			}
			default: { //1.7 or 1.8 handshake
				receivedData.readerIndex(0);
				ByteBuf data = getVarIntPrefixedData(receivedData);
				if (data != null) {
					handshakeversion = readNPHandshake(data);
				}
				break;
			}
		}
		//if we detected the protocol than we save it and process data
		if (handshakeversion != ProtocolVersion.NOT_SET) {
			setProtocol(channel, receivedData, handshakeversion);
		}
	}

	protected volatile boolean protocolSet = false;

	protected void setProtocol(final Channel channel, final ByteBuf input, ProtocolVersion version) throws Exception {
		if (protocolSet) {
			return;
		}
		protocolSet = true;
		if (MinecraftServer.getServer().isDebugging()) {
			System.out.println(Utils.getNetworkManagerSocketAddress(channel)+ " connected with protocol version "+version);
		}
		ProtocolStorage.setProtocolVersion(Utils.getNetworkManagerSocketAddress(channel), version);
		channel.pipeline().remove(ChannelHandlers.INITIAL_DECODER);
		pipelineBuilders.get(version).buildPipeLine(channel, version);
		input.readerIndex(0);
		channel.pipeline().firstContext().fireChannelRead(input);
	}

	@SuppressWarnings("deprecation")
	private static ProtocolVersion readNPHandshake(ByteBuf data) {
		if (readVarInt(data) == 0x00) {
			return ProtocolVersion.fromId(readVarInt(data));
		}
		return ProtocolVersion.UNKNOWN;
	}

	private static ByteBuf getVarIntPrefixedData(final ByteBuf byteBuf) {
		final byte[] array = new byte[3];
		for (int i = 0; i < array.length; ++i) {
			if (!byteBuf.isReadable()) {
				return null;
			}
			array[i] = byteBuf.readByte();
			if (array[i] >= 0) {
				final int length = readVarInt(Unpooled.wrappedBuffer(array));
				if (byteBuf.readableBytes() < length) {
					return null;
				}
				return byteBuf.readBytes(length);
			}
		}
		throw new CorruptedFrameException("Packet length is wider than 21 bit");
	}

	private static int readVarInt(ByteBuf data) {
		int value = 0;
		int length = 0;
		byte b0;
		do {
			b0 = data.readByte();
			value |= (b0 & 0x7F) << (length++ * 7);
			if (length > 5) {
				throw new RuntimeException("VarInt too big");
			}
		} while ((b0 & 0x80) == 0x80);
		return value;
	}

}
