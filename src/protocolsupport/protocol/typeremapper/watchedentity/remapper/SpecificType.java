package protocolsupport.protocol.typeremapper.watchedentity.remapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.entity.EntityType;

import protocolsupport.api.ProtocolVersion;
import protocolsupport.protocol.typeremapper.watchedentity.remapper.MappingEntry.MappingEntryOriginal;
import protocolsupport.protocol.typeremapper.watchedentity.remapper.value.ValueRemapper;
import protocolsupport.protocol.typeremapper.watchedentity.remapper.value.ValueRemapperBooleanToByte;
import protocolsupport.protocol.typeremapper.watchedentity.remapper.value.ValueRemapperStringClamp;
import protocolsupport.protocol.typeremapper.watchedentity.remapper.value.ValueRemapperNumberToByte;
import protocolsupport.protocol.typeremapper.watchedentity.remapper.value.ValueRemapperNumberToInt;
import protocolsupport.protocol.typeremapper.watchedentity.remapper.value.ValueRemapperNumberToShort;
import protocolsupport.utils.ProtocolVersionsHelper;
import protocolsupport.utils.datawatcher.DataWatcherObject;
import protocolsupport.utils.datawatcher.objects.DataWatcherObjectBlockState;
import protocolsupport.utils.datawatcher.objects.DataWatcherObjectBoolean;
import protocolsupport.utils.datawatcher.objects.DataWatcherObjectByte;
import protocolsupport.utils.datawatcher.objects.DataWatcherObjectInt;
import protocolsupport.utils.datawatcher.objects.DataWatcherObjectShort;
import protocolsupport.utils.datawatcher.objects.DataWatcherObjectVarInt;

public enum SpecificType {

	NONE(EType.NONE, -1),
	ENTITY(EType.NONE, -1,
		//flags
		new Mapping()
		.addEntries(new MappingEntryOriginal(0))
		.addProtocols(ProtocolVersionsHelper.ALL)
		.addProtocols(ProtocolVersion.MINECRAFT_PE),
		//air
		new Mapping()
		.addEntries(new MappingEntry(1, ValueRemapperNumberToShort.INSTANCE))
		.addProtocols(ProtocolVersionsHelper.BEFORE_1_9)
		.addProtocols(ProtocolVersion.MINECRAFT_PE)
	),
	LIVING(EType.NONE, -1, SpecificType.ENTITY,
		//nametag
		new Mapping()
		.addEntries(new MappingEntryOriginal(2))
		.addProtocols(ProtocolVersion.MINECRAFT_1_8)
		.addProtocols(ProtocolVersion.MINECRAFT_PE),
		new Mapping()
		.addEntries(new MappingEntry(2, 10, new ValueRemapperStringClamp(64)))
		.addProtocols(ProtocolVersion.getAllBetween(ProtocolVersion.MINECRAFT_1_7_10, ProtocolVersion.MINECRAFT_1_6_1)),
		new Mapping()
		.addEntries(new MappingEntry(2, 5, new ValueRemapperStringClamp(64)))
		.addProtocols(ProtocolVersionsHelper.BEFORE_1_6),
		//nametag visible
		new Mapping()
		.addEntries(new MappingEntry(3, 3, ValueRemapperBooleanToByte.INSTANCE))
		.addProtocols(ProtocolVersion.MINECRAFT_1_8)
		.addProtocols(ProtocolVersion.MINECRAFT_PE),
		new Mapping()
		.addEntries(new MappingEntry(3, 11, ValueRemapperBooleanToByte.INSTANCE))
		.addProtocols(ProtocolVersion.getAllBetween(ProtocolVersion.MINECRAFT_1_7_10, ProtocolVersion.MINECRAFT_1_6_1)),
		new Mapping()
		.addEntries(new MappingEntry(3, 6, ValueRemapperBooleanToByte.INSTANCE))
		.addProtocols(ProtocolVersionsHelper.BEFORE_1_6),
		//health
		new Mapping()
		.addEntries(new MappingEntryOriginal(6))
		.addProtocols(ProtocolVersion.getAllBetween(ProtocolVersion.MINECRAFT_1_8, ProtocolVersion.MINECRAFT_1_6_1)),
		//pcolor, pambient, arrowsn
		new Mapping()
		.addEntries(new MappingEntry(7, 7, ValueRemapperNumberToInt.INSTANCE))
		.addEntries(new MappingEntry(8, ValueRemapperBooleanToByte.INSTANCE))
		.addEntries(new MappingEntry(9, 9, ValueRemapperNumberToByte.INSTANCE))
		.addProtocols(ProtocolVersion.getAllBetween(ProtocolVersion.MINECRAFT_1_7_10, ProtocolVersion.MINECRAFT_1_6_1)),
		new Mapping()
		.addEntries(new MappingEntry(7, 8, ValueRemapperNumberToInt.INSTANCE))
		.addEntries(new MappingEntry(8, 9, ValueRemapperBooleanToByte.INSTANCE))
		.addEntries(new MappingEntry(9, 10, ValueRemapperNumberToByte.INSTANCE))
		.addProtocols(ProtocolVersionsHelper.BEFORE_1_6),
		//pcolor, pambient
		new Mapping()
		.addEntries(new MappingEntry(7, 7, ValueRemapperNumberToInt.INSTANCE))
		.addEntries(new MappingEntry(8, ValueRemapperBooleanToByte.INSTANCE))
		.addProtocols(ProtocolVersion.MINECRAFT_PE)
	),
	INSENTIENT(EType.NONE, -1, SpecificType.LIVING,
		//noai
		new Mapping()
		.addEntries(new MappingEntry(10, 15))
		.addProtocols(ProtocolVersion.MINECRAFT_1_8)
		.addProtocols(ProtocolVersion.MINECRAFT_PE)		
	),
	PLAYER(EType.NONE, -1, SpecificType.LIVING,
		//abs hearts, score
		new Mapping()
		.addEntries(new MappingEntry(10, 17))
		.addEntries(new MappingEntry(11, 18, ValueRemapperNumberToInt.INSTANCE))
		.addProtocols(ProtocolVersionsHelper.BEFORE_1_9),
		//skin flags(cape enabled for some protocols)
		new Mapping()
		.addEntries(new MappingEntry(12, 10))
		.addProtocols(ProtocolVersion.getAllBetween(ProtocolVersion.MINECRAFT_1_8, ProtocolVersion.MINECRAFT_1_6_1))
	),
	AGEABLE(EType.NONE, -1, SpecificType.INSENTIENT,
		//age
		new Mapping()
		.addEntries(new MappingEntry(11, 12, new ValueRemapper<DataWatcherObjectBoolean>() {
			@Override
			public DataWatcherObject<?> remap(DataWatcherObjectBoolean object) {
				return new DataWatcherObjectByte((byte) (object.getValue() ? -1 : 0));
			}
		}))
		.addProtocols(ProtocolVersion.MINECRAFT_1_8),
		new Mapping()
		.addEntries(new MappingEntry(11, 12, new ValueRemapper<DataWatcherObjectBoolean>() {
			@Override
			public DataWatcherObject<?> remap(DataWatcherObjectBoolean object) {
				return new DataWatcherObjectInt((object.getValue() ? -1 : 0));
			}
		}))
		.addProtocols(ProtocolVersionsHelper.BEFORE_1_8),
		//age - special hack for hologram plugins that want to set int age
		//datawatcher index 30 will be remapped to age datawatcher index
		new Mapping()
		.addEntries(new MappingEntry(30, 12, ValueRemapperNumberToInt.INSTANCE))
		.addProtocols(ProtocolVersion.getAllBetween(ProtocolVersion.MINECRAFT_1_7_10, ProtocolVersion.MINECRAFT_1_6_1))
	),
	TAMEABLE(EType.NONE, -1, SpecificType.AGEABLE,
		//tame flags
		new Mapping()
		.addEntries(new MappingEntry(12, 16))
		.addProtocols(ProtocolVersionsHelper.BEFORE_1_9)
	),
	ARMOR_STAND(EType.NONE, -1, SpecificType.LIVING,
		//parts position
		new Mapping()
		.addEntries(MappingEntryOriginal.of(10, 11, 12, 13, 14, 15, 16))
		.addProtocols(ProtocolVersion.MINECRAFT_1_8)
	),
	COW(EType.MOB, EntityType.COW, SpecificType.AGEABLE),
	MUSHROOM_COW(EType.MOB, EntityType.MUSHROOM_COW, SpecificType.COW),
	CHICKEN(EType.MOB, EntityType.CHICKEN, SpecificType.AGEABLE),
	SQUID(EType.MOB, EntityType.SQUID, SpecificType.INSENTIENT),
	HORSE(EType.MOB, EntityType.HORSE, SpecificType.AGEABLE,
		//info flags, type, color/variant, armor
		new Mapping()
		.addEntries(new MappingEntry(12, 16, ValueRemapperNumberToInt.INSTANCE))
		.addEntries(new MappingEntry(13, 19, ValueRemapperNumberToByte.INSTANCE))
		.addEntries(new MappingEntry(14, 20, ValueRemapperNumberToInt.INSTANCE))
		.addEntries(new MappingEntry(16, 22, ValueRemapperNumberToInt.INSTANCE))
		.addProtocols(ProtocolVersionsHelper.BEFORE_1_9)
	),
	BAT(EType.MOB, EntityType.BAT, SpecificType.INSENTIENT,
		//hanging
		new Mapping()
		.addEntries(new MappingEntry(11, 16))
		.addProtocols(ProtocolVersionsHelper.BEFORE_1_9)
		.addProtocols(ProtocolVersion.MINECRAFT_PE)
	),
	OCELOT(EType.MOB, EntityType.OCELOT, SpecificType.TAMEABLE,
		//type
		new Mapping()
		.addEntries(new MappingEntry(14, 18, ValueRemapperNumberToByte.INSTANCE))
		.addProtocols(ProtocolVersionsHelper.BEFORE_1_9)
		.addProtocols(ProtocolVersion.MINECRAFT_PE)
	),
	WOLF(EType.MOB, EntityType.WOLF, SpecificType.TAMEABLE,
		//health
		new Mapping()
		.addEntries(new MappingEntry(14, 18))
		.addProtocols(ProtocolVersion.getAllBetween(ProtocolVersion.MINECRAFT_1_8, ProtocolVersion.MINECRAFT_1_6_1)),
		new Mapping()
		.addEntries(new MappingEntry(14, 18, ValueRemapperNumberToInt.INSTANCE))
		.addProtocols(ProtocolVersionsHelper.BEFORE_1_6),
		//begging
		new Mapping()
		.addEntries(new MappingEntry(15, 19, ValueRemapperBooleanToByte.INSTANCE))
		.addProtocols(ProtocolVersionsHelper.BEFORE_1_9),
		//collar color
		new Mapping()
		.addEntries(new MappingEntry(16, 20, ValueRemapperNumberToByte.INSTANCE))
		.addProtocols(ProtocolVersion.MINECRAFT_1_8),
		new Mapping()
		.addEntries(new MappingEntry(16, 20, new ValueRemapper<DataWatcherObjectVarInt>() {
			@Override
			public DataWatcherObject<?> remap(DataWatcherObjectVarInt object) {
				return new DataWatcherObjectByte((byte) (15 - object.getValue()));
			}
		}))
		.addProtocols(ProtocolVersionsHelper.BEFORE_1_8)
	),
	PIG(EType.MOB, EntityType.PIG, SpecificType.AGEABLE,
		//has saddle
		new Mapping()
		.addEntries(new MappingEntry(12, 16, ValueRemapperBooleanToByte.INSTANCE))
		.addProtocols(ProtocolVersionsHelper.BEFORE_1_9)
		.addProtocols(ProtocolVersion.MINECRAFT_PE)
	),
	RABBIT(EType.MOB, EntityType.RABBIT, SpecificType.AGEABLE,
		//type
		new Mapping()
		.addEntries(new MappingEntry(12, 18, ValueRemapperNumberToByte.INSTANCE))
		.addProtocols(ProtocolVersionsHelper.BEFORE_1_9)
		.addProtocols(ProtocolVersion.MINECRAFT_PE)
	),
	SHEEP(EType.MOB, EntityType.SHEEP, SpecificType.AGEABLE,
		//info flags (color + sheared)
		new Mapping()
		.addEntries(new MappingEntry(12, 16))
		.addProtocols(ProtocolVersionsHelper.BEFORE_1_9)
		.addProtocols(ProtocolVersion.MINECRAFT_PE)
	),
	VILLAGER(EType.MOB, EntityType.VILLAGER, SpecificType.AGEABLE,
		//profession
		new Mapping()
		.addEntries(new MappingEntry(12, 16, ValueRemapperNumberToInt.INSTANCE))
		.addProtocols(ProtocolVersionsHelper.BEFORE_1_9)
		.addProtocols(ProtocolVersion.MINECRAFT_PE)
	),
	ENDERMAN(EType.MOB, EntityType.ENDERMAN, SpecificType.INSENTIENT,
		//carried block
		new Mapping()
		.addEntries(new MappingEntry(11, 16, new ValueRemapper<DataWatcherObjectBlockState>() {
			@Override
			public DataWatcherObject<?> remap(DataWatcherObjectBlockState object) {
				return new DataWatcherObjectShort((short) (object.getValue() >> 4));
			}
		}))
		.addProtocols(ProtocolVersion.MINECRAFT_1_8)
		.addProtocols(ProtocolVersion.MINECRAFT_PE),
		new Mapping()
		.addEntries(new MappingEntry(11, 16, new ValueRemapper<DataWatcherObjectBlockState>() {
			@Override
			public DataWatcherObject<?> remap(DataWatcherObjectBlockState object) {
				return new DataWatcherObjectByte((byte) (object.getValue() >> 4));
			}
		}))
		.addProtocols(ProtocolVersionsHelper.BEFORE_1_8),
		new Mapping()
		.addEntries(new MappingEntry(11, 17, new ValueRemapper<DataWatcherObjectBlockState>() {
			@Override
			public DataWatcherObject<?> remap(DataWatcherObjectBlockState object) {
				return new DataWatcherObjectByte((byte) (object.getValue() & 0xF));
			}
		}))
		.addProtocols(ProtocolVersionsHelper.BEFORE_1_9),
		//screaming
		new Mapping()
		.addEntries(new MappingEntry(12, 18, ValueRemapperBooleanToByte.INSTANCE))
		.addProtocols(ProtocolVersionsHelper.BEFORE_1_9)
	),
	GIANT(EType.MOB, EntityType.GIANT, SpecificType.INSENTIENT),
	SILVERFISH(EType.MOB, EntityType.SILVERFISH, SpecificType.INSENTIENT),
	ENDERMITE(EType.MOB, EntityType.ENDERMITE, SpecificType.INSENTIENT),
	ENDER_DRAGON(EType.MOB, EntityType.ENDER_DRAGON, SpecificType.INSENTIENT),
	SNOWMAN(EType.MOB, EntityType.SNOWMAN, SpecificType.INSENTIENT),
	ZOMBIE(EType.MOB, EntityType.ZOMBIE, SpecificType.INSENTIENT,
		//is baby, is villager, is converting
		new Mapping()
		.addEntries(new MappingEntry(11, 12, ValueRemapperBooleanToByte.INSTANCE))
		.addEntries(new MappingEntry(12, 13, ValueRemapperNumberToByte.INSTANCE))
		.addEntries(new MappingEntry(13, 14, ValueRemapperBooleanToByte.INSTANCE))
		.addProtocols(ProtocolVersionsHelper.BEFORE_1_9)
	),
	ZOMBIE_PIGMAN(EType.MOB, EntityType.PIG_ZOMBIE, SpecificType.ZOMBIE),
	BLAZE(EType.MOB, EntityType.BLAZE, SpecificType.INSENTIENT,
		//on fire
		new Mapping()
		.addEntries(new MappingEntry(11, 16))
		.addProtocols(ProtocolVersionsHelper.BEFORE_1_9)
		.addProtocols(ProtocolVersion.MINECRAFT_PE)
	),
	SPIDER(EType.MOB, EntityType.SPIDER, SpecificType.LIVING,
		//is climbing
		new Mapping()
		.addEntries(new MappingEntry(11, 16))
		.addProtocols(ProtocolVersionsHelper.BEFORE_1_9)
		.addProtocols(ProtocolVersion.MINECRAFT_PE)
	),
	CAVE_SPIDER(EType.MOB, EntityType.CAVE_SPIDER, SpecificType.SPIDER),
	CREEPER(EType.MOB, EntityType.CREEPER, SpecificType.INSENTIENT,
		//state, is powered, ignited
		new Mapping()
		.addEntries(new MappingEntry(11, 16, ValueRemapperNumberToByte.INSTANCE))
		.addEntries(new MappingEntry(12, 17, ValueRemapperBooleanToByte.INSTANCE))
		.addEntries(new MappingEntry(13, 18, ValueRemapperBooleanToByte.INSTANCE))
		.addProtocols(ProtocolVersionsHelper.BEFORE_1_9),
		//is powered ignited
		new Mapping()
		.addEntries(new MappingEntry(12, 19, ValueRemapperBooleanToByte.INSTANCE))
		.addEntries(new MappingEntry(13, 20, ValueRemapperBooleanToByte.INSTANCE))
		.addProtocols(ProtocolVersion.MINECRAFT_PE)
	),
	GHAST(EType.MOB, EntityType.GHAST, SpecificType.INSENTIENT,
		//is attacking
		new Mapping()
		.addEntries(new MappingEntry(11, 16, ValueRemapperBooleanToByte.INSTANCE))
		.addProtocols(ProtocolVersionsHelper.BEFORE_1_9)
		.addProtocols(ProtocolVersion.MINECRAFT_PE)
	),
	SLIME(EType.MOB, EntityType.SLIME, SpecificType.INSENTIENT,
		//size
		new Mapping()
		.addEntries(new MappingEntry(11, 16, ValueRemapperNumberToByte.INSTANCE))
		.addProtocols(ProtocolVersionsHelper.BEFORE_1_9)
		.addProtocols(ProtocolVersion.MINECRAFT_PE)
	),
	MAGMA_CUBE(EType.MOB, EntityType.MAGMA_CUBE, SpecificType.SLIME),
	SKELETON(EType.MOB, EntityType.SKELETON, SpecificType.INSENTIENT,
		//type
		new Mapping()
		.addEntries(new MappingEntry(11, 13, ValueRemapperNumberToByte.INSTANCE))
		.addProtocols(ProtocolVersionsHelper.BEFORE_1_9)
		.addProtocols(ProtocolVersion.MINECRAFT_PE)
	),
	WITCH(EType.MOB, EntityType.WITCH, SpecificType.INSENTIENT,
		//agressive
		new Mapping()
		.addEntries(new MappingEntry(11, 21, ValueRemapperBooleanToByte.INSTANCE))
		.addProtocols(ProtocolVersionsHelper.BEFORE_1_9)
	),
	IRON_GOLEM(EType.MOB, EntityType.IRON_GOLEM, SpecificType.INSENTIENT,
		//player created
		new Mapping()
		.addEntries(new MappingEntry(11, 16))
		.addProtocols(ProtocolVersionsHelper.BEFORE_1_9)
		.addProtocols(ProtocolVersion.MINECRAFT_PE)
	),
	SHULKER(EType.MOB, 69, SpecificType.INSENTIENT),
	WITHER(EType.MOB, EntityType.WITHER, SpecificType.INSENTIENT,
		//target 1-3, invulnerable time
		new Mapping()
		.addEntries(new MappingEntry(11, 17, ValueRemapperNumberToInt.INSTANCE))
		.addEntries(new MappingEntry(12, 18, ValueRemapperNumberToInt.INSTANCE))
		.addEntries(new MappingEntry(13, 19, ValueRemapperNumberToInt.INSTANCE))
		.addEntries(new MappingEntry(14, 20, ValueRemapperNumberToInt.INSTANCE))
		.addProtocols(ProtocolVersionsHelper.BEFORE_1_9)
	),
	GUARDIAN(EType.MOB, EntityType.GUARDIAN, SpecificType.INSENTIENT,
		//info flags(elder, spikes), target id
		new Mapping()
		.addEntries(new MappingEntry(11, 16, ValueRemapperNumberToInt.INSTANCE))
		.addEntries(new MappingEntry(12, 17, ValueRemapperNumberToInt.INSTANCE))
		.addProtocols(ProtocolVersion.MINECRAFT_1_8)
	),
	ARMOR_STAND_MOB(EType.MOB, EntityType.ARMOR_STAND, SpecificType.ARMOR_STAND),
	BOAT(EType.OBJECT, 1,
		//time since hit, forward direction
		new Mapping()
		.addEntries(new MappingEntry(5, 17, ValueRemapperNumberToInt.INSTANCE))
		.addEntries(new MappingEntry(6, 18, ValueRemapperNumberToInt.INSTANCE))
		.addProtocols(ProtocolVersionsHelper.BEFORE_1_9)
		.addProtocols(ProtocolVersion.MINECRAFT_PE),
		//damage taken
		new Mapping()
		.addEntries(new MappingEntry(7, 19))
		.addProtocols(ProtocolVersion.getAllBetween(ProtocolVersion.MINECRAFT_1_8, ProtocolVersion.MINECRAFT_1_6_1))
		.addProtocols(ProtocolVersion.MINECRAFT_PE),
		new Mapping()
		.addEntries(new MappingEntry(7, 19, ValueRemapperNumberToInt.INSTANCE))
		.addProtocols(ProtocolVersionsHelper.BEFORE_1_6)
	),
	TNT(EType.OBJECT, 50, SpecificType.ENTITY),
	SNOWBALL(EType.OBJECT, 61, SpecificType.ENTITY),
	EGG(EType.OBJECT, 62, SpecificType.ENTITY),
	FIREBALL(EType.OBJECT, 63, SpecificType.ENTITY),
	FIRECHARGE(EType.OBJECT, 64, SpecificType.ENTITY),
	ENDERPEARL(EType.OBJECT, 65, SpecificType.ENTITY),
	WITHER_SKULL(EType.OBJECT, 66, SpecificType.FIREBALL,
		//is charged
		new Mapping()
		.addEntries(new MappingEntry(5, 10, ValueRemapperBooleanToByte.INSTANCE))
		.addProtocols(ProtocolVersionsHelper.BEFORE_1_9)
	),
	FALLING_OBJECT(EType.OBJECT, 70, SpecificType.ENTITY),
	ENDEREYE(EType.OBJECT, 72, SpecificType.ENTITY),
	POTION(EType.OBJECT, 73, SpecificType.ENTITY),
	DRAGON_EGG(EType.OBJECT, 74, SpecificType.ENTITY),
	EXP_BOTTLE(EType.OBJECT, 75, SpecificType.ENTITY),
	LEASH_KNOT(EType.OBJECT, 77, SpecificType.ENTITY),
	FISHING_FLOAT(EType.OBJECT, 90, SpecificType.ENTITY),
	ITEM(EType.OBJECT, 2, SpecificType.ENTITY,
		//item
		new Mapping()
		.addEntries(new MappingEntry(5, 10))
		.addProtocols(ProtocolVersionsHelper.BEFORE_1_9)
	),
	MINECART(EType.OBJECT, 10, SpecificType.ENTITY,
		//shaking power, shaking direction, block y, show block
		new Mapping()
		.addEntries(new MappingEntry(5, 17, ValueRemapperNumberToInt.INSTANCE))
		.addEntries(new MappingEntry(6, 18, ValueRemapperNumberToInt.INSTANCE))
		.addEntries(new MappingEntry(9, 21, ValueRemapperNumberToInt.INSTANCE))
		.addEntries(new MappingEntry(10, 22, ValueRemapperBooleanToByte.INSTANCE))
		.addProtocols(ProtocolVersionsHelper.BEFORE_1_9)
		.addProtocols(ProtocolVersion.MINECRAFT_PE),
		//damage taken
		new Mapping()
		.addEntries(new MappingEntry(7, 19))
		.addProtocols(ProtocolVersion.getAllBetween(ProtocolVersion.MINECRAFT_1_8, ProtocolVersion.MINECRAFT_1_6_1))
		.addProtocols(ProtocolVersion.MINECRAFT_PE),
		new Mapping()
		.addEntries(new MappingEntry(7, 19, ValueRemapperNumberToInt.INSTANCE))
		.addProtocols(ProtocolVersionsHelper.BEFORE_1_6),
		//block
		new Mapping()
		.addEntries(new MappingEntry(8, 20, ValueRemapperNumberToInt.INSTANCE))
		.addProtocols(ProtocolVersion.MINECRAFT_1_8)
		.addProtocols(ProtocolVersion.MINECRAFT_PE),
		new Mapping()
		.addEntries(new MappingEntry(8, 20, new ValueRemapper<DataWatcherObjectVarInt>() {
			@Override
			public DataWatcherObject<?> remap(DataWatcherObjectVarInt object) {
				int value = object.getValue();
				int id = value & 0xFFFF;
				int data = value >> 12;
				return new DataWatcherObjectInt((data << 16) | id);
			}
		})).addProtocols(ProtocolVersionsHelper.BEFORE_1_6)
	),
	ARROW(EType.OBJECT, 60, SpecificType.ENTITY,
		//is critical
		new Mapping()
		.addEntries(new MappingEntry(5, 16))
		.addProtocols(ProtocolVersionsHelper.BEFORE_1_9)
		.addProtocols(ProtocolVersion.MINECRAFT_PE)
	),
	SPECTRAL_ARROW(EType.OBJECT, 91, SpecificType.ARROW),
	TIPPED_ARROW(EType.OBJECT, 92, SpecificType.ARROW),
	FIREWORK(EType.OBJECT, 76, SpecificType.ENTITY,
		//info
		new Mapping()
		.addEntries(new MappingEntry(5, 8))
		.addProtocols(ProtocolVersionsHelper.BEFORE_1_9)
	),
	ITEM_FRAME(EType.OBJECT, 71, SpecificType.ENTITY,
		//item, rotation
		new Mapping()
		.addEntries(new MappingEntry(5, 8))
		.addEntries(new MappingEntry(6, 9, ValueRemapperNumberToByte.INSTANCE))
		.addProtocols(ProtocolVersion.MINECRAFT_1_8),
		new Mapping()
		.addEntries(new MappingEntry(5, 2))
		.addEntries(new MappingEntry(6, 3, new ValueRemapper<DataWatcherObjectVarInt>() {
			@Override
			public DataWatcherObject<?> remap(DataWatcherObjectVarInt object) {
				return new DataWatcherObjectByte((byte) (object.getValue() >> 1));
			}
		})).addProtocols(ProtocolVersionsHelper.BEFORE_1_8)
	),
	ENDER_CRYSTAL(EType.OBJECT, 51, SpecificType.ENTITY),
	ARMOR_STAND_OBJECT(EType.OBJECT, 78, SpecificType.ARMOR_STAND),
	AREA_EFFECT_CLOUD(EType.OBJECT, 3, SpecificType.ENTITY),
	SHULKER_BULLET(EType.OBJECT, 67, SpecificType.ENTITY),
	DRAGON_FIREBALL(EType.OBJECT, 93, SpecificType.ENTITY);


	private static final SpecificType[] OBJECT_BY_TYPE_ID = new SpecificType[256];
	private static final SpecificType[] MOB_BY_TYPE_ID = new SpecificType[256];

	static {
		Arrays.fill(OBJECT_BY_TYPE_ID, SpecificType.NONE);
		Arrays.fill(MOB_BY_TYPE_ID, SpecificType.NONE);
		for (SpecificType type : values()) {
			switch (type.etype) {
				case OBJECT: {
					OBJECT_BY_TYPE_ID[type.typeId] = type;
					break;
				}
				case MOB: {
					MOB_BY_TYPE_ID[type.typeId] = type;
					break;
				}
				default: {
					break;
				}
			}
		}
	}

	public static SpecificType getObjectByTypeId(int objectTypeId) {
		return OBJECT_BY_TYPE_ID[objectTypeId];
	}

	public static SpecificType getMobByTypeId(int mobTypeId) {
		return MOB_BY_TYPE_ID[mobTypeId];
	}

	private final EType etype;
	private final int typeId;
	private final EnumMap<ProtocolVersion, ArrayList<MappingEntry>> entries = new EnumMap<ProtocolVersion, ArrayList<MappingEntry>>(ProtocolVersion.class);
	{
		for (ProtocolVersion version : ProtocolVersion.values()) {
			entries.put(version, new ArrayList<MappingEntry>());
		}
	}

	@SuppressWarnings("deprecation")
	SpecificType(EType etype, EntityType type, Mapping... entries) {
		this(etype, type.getTypeId(), entries);
	}

	SpecificType(EType etype, int typeId, Mapping... entries) {
		this.etype = etype;
		this.typeId = typeId;
		for (Mapping rp : entries) {
			for (ProtocolVersion version : rp.versions) {
				this.entries.get(version).addAll(rp.entries);
			}
		}
	}

	@SuppressWarnings("deprecation")
	SpecificType(EType etype, EntityType type, SpecificType superType, Mapping... entries) {
		this(etype, type.getTypeId(), superType, entries);
	}

	SpecificType(EType etype, int typeId, SpecificType superType, Mapping... entries) {
		this.etype = etype;
		this.typeId = typeId;
		for (Entry<ProtocolVersion, ArrayList<MappingEntry>> entry : superType.entries.entrySet()) {
			this.entries.get(entry.getKey()).addAll(entry.getValue());
		}
		for (Mapping rp : entries) {
			for (ProtocolVersion version : rp.versions) {
				this.entries.get(version).addAll(rp.entries);
			}
		}
	}

	public List<MappingEntry> getRemaps(ProtocolVersion version) {
		return entries.get(version);
	}

	private enum EType {
		NONE, OBJECT, MOB
	}

	private static class Mapping {
		private final ArrayList<ProtocolVersion> versions = new ArrayList<>();
		private final ArrayList<MappingEntry> entries = new ArrayList<>();
		protected Mapping(MappingEntry... entries) {
			this.entries.addAll(Arrays.asList(entries));
		}
		protected Mapping addEntries(MappingEntry... entries) {
			this.entries.addAll(Arrays.asList(entries));
			return this;
		}
		protected Mapping addProtocols(ProtocolVersion... versions) {
			this.versions.addAll(Arrays.asList(versions));
			return this;
		}
	}

}
