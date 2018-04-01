package selim.modjam.packs;

import java.util.List;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;

public class NBTUtils {

	public static String getKeyType(final NBTTagCompound nbt, final String key) {
		for (int i = 0; i < NBTBase.NBT_TYPES.length; i++) {
			if (nbt.hasKey(key, i)) {
				return NBTBase.NBT_TYPES[i];
			}
		}
		return null;
	}

	public static String getNBTBaseType(final NBTBase base) {
		for (int i = 0; i < NBTBase.NBT_TYPES.length; i++) {
			if (base.getId() == i) {
				return NBTBase.NBT_TYPES[i];
			}
		}
		return null;
	}

	public static void nbtToStringList(List<String> output, final NBTTagCompound nbt) {
		nbtToStringList(output, nbt, "", null);
	}

	public static void nbtToStringList(List<String> output, final NBTTagCompound nbt,
			final String name) {
		nbtToStringList(output, nbt, "", name);
	}

	private static void addNBTKey(List<String> output, final NBTTagCompound nbt, final String indent,
			final String key) {
		if (nbt == null)
			return;
		switch (getKeyType(nbt, key)) {
		case "END":
			break;
		case "BYTE":
			output.add(" " + indent + key + ": " + nbt.getByte(key) + "b,");
			break;
		case "SHORT":
			output.add(" " + indent + key + ": " + nbt.getShort(key) + "s,");
			break;
		case "INT":
			output.add(" " + indent + key + ": " + nbt.getInteger(key) + ",");
			break;
		case "LONG":
			output.add(" " + indent + key + ": " + nbt.getLong(key) + "L,");
			break;
		case "FLOAT":
			output.add(" " + indent + key + ": " + nbt.getFloat(key) + ",");
			break;
		case "DOUBLE":
			output.add(" " + indent + key + ": " + nbt.getDouble(key) + ",");
			break;
		case "BYTE[]":
			output.add(" " + indent + key + ": [");
			byte[] bytes = nbt.getByteArray(key);
			for (byte b : bytes) {
				output.add("  " + indent + b + ",");
			}
			output.add(" " + indent + "],");
			break;
		case "STRING":
			output.add(" " + indent + key + ": \"" + nbt.getString(key) + "\",");
			break;
		case "LIST":
			for (int i = 0; i < NBTBase.NBT_TYPES.length; i++) {
				NBTTagList tagList = nbt.getTagList(key, i);
				if (tagList != null && !tagList.hasNoTags()) {
					output.add(" " + indent + key + ": [");
					for (int ti = 0; ti < tagList.tagCount(); ti++) {
						addNBTBase(output, tagList.get(ti), "  " + indent, String.valueOf(ti));
					}
					output.add(" " + indent + "],");
					break;
				}
			}
			break;
		case "COMPOUND":
			nbtToStringList(output, nbt.getCompoundTag(key), " " + indent, key);
			break;
		case "INT[]":
			output.add(" " + indent + key + ": [");
			int[] ints = nbt.getIntArray(key);
			for (int i : ints) {
				output.add("  \"" + indent + i + "\",");
			}
			output.add(" " + indent + "],");
			break;
		}
	}

	private static void addNBTBase(List<String> output, final NBTBase base, final String indent,
			final String name) {
		switch (getNBTBaseType(base)) {
		case "END":
			break;
		case "BYTE":
			output.add(" " + indent + name + ": " + ((NBTTagByte) base).getByte() + "b,");
			break;
		case "SHORT":
			output.add(" " + indent + name + ": " + ((NBTTagShort) base).getShort() + "s,");
			break;
		case "INT":
			output.add(" " + indent + name + ": " + ((NBTTagInt) base).getInt() + ",");
			break;
		case "LONG":
			output.add(" " + indent + name + ": " + ((NBTTagLong) base).getLong() + "L,");
			break;
		case "FLOAT":
			output.add(" " + indent + name + ": " + ((NBTTagFloat) base).getFloat() + ",");
			break;
		case "DOUBLE":
			output.add(" " + indent + name + ": " + ((NBTTagDouble) base).getDouble() + ",");
			break;
		case "BYTE[]":
			output.add(" " + indent + name + ": [");
			byte[] bytes = ((NBTTagByteArray) base).getByteArray();
			for (byte b : bytes) {
				output.add("  " + indent + b + ",");
			}
			output.add(" " + indent + "],");
			break;
		case "STRING":
			output.add(" " + indent + name + ": \"" + ((NBTTagString) base).getString() + "\",");
			break;
		case "LIST":
			NBTTagList tagList = (NBTTagList) base;
			if (tagList != null) {
				output.add(" " + indent + name + ": [");
				for (int i = 0; i < tagList.tagCount(); i++) {
					addNBTBase(output, tagList.get(i), "  " + indent, String.valueOf(i));
				}
				output.add(" " + indent + "],");
			}
			break;
		case "COMPOUND":
			nbtToStringList(output, (NBTTagCompound) base, " " + indent, name);
			break;
		case "INT[]":
			output.add(" " + indent + name + ": [");
			int[] ints = ((NBTTagIntArray) base).getIntArray();
			for (int i : ints) {
				output.add("  " + indent + i);
			}
			output.add(" " + indent + "],");
			break;
		}
	}

	public static void nbtToStringList(List<String> output, final NBTTagCompound nbt,
			final String indent, final String name) {
		if (nbt == null)
			return;
		if (name != null && !name.equals(""))
			output.add(indent + name + ": {");
		else
			output.add(indent + "{");
		for (String key : nbt.getKeySet()) {
			addNBTKey(output, nbt, indent, key);
		}
		output.add(indent + "},");
	}
}
