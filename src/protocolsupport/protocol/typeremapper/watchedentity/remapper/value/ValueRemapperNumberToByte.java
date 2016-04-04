package protocolsupport.protocol.typeremapper.watchedentity.remapper.value;

import protocolsupport.utils.datawatcher.DataWatcherObject;
import protocolsupport.utils.datawatcher.objects.DataWatcherObjectByte;

public class ValueRemapperNumberToByte implements ValueRemapper<DataWatcherObject<?>> {

	public static final ValueRemapperNumberToByte INSTANCE = new ValueRemapperNumberToByte();

	protected ValueRemapperNumberToByte() {
	}

	@Override
	public DataWatcherObject<?> remap(DataWatcherObject<?> object) {
		Number number = (Number) object.getValue();
		return new DataWatcherObjectByte(number.byteValue());
	}

}
