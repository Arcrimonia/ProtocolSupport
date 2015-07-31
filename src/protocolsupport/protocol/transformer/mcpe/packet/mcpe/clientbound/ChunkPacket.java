package protocolsupport.protocol.transformer.mcpe.packet.mcpe.clientbound;

import io.netty.buffer.ByteBuf;

import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.Chunk;
import net.minecraft.server.v1_8_R3.ChunkSection;
import net.minecraft.server.v1_8_R3.IBlockData;
import protocolsupport.protocol.transformer.mcpe.packet.mcpe.ClientboundPEPacket;
import protocolsupport.protocol.transformer.mcpe.remapper.BlockIDRemapper;
import protocolsupport.utils.MutableBlockPosition;

public class ChunkPacket implements ClientboundPEPacket {

	protected Chunk chunk;

	public ChunkPacket(Chunk chunk) {
		this.chunk = chunk;
	}

	@Override
	public int getId() {
		return 0xAF;
	}

	@Override
	public ClientboundPEPacket encode(ByteBuf buf) throws Exception {
		buf.writeInt(chunk.locX);
		buf.writeInt(chunk.locZ);

		buf.writeInt(83200);

		MutableBlockPosition pos = new MutableBlockPosition(0, 0, 0);

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for (int y = 0; y < 128; y++) {
					buf.writeByte(BlockIDRemapper.replaceBlockId(Block.getId(getType(x, y, z, pos).getBlock())));
				}
			}
		}

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for (int y = 0; y < 128; y += 2) {
					IBlockData data1 = getType(x, y, z, pos);
					byte data = (byte) (data1.getBlock().toLegacyData(data1) & 0xF);
					IBlockData data2 = getType(x, y + 1, z, pos);
					data |= ((data2.getBlock().toLegacyData(data2) & 0xF) << 4);
					buf.writeByte(data);
				}
			}
		}

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for (int y = 0; y < 128; y += 2) {
					byte data = (byte) (getSection(y).d(x, y & 0xF, z) & 0xF);
					data |= ((getSection(y + 1).d(x, (y + 1) & 0xF, z) & 0xF) << 4);
					buf.writeByte(data);
				}
			}
		}

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for (int y = 0; y < 128; y += 2) {
					byte data = (byte) (getSection(y).e(x, y & 0xF, z) & 0xF);
					data |= ((getSection(y + 1).e(x, (y + 1) & 0xF, z) & 0xF) << 4);
					buf.writeByte(data);
				}
			}
		}

		for (int i = 0; i < 256; i++) {
			buf.writeByte((byte) 0xFF);
		}

		for (int i = 0; i < 256; i++) {
			buf.writeByte((byte) 0x01);
			buf.writeByte((byte) 0x85);
			buf.writeByte((byte) 0xB2);
			buf.writeByte((byte) 0x4A);
		}

		return this;
	}

	private IBlockData getType(int x, int y, int z, MutableBlockPosition pos) {
		pos.setCoords(x, y, z);
		return chunk.getBlockData(pos);
	}

	private ChunkSection getSection(int y) {
		ChunkSection section = chunk.getSections()[y >> 4];
		return section != null ? section : EMPTY_SECTION;
	}

	private static final EmptyChunkSection EMPTY_SECTION = new EmptyChunkSection(0, true);

	private static final class EmptyChunkSection extends ChunkSection {

		public EmptyChunkSection(int y, boolean flag) {
			super(y, flag);
		}

		@Override
		public int d(int x, int y, int z) {
			return 0;
		}

		@Override
		public int e(int x, int y, int z) {
			return 0;
		}
		
	}

}