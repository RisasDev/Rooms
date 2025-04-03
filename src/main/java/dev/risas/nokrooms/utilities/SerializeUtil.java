package dev.risas.nokrooms.utilities;

import dev.risas.nokrooms.utilities.cuboid.Cuboid;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public List<String> serializePotionEffects(List<PotionEffect> data) {
        if (data == null) return new ArrayList<>();
        return data.stream().collect(ArrayList::new, (list, effect) -> list.add(effect.getType().getName() + ":" + effect.getAmplifier()), ArrayList::addAll);
    }

    public List<PotionEffect> deserializePotionEffects(List<String> data) {
        if (data == null || data.isEmpty()) return new ArrayList<>();

        return data.stream().collect(ArrayList::new, (list, effect) -> {
            String[] splittedData = effect.split(":");
            list.add(new PotionEffect(Objects.requireNonNull(PotionEffectType.getByName(splittedData[0])), PotionEffect.INFINITE_DURATION, Integer.parseInt(splittedData[1])));
        }, ArrayList::addAll);
    }
}
