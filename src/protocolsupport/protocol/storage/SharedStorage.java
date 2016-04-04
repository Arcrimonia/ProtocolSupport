package protocolsupport.protocol.storage;

public class SharedStorage {

	private double x;
	private double y;
	private double z;
	private int teleportConfirmId;

	public boolean isTeleportConfirmNeeded() {
		return teleportConfirmId != -1;
	}

	public int tryTeleportConfirm(double x, double y, double z) {
		if (
			Double.doubleToLongBits(this.x) == Double.doubleToLongBits(x) &&
			Double.doubleToLongBits(this.y) == Double.doubleToLongBits(y) &&
			Double.doubleToLongBits(this.z) == Double.doubleToLongBits(z)
		) {
			int r = teleportConfirmId;
			teleportConfirmId = -1;
			return r;
		}
		return -1;
	}

	public int tryTeleportConfirmPE(double x, double y, double z) {
		if (
			Math.abs(this.x - x) < 0.2 &&
			Math.abs(this.y - y) < 0.2 &&
			Math.abs(this.z - z) < 0.2
		) {
			int r = teleportConfirmId;
			teleportConfirmId = -1;
			return r;
		}
		return -1;
	}

	public void setTeleportLocation(double x, double y, double z, int teleportConfirmId) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.teleportConfirmId = teleportConfirmId;
	}

}
