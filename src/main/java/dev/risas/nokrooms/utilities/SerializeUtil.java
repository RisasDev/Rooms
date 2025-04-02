package dev.risas.nokrooms.utilities;

import dev.risas.nokrooms.utilities.cuboid.Cuboid;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.World;

@UtilityClass
public class SerializeUtil {

    public String serializeCuboid(Cuboid cuboid) {
        if (cuboid == null) return null;
        return cuboid.getWorld().getName() + ":" +
                cuboid.getX1() + ":" +
                cuboid.getY1() + ":" +
                cuboid.getZ1() + ":" +
                cuboid.getX2() + ":" +
                cuboid.getY2() + ":" +
                cuboid.getZ2();
    }

    public Cuboid deserializeCuboid(String data) {
        if (data == null) return null;

        String[] splittedData = data.split(":");

        if (splittedData.length < 7) return null;

        World world = Bukkit.getWorld(splittedData[0]);
        if (world == null) return null;

        int x1 = Integer.parseInt(splittedData[1]);
        int y1 = Integer.parseInt(splittedData[2]);
        int z1 = Integer.parseInt(splittedData[3]);
        int x2 = Integer.parseInt(splittedData[4]);
        int y2 = Integer.parseInt(splittedData[5]);
        int z2 = Integer.parseInt(splittedData[6]);

        return new Cuboid(world, x1, y1, z1, x2, y2, z2);
    }
}
