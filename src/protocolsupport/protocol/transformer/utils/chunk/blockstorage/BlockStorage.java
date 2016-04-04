package protocolsupport.protocol.transformer.utils.chunk.blockstorage;

import io.netty.buffer.ByteBuf;

public abstract class BlockStorage {

	public static BlockStorage create(int bitsPerBlock, int dataLength) {
		switch (bitsPerBlock) {
			case 4: {
				return new NibbleBlockStorage(dataLength);
			}
			//TODO: find a way to test it
			/*case 8: {
				return new ByteBlockStorage();
			}
			case 0:
			case 16: {
				return new ShortBlockStorage();
			}*/
			default: {
				return new BitsBlockStorage(bitsPerBlock, dataLength);
			}
		}
	}

	protected final int bitsPerBlock;
	protected BlockStorage(int bitsPerBlock) {
		this.bitsPerBlock = bitsPerBlock;
	}

	public abstract void readFromStream(ByteBuf stream);

	public abstract int getPaletteIndex(int blockIndex);

}