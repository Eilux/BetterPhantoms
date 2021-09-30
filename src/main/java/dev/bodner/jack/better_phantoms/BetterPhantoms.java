package dev.bodner.jack.better_phantoms;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftLivingEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.Random;

public final class BetterPhantoms extends JavaPlugin implements Listener {
    ArrayList<World> worldList = new ArrayList<>();

    @Override
    public void onEnable() {
        for (World world : Bukkit.getWorlds()){
            if (world.isNatural()){
                worldList.add(world);
            }
        }

        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, () -> {
            for (World world : worldList){
//                if (world.getTime() > 0 && world.getTime() < 12300) {
                ServerLevel level = ((CraftWorld)world).getHandle();
                if (!(level.getSkyDarken() < 5 && world.hasSkyLight())) {
                    for (LivingEntity entity : world.getLivingEntities()){
                        net.minecraft.world.entity.LivingEntity livingEntity = ((CraftLivingEntity)entity).getHandle();
                        if (entity.hasPotionEffect(PotionEffectType.GLOWING)){
                            Random random = new Random();
                            int val = random.nextInt(35);
                            if (val == 1){
                                Location location = entity.getLocation();
//                                if (location.getY() > world.getSeaLevel() && world.getHighestBlockYAt(location) > world.getSeaLevel()){
                                if (livingEntity.blockPosition().getY() > world.getSeaLevel() || level.canSeeSky(livingEntity.blockPosition())){
                                    Location phantomLocation = location.add(-10 + random.nextInt(21),20 + random.nextInt(15), -10 + random.nextInt(21));

//                                    Phantom entityphantom = net.minecraft.world.entity.EntityType.PHANTOM.create(level);
                                    NewPhantom entityphantom = new NewPhantom(net.minecraft.world.entity.EntityType.PHANTOM, level);
                                    entityphantom.moveTo(phantomLocation.getX() ,phantomLocation.getY(), phantomLocation.getZ());
                                    entityphantom.finalizeSpawn(level, level.getCurrentDifficultyAt(new BlockPos(location.getBlockX(),location.getBlockY(),location.getBlockZ())), MobSpawnType.NATURAL, null, null);
                                    level.addAllEntities(entityphantom, CreatureSpawnEvent.SpawnReason.NATURAL);
//                                    entityphantom.setTarget(((CraftLivingEntity)entity).getHandle());
                                }
                            }
                        }
                    }
                }
            }
        },0, 1);
    }



    @Override
    public void onDisable() {
    }

}
